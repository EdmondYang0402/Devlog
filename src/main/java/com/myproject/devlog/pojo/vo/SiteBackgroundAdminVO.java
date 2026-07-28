package com.myproject.devlog.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SiteBackgroundAdminVO {
    private Long id;
    private String imageUrl;
    private String title;
    private Integer enabled;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
