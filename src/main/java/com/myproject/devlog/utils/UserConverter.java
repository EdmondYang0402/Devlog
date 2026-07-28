package com.myproject.devlog.utils;

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

    public User toUser(Object source) {
        if (source == null) {
            return null;
        }
        User user = new User();
        BeanUtils.copyProperties(source, user);
        return user;
    }
}
