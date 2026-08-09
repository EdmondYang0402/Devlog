package com.myproject.devlog.service;

import com.myproject.devlog.common.RoleConstant;
import com.myproject.devlog.common.UserContext;
import com.myproject.devlog.config.UploadProperties;
import com.myproject.devlog.mapper.UserMapper;
import com.myproject.devlog.pojo.entity.User;
import com.myproject.devlog.pojo.vo.ImageUploadVO;
import com.myproject.devlog.service.impl.LocalStorageServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LocalStorageServiceTests {
    @TempDir
    Path tempDir;

    private final UserMapper userMapper = mock(UserMapper.class);

    @AfterEach
    void clearUserContext() {
        UserContext.clear();
    }

    @Test
    void storesWebpAndReturnsBrowserAccessibleRelativeUrl() {
        LocalStorageServiceImpl service = createService();
        UserContext.set(7L);
        when(userMapper.selectById(7L)).thenReturn(
                User.builder().id(7L).role(RoleConstant.ADMIN).build());
        MockMultipartFile file = new MockMultipartFile(
                "file", "cover.webp", "image/webp", new byte[]{1, 2, 3});

        ImageUploadVO result = service.uploadImage(file);

        assertThat(result.getUrl()).startsWith("/uploads/article/cover/");
        assertThat(result.getUrl()).endsWith(".webp");
        assertThat(Files.exists(tempDir.resolve(result.getObjectKey()))).isTrue();
    }

    @Test
    void rejectsGifAvatarsEvenWhenArticleUploaderAllowsGif() {
        LocalStorageServiceImpl service = createService();
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.gif", "image/gif", new byte[]{1});

        assertThatThrownBy(() -> service.uploadAvatar(file))
                .hasMessage("不支持的图片类型");
    }

    @Test
    void rejectsAvatarLargerThanTwoMegabytes() {
        LocalStorageServiceImpl service = createService();
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.webp", "image/webp", new byte[2 * 1024 * 1024 + 1]);

        assertThatThrownBy(() -> service.uploadAvatar(file))
                .hasMessage("图片大小不能超过 2MB");
    }

    private LocalStorageServiceImpl createService() {
        UploadProperties properties = new UploadProperties();
        properties.setDir(tempDir);
        properties.setPublicUrlPrefix("/uploads");
        LocalStorageServiceImpl service =
                new LocalStorageServiceImpl(userMapper, properties);
        service.initializeUploadDirectory();
        return service;
    }
}
