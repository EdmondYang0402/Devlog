package com.myproject.devlog.pojo.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectDetailVO {
    private Long id;
    private String name;
    private String summary;
    private String content;
    private String coverUrl;
    private List<String> techStack;
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
