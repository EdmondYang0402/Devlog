package com.myproject.devlog.pojo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserDTO {
    private Long id;

    @Size(max = 500, message = "头像URL不能超过500个字符")
    private String avatar;

    @Size(max = 500, message = "个人简介不能超过500个字符")
    private String bio;

    @Email(message = "邮箱格式不正确")
    @Size(max = 255, message = "邮箱不能超过255个字符")
    private String email;
}
