package com.myproject.devlog.service.impl;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.mapper.SiteConfigMapper;
import com.myproject.devlog.pojo.dto.SiteConfigUpdateDTO;
import com.myproject.devlog.pojo.entity.SiteConfig;
import com.myproject.devlog.pojo.vo.AdminSiteConfigVO;
import com.myproject.devlog.pojo.vo.SiteProfileVO;
import com.myproject.devlog.service.SiteConfigService;
import com.myproject.devlog.utils.SiteConfigConverter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class SiteConfigServiceImpl implements SiteConfigService {
    private final SiteConfigMapper siteConfigMapper;
    private final SiteConfigConverter siteConfigConverter;

    public SiteConfigServiceImpl(SiteConfigMapper siteConfigMapper,
                                 SiteConfigConverter siteConfigConverter) {
        this.siteConfigMapper = siteConfigMapper;
        this.siteConfigConverter = siteConfigConverter;
    }

    @Override
    public SiteProfileVO getPublicProfile() {
        SiteConfig config = checkConfig();
        return siteConfigConverter.toPublicVO(config);
    }

    @Override
    public AdminSiteConfigVO getAdminConfig() {
        SiteConfig config = checkConfig();
        return siteConfigConverter.toAdminVO(config);
    }

    @Override
    @Transactional
    public void update(SiteConfigUpdateDTO dto) {
        SiteConfig config = siteConfigConverter.fromUpdateDTO(dto);

        int affected = siteConfigMapper.selectConfig() == null
                ? siteConfigMapper.insert(config)
                : siteConfigMapper.updateById(config);

        if (affected != 1) {
            throw new IllegalStateException("站点配置保存未影响预期记录数");
        }
    }

    private SiteConfig checkConfig(){
        SiteConfig config = siteConfigMapper.selectConfig();
        if (config == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "站点配置不存在");
        }
        return config;
    }


}
