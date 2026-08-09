package com.myproject.devlog.pojo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CategoryTagUpdateDTO {
    @NotNull(message = "标签 ID 列表不能为空")
    @Size(max = 100, message = "一个分类最多关联100个标签")
    @Valid
    private List<@NotNull(message = "标签 ID 不能为空")
            @Positive(message = "标签 ID 必须为正数") Long> tagIds;
}
