package com.myproject.devlog.mapper;

import com.myproject.devlog.pojo.entity.MediaReview;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MediaReviewMapper {

    @Select("SELECT * FROM media_review WHERE id = #{id}")
    MediaReview selectById(Long id);

    @Insert("""
            INSERT INTO media_review(
                title, media_type, status, cover_url, rating,
                short_review, content, finished_date
            ) VALUES (
                #{title}, #{mediaType}, #{status}, #{coverUrl}, #{rating},
                #{shortReview}, #{content}, #{finishedDate}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MediaReview mediaReview);

    @Update("""
            UPDATE media_review
            SET title = #{title},
                media_type = #{mediaType},
                status = #{status},
                cover_url = #{coverUrl},
                rating = #{rating},
                short_review = #{shortReview},
                content = #{content},
                finished_date = #{finishedDate}
            WHERE id = #{id}
            """)
    int update(MediaReview mediaReview);

    @Delete("DELETE FROM media_review WHERE id = #{id}")
    int deleteById(Long id);

    /** 后台按标题、类型和状态动态筛选，并直接在数据库完成分页。 */
    @Select("""
            <script>
            SELECT id, title, media_type, status, cover_url, rating,
                   short_review, finished_date, create_time, update_time
            FROM media_review
            <where>
                <if test="title != null">
                    AND title LIKE CONCAT('%', #{title}, '%')
                </if>
                <if test="mediaType != null">
                    AND media_type = #{mediaType}
                </if>
                <if test="status != null">
                    AND status = #{status}
                </if>
            </where>
            ORDER BY create_time DESC, id DESC
            LIMIT #{offset}, #{size}
            </script>
            """)
    List<MediaReview> adminPage(
            @Param("offset") Integer offset,
            @Param("size") Integer size,
            @Param("title") String title,
            @Param("mediaType") Integer mediaType,
            @Param("status") Integer status
    );

    @Select("""
            <script>
            SELECT COUNT(*) FROM media_review
            <where>
                <if test="title != null">
                    AND title LIKE CONCAT('%', #{title}, '%')
                </if>
                <if test="mediaType != null">
                    AND media_type = #{mediaType}
                </if>
                <if test="status != null">
                    AND status = #{status}
                </if>
            </where>
            </script>
            """)
    long adminCount(
            @Param("title") String title,
            @Param("mediaType") Integer mediaType,
            @Param("status") Integer status
    );

    /**
     * 前台两种排序共用该查询。latest 将空日期放后；rating 还会先把空评分放后，
     * 再以完成日期和主键保证相同分数下的顺序稳定。
     */
    @Select("""
            <script>
            SELECT id, title, media_type, status, cover_url, rating,
                   short_review, finished_date, create_time, update_time
            FROM media_review
            <where>
                <if test="mediaType != null">
                    AND media_type = #{mediaType}
                </if>
            </where>
            <choose>
                <when test="sort == 'rating'">
                    ORDER BY CASE WHEN rating IS NULL THEN 1 ELSE 0 END,
                             rating DESC,
                             CASE WHEN finished_date IS NULL THEN 1 ELSE 0 END,
                             finished_date DESC,
                             id DESC
                </when>
                <otherwise>
                    ORDER BY CASE WHEN finished_date IS NULL THEN 1 ELSE 0 END,
                             finished_date DESC,
                             id DESC
                </otherwise>
            </choose>
            LIMIT #{offset}, #{size}
            </script>
            """)
    List<MediaReview> frontPage(
            @Param("offset") Integer offset,
            @Param("size") Integer size,
            @Param("mediaType") Integer mediaType,
            @Param("sort") String sort
    );

    @Select("""
            <script>
            SELECT COUNT(*) FROM media_review
            <where>
                <if test="mediaType != null">
                    AND media_type = #{mediaType}
                </if>
            </where>
            </script>
            """)
    long frontCount(@Param("mediaType") Integer mediaType);
}
