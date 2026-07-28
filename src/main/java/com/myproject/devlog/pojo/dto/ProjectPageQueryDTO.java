package com.myproject.devlog.pojo.dto;

import lombok.Data;

@Data
public class ProjectPageQueryDTO {
    private Integer page;
    private Integer size;
    private String keyword;
    private Integer status;
    private Integer featured;
}
