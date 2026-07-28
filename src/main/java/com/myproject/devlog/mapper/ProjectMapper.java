package com.myproject.devlog.mapper;

import com.myproject.devlog.pojo.entity.ProjectShowcase;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProjectMapper {

    @Select("SELECT * FROM project_showcase WHERE id = #{id}")
    ProjectShowcase selectById(Long id);

    @Insert("""
            INSERT INTO project_showcase(
                name, summary, content, cover_url, tech_stack, github_url, demo_url,
                status, started_date, completed_date, featured, sort_order
            ) VALUES (
                #{name}, #{summary}, #{content}, #{coverUrl}, #{techStack}, #{githubUrl}, #{demoUrl},
                #{status}, #{startedDate}, #{completedDate}, #{featured}, #{sortOrder}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProjectShowcase entity);

    @Update("""
            UPDATE project_showcase
            SET name = #{name},
                summary = #{summary},
                content = #{content},
                cover_url = #{coverUrl},
                tech_stack = #{techStack},
                github_url = #{githubUrl},
                demo_url = #{demoUrl},
                status = #{status},
                started_date = #{startedDate},
                completed_date = #{completedDate},
                featured = #{featured},
                sort_order = #{sortOrder}
            WHERE id = #{id}
            """)
    int update(ProjectShowcase entity);

    @Delete("DELETE FROM project_showcase WHERE id = #{id}")
    int deleteById(Long id);

    /** 后台优先手动权重，再按最近更新与主键排序，方便管理员控制展示顺序。 */
    @Select("""
            <script>
            SELECT id, name, summary, cover_url, tech_stack, github_url, demo_url,
                   status, started_date, completed_date, featured, sort_order,
                   create_time, update_time
            FROM project_showcase
            <where>
                <if test="keyword != null">
                    AND (name LIKE CONCAT('%', #{keyword}, '%')
                         OR summary LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                <if test="status != null">
                    AND status = #{status}
                </if>
                <if test="featured != null">
                    AND featured = #{featured}
                </if>
            </where>
            ORDER BY sort_order DESC, update_time DESC, id DESC
            LIMIT #{offset}, #{size}
            </script>
            """)
    List<ProjectShowcase> adminPage(
            @Param("offset") Integer offset,
            @Param("size") Integer size,
            @Param("keyword") String keyword,
            @Param("status") Integer status,
            @Param("featured") Integer featured
    );

    @Select("""
            <script>
            SELECT COUNT(*) FROM project_showcase
            <where>
                <if test="keyword != null">
                    AND (name LIKE CONCAT('%', #{keyword}, '%')
                         OR summary LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                <if test="status != null">
                    AND status = #{status}
                </if>
                <if test="featured != null">
                    AND featured = #{featured}
                </if>
            </where>
            </script>
            """)
    long adminCount(
            @Param("keyword") String keyword,
            @Param("status") Integer status,
            @Param("featured") Integer featured
    );

    /**
     * 前台先展示精选和较高权重项目；有完成日期的记录优先，空日期放后，
     * 最后以完成日期和主键倒序保证分页顺序稳定。
     */
    @Select("""
            <script>
            SELECT id, name, summary, cover_url, tech_stack, github_url, demo_url,
                   status, started_date, completed_date, featured, sort_order,
                   create_time, update_time
            FROM project_showcase
            <where>
                <if test="status != null">
                    AND status = #{status}
                </if>
                <if test="featured != null">
                    AND featured = #{featured}
                </if>
            </where>
            ORDER BY featured DESC,
                     sort_order DESC,
                     CASE WHEN completed_date IS NULL THEN 1 ELSE 0 END,
                     completed_date DESC,
                     id DESC
            LIMIT #{offset}, #{size}
            </script>
            """)
    List<ProjectShowcase> frontPage(
            @Param("offset") Integer offset,
            @Param("size") Integer size,
            @Param("status") Integer status,
            @Param("featured") Integer featured
    );

    @Select("""
            <script>
            SELECT COUNT(*) FROM project_showcase
            <where>
                <if test="status != null">
                    AND status = #{status}
                </if>
                <if test="featured != null">
                    AND featured = #{featured}
                </if>
            </where>
            </script>
            """)
    long frontCount(@Param("status") Integer status, @Param("featured") Integer featured);

    /** 精选查询只按人工权重和主键排序，用于首页等固定数量的推荐区域。 */
    @Select("""
            SELECT id, name, summary, cover_url, tech_stack, github_url, demo_url,
                   status, started_date, completed_date, featured, sort_order,
                   create_time, update_time
            FROM project_showcase
            WHERE featured = 1
            ORDER BY sort_order DESC, id DESC
            LIMIT #{limit}
            """)
    List<ProjectShowcase> featuredList(@Param("limit") Integer limit);
}
