package com.myproject.devlog.utils;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.common.RoleConstant;
import com.myproject.devlog.common.StatusConstant;
import com.myproject.devlog.pojo.entity.Article;
import com.myproject.devlog.pojo.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

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
        BusinessException missing = org.junit.jupiter.api.Assertions.assertThrows(
                BusinessException.class, () -> PermissionUtil.checkUser(null));
        assertThat(missing.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(missing).hasMessage("用户不存在");

        User banned = new User();
        banned.setStatus(StatusConstant.BANNED);
        BusinessException forbidden = org.junit.jupiter.api.Assertions.assertThrows(
                BusinessException.class, () -> PermissionUtil.checkUser(banned));
        assertThat(forbidden.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(forbidden).hasMessage("账号已封禁");
    }

    @Test
    void checkAdminUsesAdminRole() {
        assertThatCode(() -> PermissionUtil.checkAdmin(activeUser(RoleConstant.ADMIN)))
                .doesNotThrowAnyException();
        BusinessException exception = org.junit.jupiter.api.Assertions.assertThrows(
                BusinessException.class, () -> PermissionUtil.checkAdmin(activeUser(0)));
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void articlePermissionAllowsAdminOrOwner() {
        Article article = new Article();
        article.setAuthorId(7L);

        assertThatCode(() -> PermissionUtil.checkArticlePermission(
                activeUser(RoleConstant.ADMIN), article, 8L)).doesNotThrowAnyException();
        assertThatCode(() -> PermissionUtil.checkArticlePermission(
                activeUser(0), article, 7L)).doesNotThrowAnyException();
        BusinessException forbidden = org.junit.jupiter.api.Assertions.assertThrows(
                BusinessException.class,
                () -> PermissionUtil.checkArticlePermission(activeUser(0), article, 8L));
        assertThat(forbidden.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);

        BusinessException missing = org.junit.jupiter.api.Assertions.assertThrows(
                BusinessException.class,
                () -> PermissionUtil.checkArticlePermission(activeUser(0), null, 7L));
        assertThat(missing.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private User activeUser(Integer role) {
        User user = new User();
        user.setRole(role);
        user.setStatus(0);
        return user;
    }
}
