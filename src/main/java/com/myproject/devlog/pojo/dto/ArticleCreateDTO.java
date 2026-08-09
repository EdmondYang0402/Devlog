package com.myproject.devlog.pojo.dto;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
public class ArticleCreateDTO {

    @NotBlank(message = "文章标题不能为空")
    @Size(max = 255, message = "文章标题不能超过255个字符")
    private String title;

    private String content;

    @Size(max = 1000, message = "文章摘要不能超过1000个字符")
    private String summary;

    @Size(max = 500, message = "封面地址不能超过500个字符")
    private String coverImage;

    @Positive(message = "分类ID必须为正数")
    private Long categoryId;

    @Min(value = 0, message = "文章状态非法")
    @Max(value = 1, message = "文章状态非法")
    private Integer status;

    private List<@NotNull(message = "标签ID不能为空") @Positive(message = "标签ID必须为正数") Long> tagIds;
}
