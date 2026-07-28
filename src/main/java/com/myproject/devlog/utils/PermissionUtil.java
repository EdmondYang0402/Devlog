package com.myproject.devlog.utils;

import com.myproject.devlog.common.RoleConstant;
import com.myproject.devlog.common.StatusConstant;
import com.myproject.devlog.pojo.entity.Article;
import com.myproject.devlog.pojo.entity.User;

import java.util.Objects;

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
            throw new RuntimeException("用户不存在");
        }
        if (isBanned(user)) {
            throw new RuntimeException("账号已封禁");
        }
    }

    public static void checkAdmin(User user) {
        checkUser(user);
        if (!isAdmin(user)) {
            throw new RuntimeException("无管理员权限");
        }
    }

    public static void checkArticlePermission(User user, Article article, Long currentUserId) {
        checkUser(user);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        if (!isAdmin(user) && !isOwner(currentUserId, article.getAuthorId())) {
            throw new RuntimeException("无权限");
        }
    }
}
