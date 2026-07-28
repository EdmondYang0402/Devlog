package com.myproject.devlog.service;

import com.myproject.devlog.common.PageResult;
import com.myproject.devlog.pojo.dto.ArticleCreateDTO;
import com.myproject.devlog.pojo.dto.ArticleQueryDTO;
import com.myproject.devlog.pojo.dto.ArticleUpdateDTO;
import com.myproject.devlog.pojo.vo.ArticleDetailVO;
import com.myproject.devlog.pojo.vo.ArticleListVO;

import java.util.List;

public interface ArticleService {

    //void create(ArticleCreateDTO dto);   //拆分业务到admin...中    V1只留1分页 2详情 3搜索

    //void update(ArticleUpdateDTO dto);

    //void delete(Long id);

    ArticleDetailVO getById(Long id);

    ArticleDetailVO getFrontDetail(Long id);

    void increaseViewCount(Long id);

    PageResult<ArticleListVO> getFrontList(Integer page, Integer size, Long categoryId,
                                           String categorySlug, String keyword, List<Long> tagIds);
}
