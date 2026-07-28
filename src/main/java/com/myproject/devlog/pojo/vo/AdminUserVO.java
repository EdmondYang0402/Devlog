package com.myproject.devlog.pojo.vo;


//管理员用户列表VO
import lombok.Data;

@Data
public class AdminUserVO {

    private Long id;

    private String username;

    private String email;

    private Integer role;

    private Integer status;

    private String createTime;
}
