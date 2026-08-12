package com.myproject.devlog.utils;

import com.myproject.devlog.pojo.dto.ArticleUpdateDTO;
import com.myproject.devlog.pojo.dto.CommentCreateDTO;
import com.myproject.devlog.pojo.dto.UpdateUserDTO;
import com.myproject.devlog.pojo.entity.Article;
import com.myproject.devlog.pojo.entity.Comment;
import com.myproject.devlog.pojo.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SensitiveWriteMappingTests {

    @Test
    void profileUpdateUsesExplicitAllowlist() {
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setNickname("用户昵称");
        dto.setAvatar("https://example.test/avatar.webp");

        User result = new UserConverter().fromProfileUpdateDTO(dto);

        assertEquals(dto.getNickname(), result.getNickname());
        assertEquals(dto.getAvatar(), result.getAvatar());
        assertNull(result.getId());
        assertNull(result.getUsername());
        assertNull(result.getEmail());
        assertNull(result.getBio());
        assertNull(result.getPassword());
        assertNull(result.getRole());
        assertNull(result.getStatus());
        assertNull(result.getCreateTime());
        assertNull(result.getUpdateTime());
    }

    @Test
    void articleUpdateDoesNotPopulateSystemManagedFields() {
        ArticleUpdateDTO dto = new ArticleUpdateDTO();
        dto.setId(12L);
        dto.setTitle("标题");
        dto.setSummary("摘要");
        dto.setContent("正文");
        dto.setCoverImage("/uploads/article/cover.webp");
        dto.setCategoryId(3L);
        dto.setStatus(1);

        Article result = ArticleConverter.fromUpdateDTO(dto);

        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getTitle(), result.getTitle());
        assertEquals(dto.getSummary(), result.getSummary());
        assertEquals(dto.getContent(), result.getContent());
        assertEquals(dto.getCoverImage(), result.getCoverImage());
        assertEquals(dto.getCategoryId(), result.getCategoryId());
        assertEquals(dto.getStatus(), result.getStatus());
        assertNull(result.getAuthorId());
        assertNull(result.getViewCount());
        assertNull(result.getCreateTime());
        assertNull(result.getUpdateTime());
    }

    @Test
    void commentCreateOnlyAcceptsContentAndRelationshipFieldsFromDto() {
        CommentCreateDTO dto = new CommentCreateDTO();
        dto.setArticleId(5L);
        dto.setParentId(8L);
        dto.setContent("  评论内容  ");

        Comment result = CommentConverter.fromCreateDTO(dto, 7L);

        assertEquals(5L, result.getArticleId());
        assertEquals(8L, result.getParentId());
        assertEquals(7L, result.getUserId());
        assertEquals("评论内容", result.getContent());
        assertNull(result.getId());
        assertNull(result.getReplyUserId());
        assertNull(result.getIsDeleted());
        assertNull(result.getCreateTime());
        assertNull(result.getUpdateTime());
    }
}
