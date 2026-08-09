package com.myproject.devlog.service;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.mapper.UserMapper;
import com.myproject.devlog.pojo.dto.UserLoginDTO;
import com.myproject.devlog.pojo.entity.User;
import com.myproject.devlog.pojo.vo.UserLoginVO;
import com.myproject.devlog.service.impl.UserServiceImpl;
import com.myproject.devlog.utils.UserConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceLoginTests {

    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserConverter userConverter;
    @Mock
    private LoginSessionService loginSessionService;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(
                userMapper, passwordEncoder, userConverter, loginSessionService);
    }

    @Test
    void hidesWhetherUserExists() {
        when(userMapper.selectByUsername("missing")).thenReturn(null);

        assertLoginFailure(login("missing", "secret"),
                HttpStatus.UNAUTHORIZED, "用户名或密码错误");

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verifyNoInteractions(loginSessionService);
    }

    @Test
    void reportsWrongPasswordAsUnauthorizedWithoutCreatingSession() {
        User user = user(1L, 0, "encoded");
        when(userMapper.selectByUsername("alice")).thenReturn(user);
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertLoginFailure(login("alice", "wrong"),
                HttpStatus.UNAUTHORIZED, "用户名或密码错误");

        verifyNoInteractions(loginSessionService);
    }

    @Test
    void rejectsBannedUserOnlyAfterPasswordIsVerified() {
        User user = user(1L, 1, "encoded");
        when(userMapper.selectByUsername("alice")).thenReturn(user);
        when(passwordEncoder.matches("correct", "encoded")).thenReturn(true);

        assertLoginFailure(login("alice", "correct"),
                HttpStatus.FORBIDDEN, "账号已被封禁");

        verifyNoInteractions(loginSessionService);
    }

    @Test
    void nullStatusIsNotTreatedAsBannedAndSuccessfulFlowIsPreserved() {
        User user = user(7L, null, "encoded");
        when(userMapper.selectByUsername("alice")).thenReturn(user);
        when(passwordEncoder.matches("correct", "encoded")).thenReturn(true);
        when(loginSessionService.createSession(7L)).thenReturn("session-7");

        UserLoginVO result = userService.login(login("alice", "correct"));

        assertEquals(7L, result.getId());
        assertNotNull(result.getToken());
        assertFalse(result.getToken().isBlank());
        verify(loginSessionService).createSession(7L);
    }

    private void assertLoginFailure(UserLoginDTO dto, HttpStatus status, String message) {
        BusinessException exception = assertThrows(BusinessException.class, () -> userService.login(dto));
        assertEquals(status, exception.getStatus());
        assertEquals(message, exception.getMessage());
    }

    private UserLoginDTO login(String username, String password) {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername(username);
        dto.setPassword(password);
        return dto;
    }

    private User user(Long id, Integer status, String password) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        user.setPassword(password);
        return user;
    }
}
