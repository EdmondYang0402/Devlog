package com.myproject.devlog.interceptor;


import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.common.UserContext;
import com.myproject.devlog.service.LoginSessionService;
import com.myproject.devlog.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final LoginSessionService loginSessionService;

    public JwtInterceptor(LoginSessionService loginSessionService) {
        this.loginSessionService = loginSessionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        if (isAnonymousEndpoint(request)) {
            return true;
        }

        // 项目展示只开放前台 GET 读取；后台 /admin/projects 仍完整经过 JWT 与管理员校验。
        if (isPublicProjectRead(request)) {
            return true;
        }

        // 背景轮播只公开单一 GET 列表接口，不扩大到其他 /site 或后台路径。
        if (isPublicSiteBackgroundRead(request)) {
            return true;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "未登录");
        }

        String token = authorization.substring(7).trim();
        if (token.isEmpty() || !JwtUtil.validateToken(token)) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Token非法或已过期");
        }

        Long tokenUserId = JwtUtil.parseUserId(token);
        String sessionId = JwtUtil.parseSessionId(token);
        if (sessionId == null || sessionId.isBlank()) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "登录已失效，请重新登录");
        }

        Long redisUserId = loginSessionService.getUserId(sessionId);
        if (redisUserId == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "登录已失效，请重新登录");
        }
        if (!redisUserId.equals(tokenUserId)) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "登录信息异常");
        }

        UserContext.set(tokenUserId);

        return true;
    }

    private boolean isPublicProjectRead(HttpServletRequest request) {
        if (!HttpMethod.GET.matches(request.getMethod())) {
            return false;
        }
        String path = request.getRequestURI();
        return "/projects".equals(path)
                || "/projects/featured".equals(path)
                || (path != null && path.matches("/projects/\\d+"));
    }

    private boolean isAnonymousEndpoint(HttpServletRequest request) {
        if (!HttpMethod.POST.matches(request.getMethod())) {
            return false;
        }
        String path = request.getRequestURI();
        return "/users/login".equals(path) || "/users/register".equals(path);
    }

    private boolean isPublicSiteBackgroundRead(HttpServletRequest request) {
        return HttpMethod.GET.matches(request.getMethod())
                && "/site-backgrounds".equals(request.getRequestURI());
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        UserContext.clear();
    }
}
