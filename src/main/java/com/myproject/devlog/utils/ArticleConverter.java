package com.myproject.devlog.utils;

import com.myproject.devlog.pojo.dto.ArticleCreateDTO;
import com.myproject.devlog.pojo.dto.ArticleUpdateDTO;
import com.myproject.devlog.pojo.entity.Article;
import com.myproject.devlog.pojo.vo.ArticleDetailVO;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

import static com.myproject.devlog.common.ArticleStatusConstant.DRAFT;

public class ArticleConverter {
    private ArticleConverter() {
    }

    public static Article fromCreateDTO(ArticleCreateDTO dto, Long authorId) {
        if (dto == null) {
            return null;
        }

        Article article = new Article();

        BeanUtils.copyProperties(dto, article);
        article.setAuthorId(authorId);
        article.setStatus(dto.getStatus() == null ? DRAFT : dto.getStatus());
        article.setViewCount(0L);
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        return article;
    }

    public static ArticleDetailVO toDetailVO(Article article) {
        if (article == null) {
            return null;
        }
        ArticleDetailVO vo = new ArticleDetailVO();
        BeanUtils.copyProperties(article, vo);
        return vo;
    }

    public static Article fromUpdateDTO(ArticleUpdateDTO dto) {
        if (dto == null) {
            return null;
        }
        Article article = new Article();
        BeanUtils.copyProperties(dto, article);
        return article;
    }
}
