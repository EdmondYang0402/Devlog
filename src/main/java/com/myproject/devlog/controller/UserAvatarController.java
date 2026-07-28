package com.myproject.devlog.controller;

import com.myproject.devlog.common.Result;
import com.myproject.devlog.pojo.vo.ImageUploadVO;
import com.myproject.devlog.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user/avatar")
public class UserAvatarController {
    private static final Logger log = LoggerFactory.getLogger(UserAvatarController.class);

    private final StorageService storageService;

    public UserAvatarController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public Result<ImageUploadVO> upload(@RequestParam("file") MultipartFile file) {
        log.info(
                "Avatar upload request entered: filename={}, contentType={}, size={}",
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize()
        );
        // /user/** 继续经过现有 JWT/Redis 拦截器，只为当前登录用户提供头像文件上传。
        return Result.success(storageService.uploadAvatar(file));
    }
}
