package com.myproject.devlog.mapper;

import com.myproject.devlog.pojo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("select * from user where id =#{userid}")
    User getById(Long userid);

    @Insert("INSERT INTO user (username, email, password, avatar, bio, role, status, create_time, update_time) VALUES " +
            "(#{username}, #{email}, #{password}, #{avatar}, #{bio}, #{role}, #{status}, NOW(), NOW())")
    void insert(User user);

    @Update({
            "<script>",
            "UPDATE user",
            "<set>",
            "  <if test='username != null'>username = #{username},</if>",
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
    void update(User user);

    @Select("select * from user where username =#{username}")
    User getByUsername(String username);

    @Select("select * from user where email =#{email}")
    User getByEmail(String email);
}
