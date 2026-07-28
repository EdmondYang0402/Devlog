package com.myproject.devlog.pojo.vo;

import lombok.Data;

@Data
public class ArticleTagQueryVO {
    private Long articleId;
    private Long tagId;
    private String tagName;
}
