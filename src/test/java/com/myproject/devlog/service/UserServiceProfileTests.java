package com.myproject.devlog.service;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.common.UserContext;
import com.myproject.devlog.mapper.UserMapper;
import com.myproject.devlog.pojo.dto.ChangePasswordDTO;
import com.myproject.devlog.pojo.dto.UpdateUserDTO;
import com.myproject.devlog.pojo.entity.User;
import com.myproject.devlog.pojo.vo.UserInfoVO;
import com.myproject.devlog.service.impl.UserServiceImpl;
import com.myproject.devlog.utils.UserConverter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceProfileTests {

    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private LoginSessionService loginSessionService;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(
                userMapper, passwordEncoder, new UserConverter(), loginSessionService);
    }

    @AfterEach
    void clearContext() {
        UserContext.clear();
    }

    @Test
    void serviceOwnsCurrentUserIdentityWhenUpdatingProfile() {
        UserContext.set(7L);
        User current = new User();
        current.setId(7L);
        current.setUsername("current-user");
        when(userMapper.selectById(7L)).thenReturn(current);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setAvatar("https://example.test/avatar.webp");
        dto.setBio("个人简介");
        dto.setEmail("current@example.com");

        UserInfoVO result = userService.updateProfile(dto);

        ArgumentCaptor<User> update = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(update.capture());
        assertEquals(7L, update.getValue().getId());
        assertEquals(dto.getAvatar(), update.getValue().getAvatar());
        assertEquals(dto.getBio(), update.getValue().getBio());
        assertEquals(dto.getEmail(), update.getValue().getEmail());
        assertNull(update.getValue().getUsername());
        assertNull(update.getValue().getPassword());
        assertNull(update.getValue().getRole());
        assertNull(update.getValue().getStatus());
        assertNull(update.getValue().getCreateTime());
        assertNull(update.getValue().getUpdateTime());
        assertEquals(7L, result.getId());
    }

    @Test
    void administratorStatusUpdateOnlyWritesIdAndStatus() {
        User existing = new User();
        existing.setId(9L);
        when(userMapper.selectById(9L)).thenReturn(existing);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        userService.updateStatus(9L, 1);

        ArgumentCaptor<User> update = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(update.capture());
        assertEquals(9L, update.getValue().getId());
        assertEquals(1, update.getValue().getStatus());
        assertNull(update.getValue().getUsername());
        assertNull(update.getValue().getEmail());
        assertNull(update.getValue().getPassword());
        assertNull(update.getValue().getRole());
    }

    @Test
    void currentUserCannotBeResolvedWithoutAuthentication() {
        BusinessException exception = assertThrows(
                BusinessException.class, userService::getCurrentUserInfo);

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        verifyNoInteractions(userMapper);
    }

    @Test
    void wrongOldPasswordRemainsBadRequestBusinessRule() {
        UserContext.set(7L);
        User current = new User();
        current.setId(7L);
        current.setPassword("encoded");
        when(userMapper.selectById(7L)).thenReturn(current);
        when(passwordEncoder.matches("wrong-password", "encoded")).thenReturn(false);

        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setOldPassword("wrong-password");
        dto.setNewPassword("new-password");

        BusinessException exception = assertThrows(
                BusinessException.class, () -> userService.changePassword(dto));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("旧密码错误", exception.getMessage());
        verify(userMapper, never()).updateById(org.mockito.ArgumentMatchers.any());
    }
}
