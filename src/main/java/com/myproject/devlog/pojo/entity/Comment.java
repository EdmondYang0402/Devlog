package com.myproject.devlog.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评论/回复 实体类
 */
@Data
public class Comment {

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 发表评论的用户ID
     */
    private Long userId;

    /**
     * 父评论ID（如果是回复别人的评论）
     */
    private Long parentId;

    /**
     * 被回复的用户ID（如果是楼中楼回复）
     */
    private Long replyUserId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 逻辑删除 (0: 未删除, 1: 已删除)
     * 对应数据库中的 tinyint
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
