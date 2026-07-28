package com.myproject.devlog.mapper;

import com.myproject.devlog.pojo.entity.Article;
import com.myproject.devlog.pojo.vo.ArticleDetailVO;
import com.myproject.devlog.pojo.vo.ArticleListVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

import static com.myproject.devlog.common.ArticleStatusConstant.PUBLISHED_VALUE;

@Mapper
public interface ArticleMapper {

    @Select("SELECT COUNT(*) FROM article WHERE status = " + PUBLISHED_VALUE)
    Long countPublished();

    @Insert("""
        INSERT INTO article(title, summary, content, cover_image, author_id, category_id, status)
        VALUES (#{title}, #{summary}, #{content}, #{coverImage}, #{authorId}, #{categoryId}, #{status})
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Article article);

    @Update("""
        UPDATE article
        SET title = #{title},
            summary = #{summary},
            content = #{content},
            cover_image = #{coverImage},
            category_id = #{categoryId},
            status = #{status}
        WHERE id = #{id}
    """)
    void update(Article article);

    @Delete("DELETE FROM article WHERE id = #{id}")
    void deleteById(Long id);

    @Select("SELECT * FROM article WHERE id = #{id}")
    Article selectById(Long id);

    @Select("""
        <script>
        SELECT
            id,
            title,
            summary,
            cover_image AS coverImage,
            view_count AS viewCount,
            status,
            create_time AS createTime
        FROM article
        WHERE 1 = 1
        <if test="title != null and title != ''">
            AND title LIKE CONCAT('%', #{title}, '%')
        </if>
        <if test="status != null">
            AND status = #{status}
        </if>
        ORDER BY create_time DESC
        LIMIT #{offset}, #{size}
        </script>
    """)
    List<ArticleListVO> adminPage(
            @Param("offset") Integer offset,
            @Param("size") Integer size,
            @Param("title") String title,
            @Param("status") Integer status
    );

    // 前台文章列表（仅已发布，支持分类筛选）
    @Select("""
        <script>
        SELECT
            a.id,
            a.title,
            a.summary,
            a.cover_image AS coverImage,
            a.category_id AS categoryId,
            c.name AS categoryName,
            c.name AS category,
            a.view_count AS viewCount,
            (
                SELECT COUNT(*)
                FROM comment cm
                WHERE cm.article_id = a.id
                  AND cm.is_deleted = 0
            ) AS commentCount,
            a.create_time AS createTime,
            a.update_time AS updateTime
        FROM article a
        LEFT JOIN category c ON a.category_id = c.id
        WHERE a.status = """ + PUBLISHED_VALUE + """
        <if test="categoryId != null">
            AND a.category_id = #{categoryId}
        </if>
        <if test="categoryName != null and categoryName != ''">
            AND c.name = #{categoryName}
        </if>
        <if test="keyword != null and keyword != ''">
            AND (a.title LIKE CONCAT('%', #{keyword}, '%')
                 OR a.summary LIKE CONCAT('%', #{keyword}, '%')
                 OR EXISTS (
                    SELECT 1
                    FROM article_tag keyword_at
                    JOIN tag keyword_t ON keyword_t.id = keyword_at.tag_id
                    WHERE keyword_at.article_id = a.id
                      AND keyword_t.name LIKE CONCAT('%', #{keyword}, '%')
                 ))
        </if>
        ORDER BY a.create_time DESC
        LIMIT #{offset}, #{size}
        </script>
    """)
    List<ArticleListVO> frontPage(
            @Param("offset") Integer offset,
            @Param("size") Integer size,
            @Param("categoryId") Long categoryId,
            @Param("categoryName") String categoryName,
            @Param("keyword") String keyword
    );

    // 前台文章总数（配合 frontPage 一起用，条件必须完全一致）
    @Select("""
        <script>
        SELECT COUNT(*)
        FROM article a
        LEFT JOIN category c ON c.id = a.category_id
        WHERE a.status = """ + PUBLISHED_VALUE + """
        <if test="categoryId != null">
            AND a.category_id = #{categoryId}
        </if>
        <if test="categoryName != null and categoryName != ''">
            AND c.name = #{categoryName}
        </if>
        <if test="keyword != null and keyword != ''">
            AND (a.title LIKE CONCAT('%', #{keyword}, '%')
                 OR a.summary LIKE CONCAT('%', #{keyword}, '%')
                 OR EXISTS (
                    SELECT 1
                    FROM article_tag keyword_at
                    JOIN tag keyword_t ON keyword_t.id = keyword_at.tag_id
                    WHERE keyword_at.article_id = a.id
                      AND keyword_t.name LIKE CONCAT('%', #{keyword}, '%')
                 ))
        </if>
        </script>
    """)
    Integer countFront(@Param("categoryId") Long categoryId,
                       @Param("categoryName") String categoryName,
                       @Param("keyword") String keyword);

    @Select("""
        <script>
        SELECT
            a.id,
            a.title,
            a.summary,
            a.cover_image AS coverImage,
            a.category_id AS categoryId,
            c.name AS categoryName,
            c.name AS category,
            a.view_count AS viewCount,
            (
                SELECT COUNT(*) FROM comment cm
                WHERE cm.article_id = a.id AND cm.is_deleted = 0
            ) AS commentCount,
            a.create_time AS createTime,
            a.update_time AS updateTime
        FROM article a
        JOIN (
            SELECT at.article_id
            FROM article_tag at
            WHERE at.tag_id IN
            <foreach collection="tagIds" item="tagId" open="(" separator="," close=")">
                #{tagId}
            </foreach>
            GROUP BY at.article_id
            HAVING COUNT(DISTINCT at.tag_id) = #{tagCount}
        ) matched_tags ON matched_tags.article_id = a.id
        LEFT JOIN category c ON c.id = a.category_id
        WHERE a.status = """ + PUBLISHED_VALUE + """
        <if test="categoryId != null">
            AND a.category_id = #{categoryId}
        </if>
        <if test="categoryName != null and categoryName != ''">
            AND c.name = #{categoryName}
        </if>
        <if test="keyword != null and keyword != ''">
            AND (a.title LIKE CONCAT('%', #{keyword}, '%')
                 OR a.summary LIKE CONCAT('%', #{keyword}, '%')
                 OR EXISTS (
                    SELECT 1
                    FROM article_tag keyword_at
                    JOIN tag keyword_t ON keyword_t.id = keyword_at.tag_id
                    WHERE keyword_at.article_id = a.id
                      AND keyword_t.name LIKE CONCAT('%', #{keyword}, '%')
                 ))
        </if>
        ORDER BY a.create_time DESC
        LIMIT #{offset}, #{size}
        </script>
    """)
    List<ArticleListVO> frontPageByTagIds(
            @Param("offset") Integer offset,
            @Param("size") Integer size,
            @Param("categoryId") Long categoryId,
            @Param("categoryName") String categoryName,
            @Param("keyword") String keyword,
            @Param("tagIds") List<Long> tagIds,
            @Param("tagCount") Integer tagCount
    );

    @Select("""
        <script>
        SELECT COUNT(DISTINCT a.id)
        FROM article a
        JOIN (
            SELECT at.article_id
            FROM article_tag at
            WHERE at.tag_id IN
            <foreach collection="tagIds" item="tagId" open="(" separator="," close=")">
                #{tagId}
            </foreach>
            GROUP BY at.article_id
            HAVING COUNT(DISTINCT at.tag_id) = #{tagCount}
        ) matched_tags ON matched_tags.article_id = a.id
        LEFT JOIN category c ON c.id = a.category_id
        WHERE a.status = """ + PUBLISHED_VALUE + """
        <if test="categoryId != null">
            AND a.category_id = #{categoryId}
        </if>
        <if test="categoryName != null and categoryName != ''">
            AND c.name = #{categoryName}
        </if>
        <if test="keyword != null and keyword != ''">
            AND (a.title LIKE CONCAT('%', #{keyword}, '%')
                 OR a.summary LIKE CONCAT('%', #{keyword}, '%')
                 OR EXISTS (
                    SELECT 1
                    FROM article_tag keyword_at
                    JOIN tag keyword_t ON keyword_t.id = keyword_at.tag_id
                    WHERE keyword_at.article_id = a.id
                      AND keyword_t.name LIKE CONCAT('%', #{keyword}, '%')
                 ))
        </if>
        </script>
    """)
    Integer countFrontByTagIds(
            @Param("categoryId") Long categoryId,
            @Param("categoryName") String categoryName,
            @Param("keyword") String keyword,
            @Param("tagIds") List<Long> tagIds,
            @Param("tagCount") Integer tagCount
    );


    @Select("""
        <script>
        SELECT COUNT(*)
        FROM article
        WHERE 1 = 1
        <if test="title != null and title != ''">
            AND title LIKE CONCAT('%', #{title}, '%')
        </if>
        <if test="status != null">
            AND status = #{status}
        </if>
        </script>
    """)
    Long adminCount(
            @Param("title") String title,
            @Param("status") Integer status
    );


    // 1. 根据 ID 查询前台文章详情（附带内容 content 字段）
    @Select("""
    SELECT
        a.id,
        a.title,
        a.content,
        a.summary,
        a.cover_image AS coverImage,
        a.category_id AS categoryId,
        c.name AS categoryName,
        a.view_count AS viewCount,
        a.create_time AS createTime,
        a.update_time AS updateTime
    FROM article a
    LEFT JOIN category c ON c.id = a.category_id
    WHERE a.id = #{id} AND a.status = """ + PUBLISHED_VALUE + """
""")
    ArticleDetailVO selectFrontDetailById(@Param("id") Long id);

    // 2. 浏览量原子自增
    @Update("""
    UPDATE article 
    SET view_count = view_count + 1 
    WHERE id = #{id} AND status = """ + PUBLISHED_VALUE + """
""")
    int updateViewCount(@Param("id") Long id);
}
