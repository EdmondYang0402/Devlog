package com.myproject.devlog.pojo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 5, max = 16, message = "用户名长度必须为 5~16 位")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 5, max = 16, message = "密码长度必须为 5~16 位")
    private String password;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 254, message = "邮箱长度不能超过 254 个字符")
    private String email;
}
