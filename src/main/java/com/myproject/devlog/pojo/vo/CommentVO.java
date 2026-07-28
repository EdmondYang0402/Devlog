package com.myproject.devlog.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommentVO {

    private Long id;

    private String content;

    private Integer isDeleted;

    private Long userId;

    private String username;

    private String avatar;

    private Long replyUserId;

    private String replyUsername;

    private LocalDateTime createTime;

    private List<CommentVO> replies = new ArrayList<>();
}
