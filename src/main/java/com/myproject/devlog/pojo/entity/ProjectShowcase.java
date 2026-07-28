package com.myproject.devlog.pojo.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProjectShowcase {
    private Long id;
    private String name;
    private String summary;
    private String content;
    private String coverUrl;
    private String techStack;
    private String githubUrl;
    private String demoUrl;
    private Integer status;
    private LocalDate startedDate;
    private LocalDate completedDate;
    private Integer featured;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
