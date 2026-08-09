package com.myproject.devlog.controller;

import com.myproject.devlog.common.Result;
import com.myproject.devlog.pojo.dto.CategoryTagUpdateDTO;
import com.myproject.devlog.pojo.vo.TagVO;
import com.myproject.devlog.service.CategoryTagService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/categories/{categoryId}/tags")
public class AdminCategoryTagController {
    private final CategoryTagService categoryTagService;

    public AdminCategoryTagController(CategoryTagService categoryTagService) {
        this.categoryTagService = categoryTagService;
    }

    @GetMapping
    public Result<List<TagVO>> list(@PathVariable Long categoryId) {
        return Result.success(categoryTagService.listTagsByCategoryId(categoryId));
    }

    @PutMapping
    public Result<Void> replace(@PathVariable Long categoryId,
                                @Valid @RequestBody CategoryTagUpdateDTO dto) {
        categoryTagService.setTagsForCategory(categoryId, dto.getTagIds());
        return Result.success();
    }
}
