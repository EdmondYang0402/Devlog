package com.myproject.devlog.utils;

import com.myproject.devlog.common.RoleConstant;
import com.myproject.devlog.common.StatusConstant;
import com.myproject.devlog.pojo.entity.Article;
import com.myproject.devlog.pojo.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PermissionUtilTests {
    @Test
    void booleanChecksAreNullSafe() {
        assertThat(PermissionUtil.isAdmin(null)).isFalse();
        assertThat(PermissionUtil.isBanned(null)).isFalse();
        assertThat(PermissionUtil.isOwner(null, 1L)).isFalse();
        assertThat(PermissionUtil.isOwner(null, null)).isTrue();

        User user = new User();
        assertThat(PermissionUtil.isAdmin(user)).isFalse();
        assertThat(PermissionUtil.isBanned(user)).isFalse();
    }

    @Test
    void checkUserRejectsMissingAndBannedUsers() {
        assertThatThrownBy(() -> PermissionUtil.checkUser(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("用户不存在");

        User banned = new User();
        banned.setStatus(StatusConstant.BANNED);
        assertThatThrownBy(() -> PermissionUtil.checkUser(banned))
                .hasMessage("账号已封禁");
    }

    @Test
    void checkAdminUsesAdminRole() {
        assertThatCode(() -> PermissionUtil.checkAdmin(activeUser(RoleConstant.ADMIN)))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> PermissionUtil.checkAdmin(activeUser(0)))
                .hasMessage("无管理员权限");
    }

    @Test
    void articlePermissionAllowsAdminOrOwner() {
        Article article = new Article();
        article.setAuthorId(7L);

        assertThatCode(() -> PermissionUtil.checkArticlePermission(
                activeUser(RoleConstant.ADMIN), article, 8L)).doesNotThrowAnyException();
        assertThatCode(() -> PermissionUtil.checkArticlePermission(
                activeUser(0), article, 7L)).doesNotThrowAnyException();
        assertThatThrownBy(() -> PermissionUtil.checkArticlePermission(
                activeUser(0), article, 8L)).hasMessage("无权限");
        assertThatThrownBy(() -> PermissionUtil.checkArticlePermission(
                activeUser(0), null, 7L)).hasMessage("文章不存在");
    }

    private User activeUser(Integer role) {
        User user = new User();
        user.setRole(role);
        user.setStatus(0);
        return user;
    }
}
