package com.myproject.devlog.controller;

import com.myproject.devlog.common.Result;
import com.myproject.devlog.pojo.vo.ImageUploadVO;
import com.myproject.devlog.service.StorageService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserAvatarControllerTests {
    @Test
    void delegatesAvatarUploadToExistingStorageService() {
        StorageService storageService = mock(StorageService.class);
        UserAvatarController controller = new UserAvatarController(storageService);
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.webp", "image/webp", new byte[]{1});
        ImageUploadVO uploaded = new ImageUploadVO(
                "https://example.test/user/avatar/2026/07/id.webp",
                "user/avatar/2026/07/id.webp");
        when(storageService.uploadAvatar(file)).thenReturn(uploaded);

        Result<ImageUploadVO> result = controller.upload(file);

        verify(storageService).uploadAvatar(file);
        assertThat(result.getData()).isSameAs(uploaded);
    }
}
