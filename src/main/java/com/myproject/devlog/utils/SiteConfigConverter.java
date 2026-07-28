package com.myproject.devlog.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.devlog.common.SiteConfigValidator;
import com.myproject.devlog.pojo.dto.SiteConfigUpdateDTO;
import com.myproject.devlog.pojo.entity.SiteConfig;
import com.myproject.devlog.pojo.vo.AdminSiteConfigVO;
import com.myproject.devlog.pojo.vo.SiteProfileVO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class SiteConfigConverter {

    private static final long SITE_CONFIG_ID = 1L;

    private final ObjectMapper objectMapper;
    private final SiteConfigValidator validator;


    private static final TypeReference<List<String>> STRING_LIST_TYPE =
            new TypeReference<>() {};

    public SiteConfigConverter(
            ObjectMapper objectMapper,
            SiteConfigValidator validator) {
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    public SiteProfileVO toPublicVO(SiteConfig config) {
        return SiteProfileVO.builder()
                .siteTitle(config.getSiteTitle())
                .heroSubtitle(config.getHeroSubtitle())
                .heroKeywords(readKeywords(config.getHeroKeywords()))
                .authorName(config.getAuthorName())
                .authorBio(config.getAuthorBio())
                .avatarUrl(config.getAvatarUrl())
                .profileBackgroundUrl(config.getProfileBackgroundUrl())
                .announcement(config.getAnnouncement())
                .githubUrl(config.getGithubUrl())
                .giteeUrl(config.getGiteeUrl())
                .build();
    }

    public AdminSiteConfigVO toAdminVO(SiteConfig config) {
        return AdminSiteConfigVO.builder()
                .id(config.getId())
                .siteTitle(config.getSiteTitle())
                .heroSubtitle(config.getHeroSubtitle())
                .heroKeywords(readKeywords(config.getHeroKeywords()))
                .authorName(config.getAuthorName())
                .authorBio(config.getAuthorBio())
                .avatarUrl(config.getAvatarUrl())
                .profileBackgroundUrl(config.getProfileBackgroundUrl())
                .announcement(config.getAnnouncement())
                .githubUrl(config.getGithubUrl())
                .giteeUrl(config.getGiteeUrl())
                .createTime(config.getCreateTime())
                .updateTime(config.getUpdateTime())
                .build();
    }

    public String writeKeywords(List<String> keywords) {
        try {
            return objectMapper.writeValueAsString(keywords);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("展示关键词序列化失败", e);
        }
    }

    private List<String> readKeywords(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }

        try {
            List<String> values = objectMapper.readValue(json, STRING_LIST_TYPE);
            return values == null
                    ? Collections.emptyList()
                    : values;
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }
    public SiteConfig fromUpdateDTO(SiteConfigUpdateDTO dto) {
        List<String> keywords =
                validator.normalizeKeywords(dto.getHeroKeywords());

        SiteConfig config = new SiteConfig();
        config.setId(SITE_CONFIG_ID);

        config.setSiteTitle(
                validator.normalizeRequired(
                        dto.getSiteTitle(),
                        "站点标题不能为空"
                )
        );

        config.setHeroSubtitle(
                validator.normalizeOptional(dto.getHeroSubtitle())
        );

        config.setHeroKeywords(writeKeywords(keywords));

        config.setAuthorName(
                validator.normalizeRequired(
                        dto.getAuthorName(),
                        "博主展示名称不能为空"
                )
        );

        config.setAuthorBio(
                validator.normalizeOptional(dto.getAuthorBio())
        );

        config.setAvatarUrl(
                validator.normalizeUrl(
                        dto.getAvatarUrl(),
                        "头像URL格式不正确"
                )
        );

        config.setProfileBackgroundUrl(
                validator.normalizeUrl(
                        dto.getProfileBackgroundUrl(),
                        "资料卡背景URL格式不正确"
                )
        );

        config.setAnnouncement(
                validator.normalizeOptional(dto.getAnnouncement())
        );

        config.setGithubUrl(
                validator.normalizeUrl(
                        dto.getGithubUrl(),
                        "GitHub地址格式不正确"
                )
        );

        config.setGiteeUrl(
                validator.normalizeUrl(
                        dto.getGiteeUrl(),
                        "Gitee地址格式不正确"
                )
        );

        return config;
}
}