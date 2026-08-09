package com.myproject.devlog.service.impl;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.common.UserContext;
import com.myproject.devlog.mapper.ArticleMapper;
import com.myproject.devlog.mapper.CommentMapper;
import com.myproject.devlog.mapper.UserMapper;
import com.myproject.devlog.pojo.dto.CommentCreateDTO;
import com.myproject.devlog.pojo.entity.Comment;
import com.myproject.devlog.pojo.entity.User;
import com.myproject.devlog.pojo.vo.CommentVO;
import com.myproject.devlog.service.CommentService;
import com.myproject.devlog.utils.CommentConverter;
import com.myproject.devlog.utils.PermissionUtil;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final ArticleMapper articleMapper;
    private final UserMapper userMapper;

    public CommentServiceImpl(CommentMapper commentMapper, ArticleMapper articleMapper, UserMapper userMapper) {
        this.commentMapper = commentMapper;
        this.articleMapper = articleMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(CommentCreateDTO dto) {
        Long currentUserId = requireCurrentUserId();
        User currentUser = requireUser(currentUserId);
        PermissionUtil.checkUser(currentUser);
        if (articleMapper.selectById(dto.getArticleId()) == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "文章不存在");
        }

        Comment comment = CommentConverter.fromCreateDTO(dto, currentUserId);

        if (dto.getParentId() != null) {
            applyReplyTarget(comment, dto.getParentId(), dto.getArticleId());
        }

        if (commentMapper.insert(comment) != 1) {
            throw new IllegalStateException("评论创建未影响预期记录数");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Long currentUserId = requireCurrentUserId();
        User currentUser = requireUser(currentUserId);
        Comment comment = commentMapper.selectById(id);
        PermissionUtil.checkUser(currentUser);

        if (comment == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "评论不存在");
        }
        if (!PermissionUtil.isAdmin(currentUser)
                && !PermissionUtil.isOwner(currentUserId, comment.getUserId())) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "无权删除该评论");
        }
        if (commentMapper.deleteById(id) == 0) {
            throw new IllegalStateException("评论删除未影响任何记录");
        }
    }

    @Override
    public List<CommentVO> listByArticleId(Long articleId) {
        if (articleId == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "文章 ID 不能为空");
        }

        List<Comment> comments = commentMapper.selectByArticleId(articleId);
        Map<Long, User> users = loadUsers(comments);
        Map<Long, CommentVO> roots = new LinkedHashMap<>();

        for (Comment comment : comments) {
            if (comment.getParentId() == null) {
                roots.put(comment.getId(), CommentConverter.toVO(
                        comment,
                        users.get(comment.getUserId()),
                        users.get(comment.getReplyUserId())));
            }
        }
        for (Comment comment : comments) {
            if (comment.getParentId() != null) {
                CommentVO root = roots.get(comment.getParentId());
                if (root != null) {
                    root.getReplies().add(CommentConverter.toVO(
                            comment,
                            users.get(comment.getUserId()),
                            users.get(comment.getReplyUserId())));
                }
            }
        }
        return new ArrayList<>(roots.values());
    }

    private void applyReplyTarget(Comment comment, Long targetCommentId, Long articleId) {
        Comment target = commentMapper.selectById(targetCommentId);
        if (target == null || !articleId.equals(target.getArticleId())) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "被回复的评论不存在");
        }

        Long rootId = target.getParentId() == null ? target.getId() : target.getParentId();
        if (target.getParentId() != null) {
            Comment root = commentMapper.selectById(rootId);
            if (root == null || root.getParentId() != null || !articleId.equals(root.getArticleId())) {
                throw new IllegalStateException("评论层级数据异常");
            }
        }

        comment.setParentId(rootId);
        comment.setReplyUserId(target.getUserId());
    }

    private Map<Long, User> loadUsers(List<Comment> comments) {
        Map<Long, User> users = new HashMap<>();
        for (Comment comment : comments) {
            loadUser(users, comment.getUserId());
            loadUser(users, comment.getReplyUserId());
        }
        return users;
    }

    private void loadUser(Map<Long, User> users, Long userId) {
        if (userId != null && !users.containsKey(userId)) {
            users.put(userId, userMapper.selectById(userId));
        }
    }

    private Long requireCurrentUserId() {
        Long currentUserId = UserContext.get();
        if (currentUserId == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "未登录");
        }
        return currentUserId;
    }

    private User requireUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        return user;
    }
}
