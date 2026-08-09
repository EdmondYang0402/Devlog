package com.myproject.devlog.interceptor;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.common.UserContext;
import com.myproject.devlog.mapper.UserMapper;
import com.myproject.devlog.pojo.entity.User;
import com.myproject.devlog.utils.PermissionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    private final UserMapper userMapper;

    public AdminInterceptor(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        Long userId = UserContext.get();
        if (userId == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "未登录");
        }
        User user = userMapper.selectById(userId);
        PermissionUtil.checkAdmin(user);
        return true;
    }
}
