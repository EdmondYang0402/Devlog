package com.myproject.devlog.controller;

import com.myproject.devlog.common.Result;
import com.myproject.devlog.common.UserContext;
import com.myproject.devlog.pojo.dto.ChangePasswordDTO;
import com.myproject.devlog.pojo.dto.UpdateUserDTO;
import com.myproject.devlog.pojo.dto.UserLoginDTO;
import com.myproject.devlog.pojo.dto.UserRegisterDTO;
import com.myproject.devlog.pojo.vo.UserInfoVO;
import com.myproject.devlog.pojo.vo.UserLoginVO;
import com.myproject.devlog.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody UserRegisterDTO dto) {
        // 注册诊断仅记录用户名和邮箱，不记录明文密码。
        log.info("Register request entered controller: username={}, email={}",
                dto.getUsername(), dto.getEmail());
        userService.register(dto);
        log.info("Register request completed: username={}", dto.getUsername());
        return Result.success();
    }

    @GetMapping("/info/{id}")
    public Result<UserInfoVO> getUserInfo(@PathVariable Long id) {
        return Result.success(userService.getInfo(id));
    }

    @GetMapping("/me")
    public Result<UserInfoVO> getCurrentUser() {
        return Result.success(userService.getInfo(UserContext.get()));
    }

    @PutMapping("/profile")
    public Result<UserInfoVO> updateProfile(@Valid @RequestBody UpdateUserDTO dto) {
        // 资料更新只能使用登录上下文中的用户 ID，不能信任前端传入的 ID。
        dto.setId(UserContext.get());
        return Result.success(userService.updateProfile(dto));
    }

    @PatchMapping("/password")
    public Result<Void> updatePassword(@RequestBody ChangePasswordDTO dto) {
        if (dto.getNewPassword() == null || dto.getOldPassword() == null) {
            return Result.error("新旧密码都不能为空");
        }
        userService.changePassword(dto);
        return Result.success();
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        userService.logout(request);
        return Result.success();
    }
}
