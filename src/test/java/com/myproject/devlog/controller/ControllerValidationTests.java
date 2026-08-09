package com.myproject.devlog.controller;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.common.GlobalExceptionHandler;
import com.myproject.devlog.pojo.dto.MediaReviewCreateDTO;
import com.myproject.devlog.service.AdminArticleService;
import com.myproject.devlog.service.CommentService;
import com.myproject.devlog.service.MediaReviewService;
import com.myproject.devlog.service.ProjectService;
import com.myproject.devlog.service.SiteBackgroundService;
import com.myproject.devlog.service.SiteConfigService;
import com.myproject.devlog.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ControllerValidationTests {

    @Test
    void loginBeanValidationReturns400WithoutCallingService() throws Exception {
        UserService service = mock(UserService.class);
        MockMvc mvc = mvc(new UserController(service));

        mvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        verifyNoInteractions(service);
    }

    @Test
    void invalidRegistrationEmailReturns400WithoutCallingService() throws Exception {
        UserService service = mock(UserService.class);
        MockMvc mvc = mvc(new UserController(service));

        mvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"secret\",\"email\":\"not-an-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        verifyNoInteractions(service);
    }

    @Test
    void blankArticleTitleReturns400WithoutCallingService() throws Exception {
        AdminArticleService service = mock(AdminArticleService.class);
        MockMvc mvc = mvc(new AdminArticleController(service));

        mvc.perform(post("/admin/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"   \",\"status\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        verifyNoInteractions(service);
    }

    @Test
    void overlongCommentReturns400WithoutCallingService() throws Exception {
        CommentService service = mock(CommentService.class);
        MockMvc mvc = mvc(new CommentController(service));
        String body = "{\"articleId\":1,\"content\":\"" + "a".repeat(501) + "\"}";

        mvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        verifyNoInteractions(service);
    }

    @Test
    void invalidSiteKeywordElementReturns400WithoutCallingService() throws Exception {
        SiteConfigService service = mock(SiteConfigService.class);
        MockMvc mvc = mvc(new AdminSiteConfigController(service));

        mvc.perform(put("/admin/site/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"siteTitle\":\"DevLog\",\"authorName\":\"作者\",\"heroKeywords\":[\"   \"]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        verifyNoInteractions(service);
    }

    @Test
    void projectBodyValidationReturns400WithoutCallingService() throws Exception {
        ProjectService service = mock(ProjectService.class);
        MockMvc mvc = mvc(new AdminProjectController(service));

        mvc.perform(post("/admin/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"   \",\"summary\":\"简介\",\"status\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        verifyNoInteractions(service);
    }

    @Test
    void mediaReviewValidationReturns400AndValidRequestDelegates() throws Exception {
        MediaReviewService service = mock(MediaReviewService.class);
        MockMvc mvc = mvc(new AdminMediaReviewController(service));

        mvc.perform(post("/admin/media-reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"作品\",\"mediaType\":2,\"status\":2,\"rating\":11}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
        verifyNoInteractions(service);

        mvc.perform(post("/admin/media-reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"作品\",\"mediaType\":2,\"status\":2,\"rating\":9}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(service).create(any(MediaReviewCreateDTO.class));
    }

    @Test
    void modelAttributeValidationReturns400WithoutCallingService() throws Exception {
        SiteBackgroundService service = mock(SiteBackgroundService.class);
        MockMvc mvc = mvc(new AdminSiteBackgroundController(service));

        mvc.perform(get("/admin/site-backgrounds").param("page", "0").param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        verifyNoInteractions(service);
    }

    @Test
    void serviceBusinessExceptionIsHandledGloballyInsteadOfByController() throws Exception {
        UserService service = mock(UserService.class);
        doThrow(new BusinessException(HttpStatus.CONFLICT, "用户名已存在"))
                .when(service).register(any());
        MockMvc mvc = mvc(new UserController(service));

        mvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"secret\",\"email\":\"alice@example.com\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("用户名已存在"));
    }

    private MockMvc mvc(Object controller) {
        return MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }
}
