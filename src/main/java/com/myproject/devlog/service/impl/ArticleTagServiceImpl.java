package com.myproject.devlog.service.impl;

import com.myproject.devlog.mapper.ArticleTagMapper;
import com.myproject.devlog.mapper.TagMapper;
import com.myproject.devlog.pojo.entity.ArticleTag;
import com.myproject.devlog.pojo.entity.Tag;
import com.myproject.devlog.pojo.vo.ArticleTagQueryVO;
import com.myproject.devlog.pojo.vo.TagVO;
import com.myproject.devlog.service.ArticleTagService;
import com.myproject.devlog.utils.TagConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ArticleTagServiceImpl implements ArticleTagService {
    private final ArticleTagMapper articleTagMapper;
    private final TagMapper tagMapper;

    public ArticleTagServiceImpl(ArticleTagMapper articleTagMapper, TagMapper tagMapper) {
        this.articleTagMapper = articleTagMapper;
        this.tagMapper = tagMapper;
    }

    @Override
    @Transactional
    public void replaceArticleTags(Long articleId, List<Long> tagIds) {
        List<Long> normalizedTagIds = tagIds == null
                ? List.of()
                : tagIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (!normalizedTagIds.isEmpty()
                && tagMapper.countByIds(normalizedTagIds) != normalizedTagIds.size()) {
            throw new RuntimeException("有不存在的标签");
        }

        articleTagMapper.deleteByArticleId(articleId);

        if (normalizedTagIds.isEmpty()) {
            return;
        }

        List<ArticleTag> relations = normalizedTagIds.stream()
                .map(tagId -> {
                    ArticleTag relation = new ArticleTag();
                    relation.setArticleId(articleId);
                    relation.setTagId(tagId);
                    return relation;
                })
                .toList();

        articleTagMapper.batchInsert(relations);
    }

    @Override
    public List<TagVO> listByArticleId(Long articleId) {
        if (articleId == null) {
            return List.of();
        }

        List<Tag> tags = articleTagMapper.listByArticleId(articleId);

        if (tags == null || tags.isEmpty()) {
            return List.of();
        }

        return tags.stream()
                .map(TagConverter::toVO)
                .toList();
    }

    @Override
    public Map<Long, List<TagVO>> listByArticleIds(List<Long> articleIds) {
        Map<Long, List<TagVO>> result = new LinkedHashMap<>();
        if (articleIds == null || articleIds.isEmpty()) return result;

        List<Long> distinctIds = articleIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (distinctIds.isEmpty()) return result;

        List<ArticleTagQueryVO> rows = articleTagMapper.listTagsByArticleIds(distinctIds);
        if (rows == null || rows.isEmpty()) return result;

        for (ArticleTagQueryVO row : rows) {
            result.computeIfAbsent(row.getArticleId(), ignored -> new ArrayList<>())
                    .add(TagConverter.toVO(row.getTagId(), row.getTagName()));
        }
        return result;
    }

    @Override
    public void deleteByArticleId(Long articleId) {
        if (articleId != null) articleTagMapper.deleteByArticleId(articleId);
    }
}
