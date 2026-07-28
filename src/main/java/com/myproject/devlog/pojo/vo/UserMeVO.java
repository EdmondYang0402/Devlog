package com.myproject.devlog.pojo.vo;

import lombok.Data;
//获取当前登录用户信息
@Data
public class UserMeVO {

    private Long id;

    private String username;

    private String email;

    private String avatar;

    private String bio;

    private Integer role;
}