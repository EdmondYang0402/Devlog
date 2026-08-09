package com.myproject.devlog.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class SiteConfigUpdateDTO {
    @NotBlank(message = "站点标题不能为空")
    @Size(max = 100, message = "站点标题不能超过100个字符")
    private String siteTitle;

    @Size(max = 255, message = "首页副标题不能超过255个字符")
    private String heroSubtitle;

    @Size(max = 8, message = "展示关键词最多8个")
    private List<@NotBlank(message = "展示关键词不能为空")
            @Size(max = 30, message = "每个展示关键词不能超过30个字符") String> heroKeywords;

    @NotBlank(message = "博主展示名称不能为空")
    @Size(max = 50, message = "博主展示名称不能超过50个字符")
    private String authorName;

    @Size(max = 500, message = "博主简介不能超过500个字符")
    private String authorBio;

    @Size(max = 500, message = "头像URL不能超过500个字符")
    private String avatarUrl;

    @Size(max = 500, message = "资料卡背景URL不能超过500个字符")
    private String profileBackgroundUrl;

    @Size(max = 1000, message = "公告不能超过1000个字符")
    private String announcement;

    @Size(max = 500, message = "GitHub地址不能超过500个字符")
    private String githubUrl;

    @Size(max = 500, message = "Gitee地址不能超过500个字符")
    private String giteeUrl;
}
