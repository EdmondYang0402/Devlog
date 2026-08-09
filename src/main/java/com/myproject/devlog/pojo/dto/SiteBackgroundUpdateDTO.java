package com.myproject.devlog.pojo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SiteBackgroundUpdateDTO {
    @NotBlank(message = "背景图片地址不能为空")
    @Size(max = 500, message = "背景图片地址不能超过500个字符")
    private String imageUrl;

    @Size(max = 100, message = "背景标题不能超过100个字符")
    private String title;

    @Min(value = 0, message = "启用状态非法")
    @Max(value = 1, message = "启用状态非法")
    private Integer enabled;

    @Min(value = -100000, message = "排序值过小")
    @Max(value = 100000, message = "排序值过大")
    private Integer sortOrder;
}
