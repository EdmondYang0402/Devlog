package com.myproject.devlog.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SiteConfig {
    private Long id;
    private String siteTitle;
    private String heroSubtitle;
    private String heroKeywords;
    private String authorName;
    private String authorBio;
    private String avatarUrl;
    private String profileBackgroundUrl;
    private String announcement;
    private String githubUrl;
    private String giteeUrl;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
