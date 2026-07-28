package com.myproject.devlog.pojo.entity;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    private String username;

    private String email;

    private String password; // BCrypt hash

    // 登录头像属于账号资料，URL 直接保存在 user 表；不与 site_config 的博客公开头像共用。
    private String avatar;

    //个人简介
    private String bio;

    private Integer role; // 0 普通用户 1 管理员

    private Integer status; // 0 正常 1 封禁

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
