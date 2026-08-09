package com.myproject.devlog.service.impl;

import com.myproject.devlog.mapper.ArticleMapper;
import com.myproject.devlog.mapper.CategoryMapper;
import com.myproject.devlog.mapper.CommentMapper;
import com.myproject.devlog.pojo.vo.StatisticsVO;
import com.myproject.devlog.service.StatisticsService;
import org.springframework.stereotype.Service;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final ArticleMapper articleMapper;
    private final CommentMapper commentMapper;
    private final CategoryMapper categoryMapper;

    public StatisticsServiceImpl(
            ArticleMapper articleMapper,
            CommentMapper commentMapper,
            CategoryMapper categoryMapper) {
        this.articleMapper = articleMapper;
        this.commentMapper = commentMapper;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public StatisticsVO getProfileStatistics() {
        StatisticsVO statistics = new StatisticsVO();
        statistics.setArticleCount(articleMapper.countPublished());
        statistics.setCommentCount(commentMapper.countActive());
        statistics.setCategoryCount(categoryMapper.count());
        return statistics;
    }
}
