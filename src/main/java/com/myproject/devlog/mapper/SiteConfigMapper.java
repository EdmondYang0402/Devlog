package com.myproject.devlog.mapper;

import com.myproject.devlog.pojo.entity.SiteConfig;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SiteConfigMapper {
    @Select("""
            SELECT id, site_title AS siteTitle, hero_subtitle AS heroSubtitle,
                   hero_keywords AS heroKeywords, author_name AS authorName,
                   author_bio AS authorBio, avatar_url AS avatarUrl,
                   profile_background_url AS profileBackgroundUrl, announcement,
                   github_url AS githubUrl, gitee_url AS giteeUrl,
                   create_time AS createTime, update_time AS updateTime
            FROM site_config WHERE id = 1
            """)
    SiteConfig getConfig();

    @Insert("""
            INSERT INTO site_config
            (id, site_title, hero_subtitle, hero_keywords, author_name, author_bio,
             avatar_url, profile_background_url, announcement, github_url, gitee_url)
            VALUES (1, #{siteTitle}, #{heroSubtitle}, #{heroKeywords}, #{authorName}, #{authorBio},
                    #{avatarUrl}, #{profileBackgroundUrl}, #{announcement}, #{githubUrl}, #{giteeUrl})
            """)
    int insert(SiteConfig config);

    @Update("""
            UPDATE site_config SET site_title = #{siteTitle}, hero_subtitle = #{heroSubtitle},
                hero_keywords = #{heroKeywords}, author_name = #{authorName}, author_bio = #{authorBio},
                avatar_url = #{avatarUrl}, profile_background_url = #{profileBackgroundUrl},
                announcement = #{announcement}, github_url = #{githubUrl}, gitee_url = #{giteeUrl}
            WHERE id = 1
            """)
    int update(SiteConfig config);
}
