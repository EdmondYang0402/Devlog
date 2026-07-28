package com.myproject.devlog.controller;

import com.myproject.devlog.common.Result;
import com.myproject.devlog.pojo.dto.SiteConfigUpdateDTO;
import com.myproject.devlog.pojo.vo.AdminSiteConfigVO;
import com.myproject.devlog.service.SiteConfigService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/site")
public class AdminSiteConfigController {
    private final SiteConfigService siteConfigService;

    public AdminSiteConfigController(SiteConfigService siteConfigService) {
        this.siteConfigService = siteConfigService;
    }

    @GetMapping("/profile")
    public Result<AdminSiteConfigVO> getProfile() {
        return Result.success(siteConfigService.getAdminConfig());
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@Valid @RequestBody SiteConfigUpdateDTO dto) {
        siteConfigService.update(dto);
        return Result.success();
    }
}
