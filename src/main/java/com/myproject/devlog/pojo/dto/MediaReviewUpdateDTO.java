package com.myproject.devlog.pojo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MediaReviewUpdateDTO {
    private String title;
    private Integer mediaType;
    private Integer status;
    private String coverUrl;
    private Integer rating;
    private String shortReview;
    private String content;
    private LocalDate finishedDate;
}
