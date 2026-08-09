package com.myproject.devlog.service;

import com.myproject.devlog.common.PageResult;
import com.myproject.devlog.pojo.dto.MediaReviewCreateDTO;
import com.myproject.devlog.pojo.dto.MediaReviewUpdateDTO;
import com.myproject.devlog.pojo.vo.MediaReviewDetailVO;
import com.myproject.devlog.pojo.vo.MediaReviewListVO;

public interface MediaReviewService {
    void create(MediaReviewCreateDTO dto);

    void update(Long id, MediaReviewUpdateDTO dto);

    void delete(Long id);

    MediaReviewDetailVO getAdminById(Long id);

    PageResult<MediaReviewListVO> pageAdmin(Integer page, Integer size, String title,
                                             Integer mediaType, Integer status);

    MediaReviewDetailVO getById(Long id);

    PageResult<MediaReviewListVO> page(Integer page, Integer size,
                                                Integer mediaType, String sort);
}
