package com.myproject.devlog.controller;

import com.myproject.devlog.common.Result;
import com.myproject.devlog.pojo.dto.CategoryCreateDTO;
import com.myproject.devlog.pojo.dto.CategoryUpdateDTO;
import com.myproject.devlog.pojo.vo.CategoryVO;
import com.myproject.devlog.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/admin/category")
    public Result<Void> create(@Valid @RequestBody CategoryCreateDTO dto) {
        categoryService.create(dto);
        return Result.success();
    }

    @PutMapping("/admin/category/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CategoryUpdateDTO dto) {
        categoryService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/admin/category/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }

    @GetMapping({"/admin/category", "/category"})
    public Result<List<CategoryVO>> listAll() {
        return Result.success(categoryService.listAll());
    }
}
