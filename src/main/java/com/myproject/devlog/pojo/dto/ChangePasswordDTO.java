package com.myproject.devlog.pojo.dto;

import lombok.Data;

@Data
public class ChangePasswordDTO {

    private String oldPassword;

    private String newPassword;
}
