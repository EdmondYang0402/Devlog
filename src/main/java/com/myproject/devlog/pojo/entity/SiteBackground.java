package com.myproject.devlog.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SiteBackground {
    private Long id;
    private String imageUrl;
    private String title;
    private Integer enabled;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
