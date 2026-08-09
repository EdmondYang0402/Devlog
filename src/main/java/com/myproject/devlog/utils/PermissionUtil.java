package com.myproject.devlog.utils;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.common.RoleConstant;
import com.myproject.devlog.common.StatusConstant;
import com.myproject.devlog.pojo.entity.Article;
import com.myproject.devlog.pojo.entity.User;

import java.util.Objects;
import org.springframework.http.HttpStatus;

public final class PermissionUtil {

    private PermissionUtil() {
    }

    public static boolean isAdmin(User user) {
        return user != null
                && RoleConstant.ADMIN.equals(user.getRole());
    }

    public static boolean isBanned(User user) {
        return user != null
                && StatusConstant.BANNED.equals(user.getStatus());
    }

    public static boolean isOwner(Long currentUserId, Long ownerId) {
        return Objects.equals(currentUserId, ownerId);
    }

    public static void checkUser(User user) {
        if (user == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        if (isBanned(user)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "账号已封禁");
        }
    }

    public static void checkAdmin(User user) {
        checkUser(user);
        if (!isAdmin(user)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "无管理员权限");
        }
    }

    public static void checkArticlePermission(User user, Article article, Long currentUserId) {
        checkUser(user);
        if (article == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "文章不存在");
        }
        if (!isAdmin(user) && !isOwner(currentUserId, article.getAuthorId())) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "无权执行该操作");
        }
    }
}
