package com.myproject.devlog.pojo.vo;

import lombok.Data;

import java.util.List;

@Data
public class ArticleDetailVO {

    private Long id;

    private String title;

    private String content;

    private String summary;

    private String coverImage;

    private Long categoryId;

    private Integer status;

    private Long viewCount;

    private String createTime;

    private String updateTime;

    private UserInfoVO author;

    private String categoryName;

    private List<TagVO> tags;
}
