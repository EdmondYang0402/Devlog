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
@RequestMapping("/admin/upload")
public class UploadController {
    private static final Logger log = LoggerFactory.getLogger(UploadController.class);

    private final StorageService storageService;

    public UploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/image")
    public Result<ImageUploadVO> uploadImage(@RequestParam("file") MultipartFile file) {
        log.info(
                "Image upload request entered: filename={}, contentType={}, size={}",
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize()
        );
        return Result.success(storageService.uploadImage(file));
    }
}
