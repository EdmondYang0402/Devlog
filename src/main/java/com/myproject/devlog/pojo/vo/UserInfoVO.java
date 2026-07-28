package com.myproject.devlog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoVO {

    private Long id;

    private String username;

    private String email;

    private String avatar;

    private String bio;

    private Integer role;

    private Integer status;
}

