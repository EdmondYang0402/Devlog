package com.myproject.devlog.interceptor;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.common.UserContext;
import com.myproject.devlog.service.LoginSessionService;
import com.myproject.devlog.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtInterceptorTests {

    private final LoginSessionService loginSessionService = mock(LoginSessionService.class);
    private final JwtInterceptor interceptor = new JwtInterceptor(loginSessionService);
    private final HttpServletResponse response = mock(HttpServletResponse.class);

    @AfterEach
    void cleanContext() {
        UserContext.clear();
    }

    @Test
    void validJwtAndRedisSessionSetAndClearUserContext() {
        String sessionId = "active-session";
        String token = JwtUtil.generateToken(7L, sessionId);
        HttpServletRequest request = requestWithToken(token);
        when(loginSessionService.getUserId(sessionId)).thenReturn(7L);

        assertTrue(interceptor.preHandle(request, response, new Object()));
        assertEquals(7L, UserContext.get());

        interceptor.afterCompletion(request, response, new Object(), null);
        assertNull(UserContext.get());
    }

    @Test
    void jwtIsRejectedImmediatelyWhenRedisSessionIsGone() {
        String sessionId = "logged-out-session";
        String token = JwtUtil.generateToken(7L, sessionId);
        when(loginSessionService.getUserId(sessionId)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> interceptor.preHandle(requestWithToken(token), response, new Object()));

        assertEquals(401, exception.getStatus().value());
        assertEquals("登录已失效，请重新登录", exception.getMessage());
    }

    @Test
    void mismatchedRedisUserIsRejected() {
        String sessionId = "mismatched-session";
        String token = JwtUtil.generateToken(7L, sessionId);
        when(loginSessionService.getUserId(sessionId)).thenReturn(8L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> interceptor.preHandle(requestWithToken(token), response, new Object()));

        assertEquals(401, exception.getStatus().value());
        assertEquals("登录信息异常", exception.getMessage());
    }

    @Test
    void projectFrontGetRoutesArePublicButWritesAndAdminRoutesRemainProtected() {
        assertTrue(interceptor.preHandle(projectRequest("GET", "/projects"), response, new Object()));
        assertTrue(interceptor.preHandle(projectRequest("GET", "/projects/12"), response, new Object()));
        assertTrue(interceptor.preHandle(projectRequest("GET", "/projects/featured"), response, new Object()));

        assertEquals(401, assertThrows(BusinessException.class,
                () -> interceptor.preHandle(projectRequest("POST", "/projects"), response, new Object()))
                .getStatus().value());

        assertEquals(401, assertThrows(BusinessException.class,
                () -> interceptor.preHandle(projectRequest("GET", "/admin/projects"), response, new Object()))
                .getStatus().value());
        assertEquals(401, assertThrows(BusinessException.class,
                () -> interceptor.preHandle(projectRequest("GET", "/projects/internal/stats"), response, new Object()))
                .getStatus().value());
    }

    @Test
    void siteBackgroundGetIsPublicButWritesAndAdminRoutesRemainProtected() {
        assertTrue(interceptor.preHandle(projectRequest("GET", "/site-backgrounds"), response, new Object()));
        assertEquals(401, assertThrows(BusinessException.class,
                () -> interceptor.preHandle(projectRequest("POST", "/site-backgrounds"), response, new Object()))
                .getStatus().value());
        assertEquals(401, assertThrows(BusinessException.class,
                () -> interceptor.preHandle(projectRequest("GET", "/site-backgrounds/1"), response, new Object()))
                .getStatus().value());
        assertEquals(401, assertThrows(BusinessException.class,
                () -> interceptor.preHandle(projectRequest("GET", "/admin/site-backgrounds"), response, new Object()))
                .getStatus().value());
    }

    @Test
    void optionsLoginAndRegisterAreAnonymousButOtherPostsRemainProtected() {
        assertTrue(interceptor.preHandle(
                projectRequest("OPTIONS", "/anything"), response, new Object()));
        assertTrue(interceptor.preHandle(
                projectRequest("POST", "/users/login"), response, new Object()));
        assertTrue(interceptor.preHandle(
                projectRequest("POST", "/users/register"), response, new Object()));

        assertEquals(401, assertThrows(BusinessException.class,
                () -> interceptor.preHandle(
                        projectRequest("POST", "/users/profile"), response, new Object()))
                .getStatus().value());
    }

    private HttpServletRequest projectRequest(String method, String path) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn(method);
        when(request.getRequestURI()).thenReturn(path);
        return request;
    }

    private HttpServletRequest requestWithToken(String token) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        return request;
    }
}
