package com.myproject.devlog.utils;

import com.myproject.devlog.pojo.dto.CommentCreateDTO;
import com.myproject.devlog.pojo.entity.Comment;
import com.myproject.devlog.pojo.entity.User;
import com.myproject.devlog.pojo.vo.CommentVO;
import org.springframework.beans.BeanUtils;

public class CommentConverter {

    private static final String DELETED_CONTENT = "该评论已删除";

    private CommentConverter() {
    }

    public static Comment fromCreateDTO(CommentCreateDTO dto, Long userId) {
        if (dto == null) {
            return null;
        }

        Comment comment = new Comment();
        BeanUtils.copyProperties(dto, comment);
        comment.setUserId(userId);
        comment.setContent(dto.getContent().trim());
        return comment;
    }

    public static CommentVO toVO(Comment comment, User author, User replyUser) {
        if (comment == null) {
            return null;
        }

        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(comment, vo);
        vo.setContent(Integer.valueOf(1).equals(comment.getIsDeleted())
                ? DELETED_CONTENT
                : comment.getContent());

        if (author != null) {
            vo.setUsername(author.getUsername());
            vo.setAvatar(author.getAvatar());
        }
        if (replyUser != null) {
            vo.setReplyUsername(replyUser.getUsername());
        }
        return vo;
    }
}
