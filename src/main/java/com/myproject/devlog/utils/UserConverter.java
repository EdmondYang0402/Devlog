package com.myproject.devlog.utils;

import com.myproject.devlog.pojo.dto.UpdateUserDTO;
import com.myproject.devlog.pojo.entity.User;
import com.myproject.devlog.pojo.vo.UserInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public UserInfoVO toUserInfoVO(User user) {
        if (user == null) {
            return null;
        }
        UserInfoVO vo = new UserInfoVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    /** 资料更新只允许写入普通用户资料字段，账号身份和安全字段不参与映射。 */
    public User fromProfileUpdateDTO(UpdateUserDTO dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setNickname(dto.getNickname());
        user.setAvatar(dto.getAvatar());
        return user;
    }
}
