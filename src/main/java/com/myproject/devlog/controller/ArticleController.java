package com.myproject.devlog.controller;

import com.myproject.devlog.common.PageResult;
import com.myproject.devlog.common.Result;
import com.myproject.devlog.pojo.vo.ArticleDetailVO;
import com.myproject.devlog.pojo.vo.ArticleListVO;
import com.myproject.devlog.service.ArticleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    // 文章详情
    @GetMapping("/{id}")
    public Result<ArticleDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(articleService.getFrontDetail(id));
    }

    // 浏览量+1
    @PostMapping("/{id}/view")
    public Result<Void> increaseView(@PathVariable Long id) {
        articleService.increaseViewCount(id);
        return Result.success();
    }

    // 前台文章列表：GET /articles?page=1&size=10&categorySlug=notes
    @GetMapping
    public Result<PageResult<ArticleListVO>> list(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String categorySlug,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Long> tagIds) {
        return Result.success(articleService.page(
                page, size, categoryId, categorySlug, keyword, tagIds
        ));
    }


}
