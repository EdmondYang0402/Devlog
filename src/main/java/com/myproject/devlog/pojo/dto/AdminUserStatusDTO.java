package com.myproject.devlog.pojo.dto;

import lombok.Data;

@Data
public class AdminUserStatusDTO {

    private Long userId;

    private Integer status; // 0 正常 1 封禁
}
