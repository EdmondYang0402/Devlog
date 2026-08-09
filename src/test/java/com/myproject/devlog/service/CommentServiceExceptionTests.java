package com.myproject.devlog.service;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.common.UserContext;
import com.myproject.devlog.mapper.ArticleMapper;
import com.myproject.devlog.mapper.CommentMapper;
import com.myproject.devlog.mapper.UserMapper;
import com.myproject.devlog.pojo.entity.Comment;
import com.myproject.devlog.pojo.entity.User;
import com.myproject.devlog.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommentServiceExceptionTests {
    private CommentMapper commentMapper;
    private UserMapper userMapper;
    private CommentServiceImpl service;

    @BeforeEach
    void setUp() {
        commentMapper = mock(CommentMapper.class);
        userMapper = mock(UserMapper.class);
        service = new CommentServiceImpl(commentMapper, mock(ArticleMapper.class), userMapper);
    }

    @AfterEach
    void clearUserContext() {
        UserContext.clear();
    }

    @Test
    void deleteWithoutAuthenticatedIdentityUsesUnauthorized() {
        BusinessException exception = assertThrows(BusinessException.class, () -> service.delete(1L));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    void deletingMissingCommentUsesNotFound() {
        authenticate(7L, 0);
        when(commentMapper.selectById(9L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> service.delete(9L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void deletingAnotherUsersCommentUsesForbidden() {
        authenticate(7L, 0);
        Comment comment = new Comment();
        comment.setId(9L);
        comment.setUserId(8L);
        when(commentMapper.selectById(9L)).thenReturn(comment);

        BusinessException exception = assertThrows(BusinessException.class, () -> service.delete(9L));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void nullArticleIdUsesBadRequest() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.listByArticleId(null));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    private void authenticate(Long userId, Integer role) {
        UserContext.set(userId);
        User user = new User();
        user.setId(userId);
        user.setRole(role);
        user.setStatus(0);
        when(userMapper.selectById(userId)).thenReturn(user);
    }
}
