package com.myproject.devlog.pojo.dto;

import lombok.Data;

@Data
public class CommentCreateDTO {

    private Long articleId;

    /**
     * The comment being replied to. Null means a top-level comment.
     */
    private Long parentId;

    private String content;
}
