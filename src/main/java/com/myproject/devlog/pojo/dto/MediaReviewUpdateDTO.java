package com.myproject.devlog.pojo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MediaReviewUpdateDTO {
    @NotBlank(message = "媒体标题不能为空")
    @Size(max = 200, message = "媒体标题不能超过200个字符")
    private String title;

    @NotNull(message = "媒体类型不能为空")
    @Min(value = 0, message = "媒体类型非法")
    @Max(value = 3, message = "媒体类型非法")
    private Integer mediaType;

    @NotNull(message = "评价状态不能为空")
    @Min(value = 0, message = "评价状态非法")
    @Max(value = 3, message = "评价状态非法")
    private Integer status;

    @Size(max = 500, message = "封面地址不能超过500个字符")
    private String coverUrl;

    @Min(value = 1, message = "评分不能低于1")
    @Max(value = 10, message = "评分不能超过10")
    private Integer rating;

    @Size(max = 500, message = "短评不能超过500个字符")
    private String shortReview;

    private String content;
    private LocalDate finishedDate;
}
