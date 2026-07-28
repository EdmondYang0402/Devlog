package com.myproject.devlog.mapper;

import com.myproject.devlog.pojo.entity.SiteBackground;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SiteBackgroundMapper {

    @Select("SELECT * FROM site_background WHERE id = #{id}")
    SiteBackground selectById(Long id);

    @Insert("""
            INSERT INTO site_background(image_url, title, enabled, sort_order)
            VALUES (#{imageUrl}, #{title}, #{enabled}, #{sortOrder})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SiteBackground entity);

    @Update("""
            UPDATE site_background
            SET image_url = #{imageUrl}, title = #{title}, enabled = #{enabled}, sort_order = #{sortOrder}
            WHERE id = #{id}
            """)
    int update(SiteBackground entity);

    @Delete("DELETE FROM site_background WHERE id = #{id}")
    int deleteById(Long id);

    /** 后台优先显示较高权重，再按最近修改时间和主键倒序，方便管理员调整。 */
    @Select("""
            <script>
            SELECT id, image_url, title, enabled, sort_order, create_time, update_time
            FROM site_background
            <where>
                <if test="keyword != null">
                    AND title LIKE CONCAT('%', #{keyword}, '%')
                </if>
                <if test="enabled != null">
                    AND enabled = #{enabled}
                </if>
            </where>
            ORDER BY sort_order DESC, update_time DESC, id DESC
            LIMIT #{offset}, #{size}
            </script>
            """)
    List<SiteBackground> adminPage(@Param("offset") Integer offset,
                                   @Param("size") Integer size,
                                   @Param("keyword") String keyword,
                                   @Param("enabled") Integer enabled);

    @Select("""
            <script>
            SELECT COUNT(*) FROM site_background
            <where>
                <if test="keyword != null">
                    AND title LIKE CONCAT('%', #{keyword}, '%')
                </if>
                <if test="enabled != null">
                    AND enabled = #{enabled}
                </if>
            </where>
            </script>
            """)
    long adminCount(@Param("keyword") String keyword, @Param("enabled") Integer enabled);

    /** 前台只读取启用记录；sortOrder 越大越靠前，主键用于保证同权重下顺序稳定。 */
    @Select("""
            SELECT id, image_url, title
            FROM site_background
            WHERE enabled = 1
            ORDER BY sort_order DESC, id DESC
            """)
    List<SiteBackground> selectEnabledList();
}
