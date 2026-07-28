package com.myproject.devlog.service;

import com.myproject.devlog.pojo.dto.SiteConfigUpdateDTO;
import com.myproject.devlog.pojo.vo.AdminSiteConfigVO;
import com.myproject.devlog.pojo.vo.SiteProfileVO;

public interface SiteConfigService {
    SiteProfileVO getPublicProfile();
    AdminSiteConfigVO getAdminConfig();
    void update(SiteConfigUpdateDTO dto);
}
