package com.myproject.devlog.pojo.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MediaReview {
    private Long id;
    private String title;
    private Integer mediaType;
    private Integer status;
    private String coverUrl;
    private Integer rating;
    private String shortReview;
    private String content;
    private LocalDate finishedDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
