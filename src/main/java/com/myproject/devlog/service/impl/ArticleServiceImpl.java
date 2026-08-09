package com.myproject.devlog.service.impl;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.common.PageResult;
import com.myproject.devlog.mapper.ArticleMapper;
import com.myproject.devlog.pojo.entity.Article;
import com.myproject.devlog.pojo.vo.ArticleDetailVO;
import com.myproject.devlog.pojo.vo.ArticleListVO;
import com.myproject.devlog.pojo.vo.TagVO;
import com.myproject.devlog.service.ArticleService;
import com.myproject.devlog.service.ArticleTagService;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.myproject.devlog.utils.ArticleConverter.toDetailVO;

@Service
public class ArticleServiceImpl implements ArticleService {
    private static final String NOTES_CATEGORY_SLUG = "notes";
    private static final String NOTES_CATEGORY_NAME = "手记";

    private final ArticleMapper articleMapper;
    private final ArticleTagService articleTagService;

    public ArticleServiceImpl(ArticleMapper articleMapper, ArticleTagService articleTagService) {
        this.articleMapper = articleMapper;
        this.articleTagService = articleTagService;
    }

    @Override
    public ArticleDetailVO getById(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "文章不存在");
        }
        return toDetailVO(article);
    }

    @Override
    public void increaseViewCount(Long id) {
        if (articleMapper.updateViewCount(id) == 0) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "文章不存在或未发布");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleDetailVO getFrontDetail(Long id) {
        ArticleDetailVO detail = articleMapper.selectFrontDetailById(id);
        if (detail == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "文章不存在或未发布");
        }
        articleMapper.updateViewCount(id);
        detail.setViewCount(detail.getViewCount() == null ? 1L : detail.getViewCount() + 1);
        detail.setTags(articleTagService.listByArticleId(id));
        return detail;
    }

    @Override
    public PageResult<ArticleListVO> page(Integer page, Integer size, Long categoryId,
                                                  String categorySlug, String keyword,
                                                  List<Long> tagIds) {
        if (page == null || page < 1 || size == null || size < 1 || size > 100
                || (long) (page - 1) * size > Integer.MAX_VALUE) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "分页参数不合法");
        }
        Integer offset = (page - 1) * size;
        String categoryName = resolveCategoryName(categorySlug);
        String normalizedKeyword = normalizeKeyword(keyword);
        List<Long> normalizedTagIds = normalizeTagIds(tagIds);
        PageResult<ArticleListVO> result = queryFrontArticles(
                offset, size, categoryId, categoryName, normalizedKeyword, normalizedTagIds
        );
        attachTags(result.getRecords());
        return result;
    }

    private String resolveCategoryName(String categorySlug) {
        if (categorySlug == null || categorySlug.isBlank()) {
            return null;
        }
        String normalizedSlug = categorySlug.trim();
        // category 表没有 slug；将公开稳定标识 notes 映射到唯一分类名，避免依赖各环境不同的 categoryId。
        return NOTES_CATEGORY_SLUG.equalsIgnoreCase(normalizedSlug)
                ? NOTES_CATEGORY_NAME
                : normalizedSlug;
    }

    private String normalizeKeyword(String keyword) {
        return keyword == null || keyword.isBlank() ? null : keyword.trim();
    }

    private List<Long> normalizeTagIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return List.of();
        }
        return tagIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private PageResult<ArticleListVO> queryFrontArticles(Integer offset, Integer size, Long categoryId,
                                                          String categoryName, String keyword,
                                                          List<Long> tagIds) {
        List<ArticleListVO> records;
        Integer total;
        if (tagIds.isEmpty()) {
            records = articleMapper.selectFrontPage(offset, size, categoryId, categoryName, keyword);
            // records 和 count 必须使用同一筛选条件，否则分页总数会与当前列表不一致。
            total = articleMapper.countFront(categoryId, categoryName, keyword);
        } else {
            records = articleMapper.selectFrontPageByTagIds(
                    offset, size, categoryId, categoryName, keyword, tagIds, tagIds.size()
            );
            total = articleMapper.countFrontByTagIds(
                    categoryId, categoryName, keyword, tagIds, tagIds.size()
            );
        }
        return new PageResult<>(records, total);
    }

    private void attachTags(List<ArticleListVO> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        List<Long> articleIds = records.stream().map(ArticleListVO::getId).toList();
        Map<Long, List<TagVO>> tagsByArticleId =
                articleTagService.listByArticleIds(articleIds);
        records.forEach(record -> record.setTags(
                tagsByArticleId.getOrDefault(record.getId(), List.of())
        ));
    }
}
