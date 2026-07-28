package com.myproject.devlog.pojo.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminTagVO {
    private Long id;
    private String name;
    private Long articleCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
