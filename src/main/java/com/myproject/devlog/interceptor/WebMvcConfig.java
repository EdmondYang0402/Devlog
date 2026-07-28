package com.myproject.devlog.interceptor;

import org.springframework.context.annotation.Configuration;
import com.myproject.devlog.config.UploadProperties;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
    private final AdminInterceptor adminInterceptor;
    private final UploadProperties uploadProperties;

    public WebMvcConfig(JwtInterceptor jwtInterceptor, AdminInterceptor adminInterceptor,
                        UploadProperties uploadProperties) {
        this.jwtInterceptor = jwtInterceptor;
        this.adminInterceptor = adminInterceptor;
        this.uploadProperties = uploadProperties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/user/resetPwd",
                        "/error",
                        "/article/**",
                        "/media-reviews/**",
                        "/category",
                        "/tags",
                        "/comment/article/**",
                        "/site/profile",
                        "/site/backgrounds",
                        "/projects/**",
                        "/uploads/**",
                        "/statistics/profile"
                )
                .order(1);

        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**")
                .order(2);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String resourceLocation = uploadProperties.getDir()
                .toAbsolutePath()
                .normalize()
                .toUri()
                .toString();
        if (!resourceLocation.endsWith("/")) {
            resourceLocation += "/";
        }
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation);
    }
}
