package com.myproject.devlog.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ArticleTag {
    private Long articleId;
    private Long tagId;
    private LocalDateTime createTime;
}
