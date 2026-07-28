package com.myproject.devlog.mapper;

import com.myproject.devlog.pojo.entity.ArticleTag;
import com.myproject.devlog.pojo.entity.Tag;
import com.myproject.devlog.pojo.vo.ArticleTagQueryVO;
import com.myproject.devlog.pojo.vo.TagVO;
import org.apache.ibatis.annotations.*;

import java.util.Collection;
import java.util.List;

@Mapper
public interface ArticleTagMapper {
    @Delete("DELETE FROM article_tag WHERE article_id = #{articleId}")
    int deleteByArticleId(Long articleId);

    @Insert("""
        <script>
        INSERT INTO article_tag(article_id, tag_id) VALUES
        <foreach collection="relations" item="relation" separator=",">
            (#{relation.articleId}, #{relation.tagId})
        </foreach>
        </script>
        """)
    int batchInsert(@Param("relations") Collection<ArticleTag> relations);

    @Select("""
        SELECT t.id, t.name
        FROM article_tag at
        JOIN tag t ON t.id = at.tag_id
        WHERE at.article_id = #{articleId}
        ORDER BY t.name ASC, t.id ASC
        """)
    List<TagVO> listTagsByArticleId(Long articleId);

    @Select("""
        <script>
        SELECT at.article_id AS articleId, t.id AS tagId, t.name AS tagName
        FROM article_tag at
        JOIN tag t ON t.id = at.tag_id
        WHERE at.article_id IN
        <foreach collection="articleIds" item="articleId" open="(" separator="," close=")">
            #{articleId}
        </foreach>
        ORDER BY at.article_id ASC, t.name ASC, t.id ASC
        </script>
        """)
    List<ArticleTagQueryVO> listTagsByArticleIds(@Param("articleIds") Collection<Long> articleIds);

    @Select("SELECT COUNT(*) FROM article_tag WHERE tag_id = #{tagId}")
    long countByTagId(Long tagId);

    @Select("""
    SELECT
        t.id,
        t.name,
        t.create_time,
        t.update_time
    FROM article_tag at
    INNER JOIN tag t ON t.id = at.tag_id
    WHERE at.article_id = #{articleId}
    ORDER BY t.id
    """)
    List<Tag> listByArticleId(Long articleId);
}
