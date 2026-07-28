package com.myproject.devlog.service;

import com.myproject.devlog.pojo.vo.TagVO;

import java.util.List;
import java.util.Map;

public interface ArticleTagService {
    void replaceArticleTags(Long articleId, List<Long> tagIds);
    List<TagVO> listByArticleId(Long articleId);
    Map<Long, List<TagVO>> listByArticleIds(List<Long> articleIds);
    void deleteByArticleId(Long articleId);
}
