package com.myproject.devlog.pojo.dto;

import lombok.Data;

@Data
public class SiteBackgroundPageQueryDTO {
    private Integer page;
    private Integer size;
    private String keyword;
    private Integer enabled;
}
