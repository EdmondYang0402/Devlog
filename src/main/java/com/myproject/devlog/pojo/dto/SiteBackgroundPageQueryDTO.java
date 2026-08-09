package com.myproject.devlog.pojo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SiteBackgroundPageQueryDTO {
    @Min(value = 1, message = "页码必须大于等于1")
    private Integer page;

    @Min(value = 1, message = "每页数量必须大于等于1")
    @Max(value = 100, message = "每页数量不能超过100")
    private Integer size;

    @Size(max = 100, message = "搜索关键字不能超过100个字符")
    private String keyword;

    @Min(value = 0, message = "启用状态非法")
    @Max(value = 1, message = "启用状态非法")
    private Integer enabled;
}
