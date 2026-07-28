package com.myproject.devlog.service;

import com.myproject.devlog.pojo.dto.CommentCreateDTO;
import com.myproject.devlog.pojo.vo.CommentVO;

import java.util.List;

public interface CommentService {

    void create(CommentCreateDTO dto);

    void delete(Long id);

    List<CommentVO> listByArticleId(Long articleId);
}
