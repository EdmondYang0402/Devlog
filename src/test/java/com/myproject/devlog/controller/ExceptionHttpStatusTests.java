package com.myproject.devlog.controller;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.common.GlobalExceptionHandler;
import com.myproject.devlog.service.ArticleService;
import com.myproject.devlog.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExceptionHttpStatusTests {
    private ArticleService articleService;
    private CommentService commentService;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        articleService = mock(ArticleService.class);
        commentService = mock(CommentService.class);
        mvc = MockMvcBuilders.standaloneSetup(
                        new ArticleController(articleService),
                        new CommentController(commentService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void missingArticleUsesHttp404AndMatchingBusinessCode() throws Exception {
        when(articleService.getFrontDetail(99L))
                .thenThrow(new BusinessException(HttpStatus.NOT_FOUND, "文章不存在或未发布"));

        mvc.perform(get("/articles/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("文章不存在或未发布"));
    }

    @Test
    void beanValidationFailureUsesHttp400WithoutCallingService() throws Exception {
        mvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"articleId\":1,\"content\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        verifyNoInteractions(commentService);
    }

    @Test
    void missingCommentUsesHttp404() throws Exception {
        doThrow(new BusinessException(HttpStatus.NOT_FOUND, "评论不存在"))
                .when(commentService).delete(7L);

        mvc.perform(delete("/comments/7"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void unknownExceptionUsesSanitizedHttp500() throws Exception {
        when(commentService.listByArticleId(1L))
                .thenThrow(new IllegalStateException("filesystem C:\\secret leaked"));

        mvc.perform(get("/comments/articles/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("服务器内部错误"));
    }

    @Test
    void emptyListRemainsHttp200() throws Exception {
        when(commentService.listByArticleId(1L)).thenReturn(List.of());

        mvc.perform(get("/comments/articles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
