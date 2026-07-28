package com.myproject.devlog.service;

import com.myproject.devlog.pojo.vo.ImageUploadVO;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    ImageUploadVO uploadImage(MultipartFile file);

    ImageUploadVO uploadAvatar(MultipartFile file);
}
