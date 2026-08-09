package com.myproject.devlog.service;

import com.myproject.devlog.common.PageResult;
import com.myproject.devlog.pojo.dto.ArticleCreateDTO;
import com.myproject.devlog.pojo.dto.ArticleUpdateDTO;
import com.myproject.devlog.pojo.vo.ArticleListVO;
import com.myproject.devlog.pojo.vo.ArticleDetailVO;

public interface AdminArticleService {
    void create(ArticleCreateDTO dto);
    void update(ArticleUpdateDTO dto);
    ArticleDetailVO getById(Long id);
    void delete(Long id);
    PageResult<ArticleListVO> page(Integer page, Integer size, String title, Integer status);
}
