package com.myproject.devlog.service;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.mapper.UserMapper;
import com.myproject.devlog.pojo.dto.UserRegisterDTO;
import com.myproject.devlog.pojo.entity.User;
import com.myproject.devlog.service.impl.UserServiceImpl;
import com.myproject.devlog.utils.UserConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserRegistrationServiceTests {

    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userMapper = mock(UserMapper.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserServiceImpl(
                userMapper,
                passwordEncoder,
                mock(UserConverter.class),
                mock(LoginSessionService.class)
        );
    }

    @Test
    void rejectsExistingUsernameWithClearMessage() {
        UserRegisterDTO dto = registration("takenUser", "validPassword", "new@example.com");
        when(userMapper.selectByUsername("takenUser")).thenReturn(new User());

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.register(dto));

        assertEquals("用户名已存在", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(userMapper, never()).insert(any());
    }

    @Test
    void validatesUsernameLengthAfterTrimming() {
        UserRegisterDTO dto = registration("  abc  ", "validPassword", "new@example.com");

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.register(dto));

        assertEquals("用户名长度必须为 5~16 位", exception.getMessage());
        verify(userMapper, never()).insert(any());
    }

    @Test
    void rejectsExistingEmailWithClearMessage() {
        UserRegisterDTO dto = registration("newUser", "validPassword", "taken@example.com");
        when(userMapper.selectByEmail("taken@example.com")).thenReturn(new User());

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.register(dto));

        assertEquals("邮箱已被注册", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(userMapper, never()).insert(any());
    }

    @Test
    void trimsIdentifiersHashesPasswordAndInsertsUser() {
        UserRegisterDTO dto = registration("  newUser  ", "validPassword", "  new@example.com  ");
        when(passwordEncoder.encode("validPassword")).thenReturn("hashed-password");
        when(userMapper.insert(any(User.class))).thenReturn(1);

        userService.register(dto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insert(captor.capture());
        User inserted = captor.getValue();
        assertEquals("newUser", inserted.getUsername());
        assertEquals("new@example.com", inserted.getEmail());
        assertEquals("hashed-password", inserted.getPassword());
    }

    @Test
    void convertsDatabaseUniquenessRaceToSafeBusinessError() {
        UserRegisterDTO dto = registration("newUser", "validPassword", "new@example.com");
        when(passwordEncoder.encode("validPassword")).thenReturn("hashed-password");
        org.mockito.Mockito.doThrow(new DataIntegrityViolationException("duplicate"))
                .when(userMapper).insert(any(User.class));

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.register(dto));

        assertEquals("用户名或邮箱已存在", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    private UserRegisterDTO registration(String username, String password, String email) {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername(username);
        dto.setPassword(password);
        dto.setEmail(email);
        return dto;
    }
}
