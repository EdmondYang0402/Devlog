package com.myproject.devlog.mapper;

import com.myproject.devlog.pojo.entity.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Select("SELECT COUNT(*) FROM comment WHERE is_deleted = 0")
    Long countActive();
    @Insert("""
        INSERT INTO comment(
            article_id,
            user_id,
            parent_id,
            reply_user_id,
            content,
            is_deleted,
            create_time,
            update_time
        )
        VALUES(
            #{articleId},
            #{userId},
            #{parentId},
            #{replyUserId},
            #{content},
            0,
            NOW(),
            NOW()
        )
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Comment comment);

    @Select("""
        SELECT *
        FROM comment
        WHERE id = #{id}
          AND is_deleted = 0
    """)
    Comment getById(Long id);

    @Select("""
        SELECT *
        FROM comment
        WHERE article_id = #{articleId}
        ORDER BY create_time ASC
    """)
    List<Comment> listByArticleId(Long articleId);

    @Update("""
        UPDATE comment
        SET is_deleted = 1,
            update_time = NOW()
        WHERE id = #{id}
          AND is_deleted = 0
    """)
    void logicalDelete(Long id);

    @Delete("DELETE FROM comment WHERE article_id = #{articleId}")
    void deleteByArticleId(Long articleId);//因为删除文章后评论就没有意义了，所以根据文章删除所有评论

}
