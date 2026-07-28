package com.myproject.devlog.controller;

import com.myproject.devlog.common.Result;
import com.myproject.devlog.pojo.vo.SiteProfileVO;
import com.myproject.devlog.service.SiteConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/site")
public class SiteProfileController {
    private final SiteConfigService siteConfigService;

    public SiteProfileController(SiteConfigService siteConfigService) {
        this.siteConfigService = siteConfigService;
    }

    @GetMapping("/profile")
    public Result<SiteProfileVO> getProfile() {
        return Result.success(siteConfigService.getPublicProfile());
    }
}
