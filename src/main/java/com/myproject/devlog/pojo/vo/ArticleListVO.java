package com.myproject.devlog.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleListVO {
    private Long id;

    private String title;

    private String summary;

    private String coverImage;

    private String category;

    private Long categoryId;

    private String categoryName;

    private Integer viewCount;

    private Long commentCount;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<TagVO> tags;
}
