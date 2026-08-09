package com.myproject.devlog.controller;

import com.myproject.devlog.common.Result;
import com.myproject.devlog.pojo.dto.TagCreateDTO;
import com.myproject.devlog.pojo.dto.TagUpdateDTO;
import com.myproject.devlog.pojo.vo.AdminTagVO;
import com.myproject.devlog.pojo.vo.TagVO;
import com.myproject.devlog.service.TagService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/tags")
public class AdminTagController {
    private final TagService tagService;

    public AdminTagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public Result<List<AdminTagVO>> list() {
        return Result.success(tagService.listAdmin());
    }

    @GetMapping("/options")
    public Result<List<TagVO>> options() {
        return Result.success(tagService.list());
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody TagCreateDTO dto) {
        tagService.create(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody TagUpdateDTO dto) {
        tagService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return Result.success();
    }
}
