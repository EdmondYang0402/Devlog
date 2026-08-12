package com.myproject.devlog.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserDTO {
    @NotBlank(message = "昵称不能为空")
    @Size(max = 30, message = "昵称不能超过30个字符")
    private String nickname;

    @Size(max = 500, message = "头像URL不能超过500个字符")
    private String avatar;
}
