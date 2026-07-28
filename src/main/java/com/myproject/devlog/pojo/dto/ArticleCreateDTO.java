package com.myproject.devlog.pojo.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class ArticleCreateDTO {

    private String title;

    private String content;

    private String summary;

    private String coverImage;

    private Long categoryId;

    private Integer status;

    private List<@NotNull(message = "标签ID不能为null") Long> tagIds;
}
