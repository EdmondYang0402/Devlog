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

class UploadControllerTests {
    @Test
    void delegatesImageUploadToStorageService() {
        StorageService storageService = mock(StorageService.class);
        UploadController controller = new UploadController(storageService);
        MockMultipartFile file = new MockMultipartFile(
                "file", "cover.jpg", "image/jpeg", new byte[]{1});
        ImageUploadVO uploaded = new ImageUploadVO(
                "https://example.test/article/cover/2026/07/id.jpg",
                "article/cover/2026/07/id.jpg");
        when(storageService.uploadImage(file)).thenReturn(uploaded);

        Result<ImageUploadVO> result = controller.uploadImage(file);

        verify(storageService).uploadImage(file);
        assertThat(result.getData()).isSameAs(uploaded);
    }
}
