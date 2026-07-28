package com.myproject.devlog.service;

import com.myproject.devlog.pojo.dto.ChangePasswordDTO;
import com.myproject.devlog.pojo.dto.UpdateUserDTO;
import com.myproject.devlog.pojo.dto.UserLoginDTO;
import com.myproject.devlog.pojo.dto.UserRegisterDTO;
import com.myproject.devlog.pojo.entity.User;
import com.myproject.devlog.pojo.vo.UserInfoVO;
import com.myproject.devlog.pojo.vo.UserLoginVO;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    UserLoginVO login(UserLoginDTO dto);

    void register(UserRegisterDTO dto);

    UserInfoVO getInfo(Long id);

    UserInfoVO updateProfile(UpdateUserDTO dto);

    void changePassword(ChangePasswordDTO dto);

    void logout(HttpServletRequest request);

    User getByUsername(String username);

    User getById(Long id);

    void updateStatus(Long userId, Integer status);
}
