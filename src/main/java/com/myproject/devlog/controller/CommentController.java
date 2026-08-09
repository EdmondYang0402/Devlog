package com.myproject.devlog.controller;

import com.myproject.devlog.common.Result;
import com.myproject.devlog.pojo.dto.CommentCreateDTO;
import com.myproject.devlog.pojo.vo.CommentVO;
import com.myproject.devlog.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody CommentCreateDTO dto) {
        commentService.create(dto);
        return Result.success();
    }

    @GetMapping("/articles/{articleId}")
    public Result<List<CommentVO>> listByArticleId(@PathVariable Long articleId) {
        return Result.success(commentService.listByArticleId(articleId));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return Result.success();
    }
}
