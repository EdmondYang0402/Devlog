package com.myproject.devlog.controller;

import com.myproject.devlog.common.PageResult;
import com.myproject.devlog.common.Result;
import com.myproject.devlog.pojo.dto.MediaReviewCreateDTO;
import com.myproject.devlog.pojo.dto.MediaReviewUpdateDTO;
import com.myproject.devlog.pojo.vo.MediaReviewDetailVO;
import com.myproject.devlog.pojo.vo.MediaReviewListVO;
import com.myproject.devlog.service.MediaReviewService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/media-reviews")
public class AdminMediaReviewController {
    // 后台 Controller 只暴露管理操作，身份与管理员权限继续交给项目现有拦截器链。
    private final MediaReviewService mediaReviewService;

    public AdminMediaReviewController(MediaReviewService mediaReviewService) {
        this.mediaReviewService = mediaReviewService;
    }

    @GetMapping
    public Result<PageResult<MediaReviewListVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer mediaType,
            @RequestParam(required = false) Integer status) {
        return Result.success(mediaReviewService.pageAdmin(page, size, title, mediaType, status));
    }

    @GetMapping("/{id}")
    public Result<MediaReviewDetailVO> detail(@PathVariable Long id) {
        return Result.success(mediaReviewService.getAdminById(id));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody MediaReviewCreateDTO dto) {
        mediaReviewService.create(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody MediaReviewUpdateDTO dto) {
        mediaReviewService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        mediaReviewService.delete(id);
        return Result.success();
    }
}
