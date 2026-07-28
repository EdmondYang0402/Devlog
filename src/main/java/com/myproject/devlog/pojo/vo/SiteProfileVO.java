package com.myproject.devlog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteProfileVO {
    private String siteTitle;
    private String heroSubtitle;
    private List<String> heroKeywords;
    private String authorName;
    private String authorBio;
    private String avatarUrl;
    private String profileBackgroundUrl;
    private String announcement;
    private String githubUrl;
    private String giteeUrl;
}
