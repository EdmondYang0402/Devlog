package com.myproject.devlog.service;

import com.myproject.devlog.mapper.UserMapper;
import com.myproject.devlog.service.impl.UserServiceImpl;
import com.myproject.devlog.utils.JwtUtil;
import com.myproject.devlog.utils.UserConverter;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceLogoutTests {

    @Test
    void logoutDeletesSessionContainedInBearerToken() {
        LoginSessionService loginSessionService = mock(LoginSessionService.class);
        UserServiceImpl userService = new UserServiceImpl(
                mock(UserMapper.class),
                mock(PasswordEncoder.class),
                mock(UserConverter.class),
                loginSessionService
        );
        String sessionId = "session-to-delete";
        String token = JwtUtil.generateToken(7L, sessionId);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        userService.logout(request);

        verify(loginSessionService).deleteSession(sessionId);
    }
}
