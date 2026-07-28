package com.myproject.devlog.pojo.vo;
import lombok.Data;

//登录成功返回用户信息


@Data
public class UserLoginVO {

    private Long id;

    private String username;

    private String avatar;

    private String token;

    public UserLoginVO(Long userId, String token) {
        this.id = userId;
        this.token = token;
    }
}
