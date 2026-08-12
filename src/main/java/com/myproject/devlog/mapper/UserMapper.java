package com.myproject.devlog.mapper;

import com.myproject.devlog.pojo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("select * from user where id =#{userid}")
    User selectById(Long userid);

    @Insert("INSERT INTO user (username, nickname, email, password, avatar, bio, role, status, create_time, update_time) VALUES " +
            "(#{username}, #{nickname}, #{email}, #{password}, #{avatar}, #{bio}, #{role}, #{status}, NOW(), NOW())")
    int insert(User user);

    @Update({
            "<script>",
            "UPDATE user",
            "<set>",
            "  <if test='username != null'>username = #{username},</if>",
            "  <if test='nickname != null'>nickname = #{nickname},</if>",
            "  <if test='email != null'>email = #{email},</if>",
            "  <if test='password != null'>password = #{password},</if>",
            "  <if test='avatar != null'>avatar = #{avatar},</if>",
            "  <if test='bio != null'>bio = #{bio},</if>",
            "  <if test='role != null'>role = #{role},</if>",
            "  <if test='status != null'>status = #{status},</if>",
            "  update_time = NOW()",
            "</set>",
            "WHERE id = #{id}",
            "</script>"
    })
    int updateById(User user);

    @Select("select * from user where username =#{username}")
    User selectByUsername(String username);

    @Select("select * from user where email =#{email}")
    User selectByEmail(String email);
}
