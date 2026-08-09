package com.myproject.devlog.controller;

import com.myproject.devlog.common.PageResult;
import com.myproject.devlog.common.Result;
import com.myproject.devlog.pojo.vo.MediaReviewDetailVO;
import com.myproject.devlog.pojo.vo.MediaReviewListVO;
import com.myproject.devlog.service.MediaReviewService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/media-reviews")
public class MediaReviewController {
    // 前台只提供读取接口，并沿用 WebMvcConfig 中已有的公开白名单。
    private final MediaReviewService mediaReviewService;

    public MediaReviewController(MediaReviewService mediaReviewService) {
        this.mediaReviewService = mediaReviewService;
    }

    @GetMapping
    public Result<PageResult<MediaReviewListVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer size,
            @RequestParam(required = false) Integer mediaType,
            @RequestParam(defaultValue = "latest") String sort) {
        return Result.success(mediaReviewService.page(page, size, mediaType, sort));
    }

    @GetMapping("/{id}")
    public Result<MediaReviewDetailVO> detail(@PathVariable Long id) {
        return Result.success(mediaReviewService.getById(id));
    }
}
