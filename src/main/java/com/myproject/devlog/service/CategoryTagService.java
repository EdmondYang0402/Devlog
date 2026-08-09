package com.myproject.devlog.service;

import com.myproject.devlog.pojo.vo.TagVO;

import java.util.List;

public interface CategoryTagService {
    List<TagVO> listTagsByCategoryId(Long categoryId);

    void setTagsForCategory(Long categoryId, List<Long> tagIds);

    void deleteByCategoryId(Long categoryId);

    void deleteByTagId(Long tagId);
}
