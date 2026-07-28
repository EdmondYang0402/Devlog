package com.myproject.devlog.service.impl;

import com.myproject.devlog.common.PageResult;
import com.myproject.devlog.common.UserContext;
import com.myproject.devlog.mapper.ArticleMapper;
import com.myproject.devlog.mapper.CommentMapper;
import com.myproject.devlog.mapper.UserMapper;
import com.myproject.devlog.pojo.dto.ArticleCreateDTO;
import com.myproject.devlog.pojo.dto.ArticleUpdateDTO;
import com.myproject.devlog.pojo.entity.Article;
import com.myproject.devlog.pojo.entity.User;
import com.myproject.devlog.pojo.vo.ArticleListVO;
import com.myproject.devlog.pojo.vo.ArticleDetailVO;
import com.myproject.devlog.pojo.vo.TagVO;
import com.myproject.devlog.service.AdminArticleService;
import com.myproject.devlog.service.ArticleTagService;
import com.myproject.devlog.utils.PermissionUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.myproject.devlog.utils.ArticleConverter.fromCreateDTO;
import static com.myproject.devlog.utils.ArticleConverter.fromUpdateDTO;
import static com.myproject.devlog.utils.ArticleConverter.toDetailVO;
import static com.myproject.devlog.common.ArticleStatusConstant.isValid;

@Service
public class AdminArticleServiceImpl implements AdminArticleService {
    private final UserMapper userMapper;
    private final ArticleMapper articleMapper;
    private final ArticleTagService articleTagService;
    private final CommentMapper commentMapper;

    public AdminArticleServiceImpl(UserMapper userMapper, ArticleMapper articleMapper,
                                   ArticleTagService articleTagService, CommentMapper commentMapper) {
        this.userMapper = userMapper;
        this.articleMapper = articleMapper;
        this.articleTagService = articleTagService;
        this.commentMapper = commentMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(ArticleCreateDTO dto) {
        validateStatus(dto.getStatus());
        Long currentUserId = UserContext.get();
        User user = userMapper.getById(currentUserId);
        Article article = fromCreateDTO(dto, currentUserId);
        PermissionUtil.checkUser(user);
        articleMapper.insert(article);
        articleTagService.replaceArticleTags(article.getId(), dto.getTagIds());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ArticleUpdateDTO dto) {
        validateStatus(dto.getStatus());
        Long currentUserId = UserContext.get();
        User user = userMapper.getById(currentUserId);
        Article article = articleMapper.selectById(dto.getId());
        PermissionUtil.checkArticlePermission(user, article, currentUserId);
        Article updatedArticle = fromUpdateDTO(dto);
        if (updatedArticle.getStatus() == null) {
            updatedArticle.setStatus(article.getStatus());
        }
        articleMapper.update(updatedArticle);
        articleTagService.replaceArticleTags(dto.getId(), dto.getTagIds());
    }

    @Override
    public ArticleDetailVO getDetail(Long id) {
        Long currentUserId = UserContext.get();
        User user = userMapper.getById(currentUserId);
        Article article = articleMapper.selectById(id);
        PermissionUtil.checkArticlePermission(user, article, currentUserId);
        ArticleDetailVO detail = toDetailVO(article);
        detail.setTags(articleTagService.listByArticleId(id));
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Long currentUserId = UserContext.get();
        User user = userMapper.getById(currentUserId);
        Article article = articleMapper.selectById(id);
        PermissionUtil.checkArticlePermission(user, article, currentUserId);
        commentMapper.deleteByArticleId(id);
        articleTagService.deleteByArticleId(id);
        articleMapper.deleteById(id);
    }


    public PageResult<ArticleListVO> page(Integer page, Integer size, String title, Integer status) {
        if (status != null) validateStatus(status);
        int offset = (page - 1) * size;
        List<ArticleListVO> records = articleMapper.adminPage(offset, size, title, status);
        attachTags(records);
        return new PageResult<>(records, articleMapper.adminCount(title, status));
    }

    private void attachTags(List<ArticleListVO> records) {
        List<Long> articleIds = records.stream()
                .map(ArticleListVO::getId)
                .toList();

        Map<Long, List<TagVO>> tagsByArticleId =
                articleTagService.listByArticleIds(articleIds);

        records.forEach(record ->
                record.setTags(
                        tagsByArticleId.getOrDefault(
                                record.getId(),
                                List.of()
                        )
                )
        );
    }

    private void validateStatus(Integer status) {
        if (status != null && !isValid(status)) {
            throw new RuntimeException("文章状态只能是草稿或已发布");
        }
    }
}
