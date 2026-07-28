package com.myproject.devlog.controller;

import com.myproject.devlog.common.PageResult;
import com.myproject.devlog.common.Result;
import com.myproject.devlog.pojo.dto.ArticleCreateDTO;
import com.myproject.devlog.pojo.dto.ArticleUpdateDTO;
import com.myproject.devlog.pojo.vo.ArticleListVO;
import com.myproject.devlog.pojo.vo.ArticleDetailVO;
import com.myproject.devlog.service.AdminArticleService;
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
@RequestMapping("/admin/article")
public class AdminArticleController {
    private final AdminArticleService adminArticleService;

    public AdminArticleController(AdminArticleService adminArticleService) {
        this.adminArticleService = adminArticleService;
    }

    // 发布文章
    @PostMapping
    public Result<Void> create(@Valid @RequestBody ArticleCreateDTO dto) {
        adminArticleService.create(dto);
        return Result.success();
    }

    // 修改文章
    @PutMapping
    public Result<Void> update(@Valid @RequestBody ArticleUpdateDTO dto) {
        adminArticleService.update(dto);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<ArticleDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(adminArticleService.getDetail(id));
    }

    // 删除文章
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        adminArticleService.delete(id);
        return Result.success();
    }

    // 后台文章分页
    @GetMapping
    public Result<PageResult<ArticleListVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status) {
        return Result.success(adminArticleService.page(page, size, title, status));
    }
}
