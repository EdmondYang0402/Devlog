package com.myproject.devlog.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryTag {
    private Long categoryId;
    private Long tagId;
    private LocalDateTime createTime;
}
