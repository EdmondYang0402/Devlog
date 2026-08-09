package com.myproject.devlog.interceptor;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.common.UserContext;
import com.myproject.devlog.mapper.UserMapper;
import com.myproject.devlog.pojo.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminInterceptorTests {
    @AfterEach
    void cleanContext() {
        UserContext.clear();
    }

    @Test
    void ordinaryUserCannotAccessAdminEndpoints() {
        UserMapper mapper = mock(UserMapper.class);
        User user = new User();
        user.setRole(0);
        user.setStatus(1);
        UserContext.set(7L);
        when(mapper.selectById(7L)).thenReturn(user);

        AdminInterceptor interceptor = new AdminInterceptor(mapper);
        BusinessException exception = assertThrows(BusinessException.class,
                () -> interceptor.preHandle(mock(jakarta.servlet.http.HttpServletRequest.class),
                        mock(jakarta.servlet.http.HttpServletResponse.class), new Object()));
        assertEquals(403, exception.getStatus().value());
    }
}
