package com.myproject.devlog.pojo.dto;

import lombok.Data;

@Data
public class SiteBackgroundUpdateDTO {
    private String imageUrl;
    private String title;
    private Integer enabled;
    private Integer sortOrder;
}
