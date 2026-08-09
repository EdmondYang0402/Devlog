package com.myproject.devlog.service;

import com.myproject.devlog.common.PageResult;
import com.myproject.devlog.pojo.dto.SiteBackgroundCreateDTO;
import com.myproject.devlog.pojo.dto.SiteBackgroundPageQueryDTO;
import com.myproject.devlog.pojo.dto.SiteBackgroundUpdateDTO;
import com.myproject.devlog.pojo.vo.SiteBackgroundAdminVO;
import com.myproject.devlog.pojo.vo.SiteBackgroundPublicVO;

import java.util.List;

public interface SiteBackgroundService {
    Long create(SiteBackgroundCreateDTO dto);

    void update(Long id, SiteBackgroundUpdateDTO dto);

    void delete(Long id);

    SiteBackgroundAdminVO getAdminById(Long id);

    PageResult<SiteBackgroundAdminVO> pageAdmin(SiteBackgroundPageQueryDTO query);

    List<SiteBackgroundPublicVO> listEnabled();
}
