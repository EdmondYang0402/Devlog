package com.myproject.devlog.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentCreateDTO {

    @NotNull(message = "文章ID不能为空")
    @Positive(message = "文章ID必须为正数")
    private Long articleId;

    /**
     * The comment being replied to. Null means a top-level comment.
     */
    @Positive(message = "父评论ID必须为正数")
    private Long parentId;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 500, message = "评论内容不能超过500个字符")
    private String content;

}
