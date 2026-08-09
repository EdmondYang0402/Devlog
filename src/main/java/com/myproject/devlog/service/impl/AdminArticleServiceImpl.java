package com.myproject.devlog.service.impl;

import com.myproject.devlog.common.PageResult;
import com.myproject.devlog.common.BusinessException;
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
import org.springframework.http.HttpStatus;
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
        Long currentUserId = requireCurrentUserId();
        User user = userMapper.selectById(currentUserId);
        Article article = fromCreateDTO(dto, currentUserId);
        PermissionUtil.checkUser(user);
        if (articleMapper.insert(article) != 1 || article.getId() == null) {
            throw new IllegalStateException("文章创建未返回预期写入结果");
        }
        articleTagService.replaceArticleTags(article.getId(), dto.getTagIds());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ArticleUpdateDTO dto) {
        Long currentUserId = requireCurrentUserId();
        User user = userMapper.selectById(currentUserId);
        Article article = articleMapper.selectById(dto.getId());
        PermissionUtil.checkArticlePermission(user, article, currentUserId);
        Article updatedArticle = fromUpdateDTO(dto);
        if (updatedArticle.getStatus() == null) {
            updatedArticle.setStatus(article.getStatus());
        }
        if (articleMapper.updateById(updatedArticle) != 1) {
            throw new IllegalStateException("文章更新未影响预期记录数");
        }
        articleTagService.replaceArticleTags(dto.getId(), dto.getTagIds());
    }

    @Override
    public ArticleDetailVO getById(Long id) {
        Long currentUserId = requireCurrentUserId();
        User user = userMapper.selectById(currentUserId);
        Article article = articleMapper.selectById(id);
        PermissionUtil.checkArticlePermission(user, article, currentUserId);
        ArticleDetailVO detail = toDetailVO(article);
        detail.setTags(articleTagService.listByArticleId(id));
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Long currentUserId = requireCurrentUserId();
        User user = userMapper.selectById(currentUserId);
        Article article = articleMapper.selectById(id);
        PermissionUtil.checkArticlePermission(user, article, currentUserId);
        commentMapper.deleteByArticleId(id);
        articleTagService.deleteByArticleId(id);
        if (articleMapper.deleteById(id) != 1) {
            throw new IllegalStateException("文章删除未影响预期记录数");
        }
    }


    public PageResult<ArticleListVO> page(Integer page, Integer size, String title, Integer status) {
        if (page == null || page < 1 || size == null || size < 1 || size > 100
                || (long) (page - 1) * size > Integer.MAX_VALUE) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "分页参数不合法");
        }
        if (status != null) validateStatus(status);
        int offset = (page - 1) * size;
        List<ArticleListVO> records = articleMapper.selectAdminPage(offset, size, title, status);
        attachTags(records);
        return new PageResult<>(records, articleMapper.countAdmin(title, status));
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
            throw new BusinessException(HttpStatus.BAD_REQUEST, "文章状态只能是草稿或已发布");
        }
    }

    private Long requireCurrentUserId() {
        Long currentUserId = UserContext.get();
        if (currentUserId == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "未登录");
        }
        return currentUserId;
    }
}
