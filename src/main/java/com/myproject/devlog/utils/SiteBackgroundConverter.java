package com.myproject.devlog.utils;

import com.myproject.devlog.pojo.dto.SiteBackgroundCreateDTO;
import com.myproject.devlog.pojo.dto.SiteBackgroundUpdateDTO;
import com.myproject.devlog.pojo.entity.SiteBackground;
import com.myproject.devlog.pojo.vo.SiteBackgroundAdminVO;
import com.myproject.devlog.pojo.vo.SiteBackgroundPublicVO;
import org.springframework.stereotype.Component;

@Component
public class SiteBackgroundConverter {
    private final SiteBackgroundValidator validator;

    public SiteBackgroundConverter(SiteBackgroundValidator validator) {
        this.validator = validator;
    }

    public SiteBackground fromCreateDTO(SiteBackgroundCreateDTO dto) {
        SiteBackground entity = new SiteBackground();
        copyEditableFields(entity, dto.getImageUrl(), dto.getTitle(), dto.getEnabled(), dto.getSortOrder());
        return entity;
    }

    /** 只覆盖可编辑字段，因此已有记录的 id 和 createTime 会保持不变。 */
    public void applyUpdate(SiteBackground existing, SiteBackgroundUpdateDTO dto) {
        copyEditableFields(existing, dto.getImageUrl(), dto.getTitle(), dto.getEnabled(), dto.getSortOrder());
    }

    /** 后台需要状态、排序和审计时间，便于管理员识别与调整记录。 */
    public SiteBackgroundAdminVO toAdminVO(SiteBackground entity) {
        SiteBackgroundAdminVO vo = new SiteBackgroundAdminVO();
        vo.setId(entity.getId());
        vo.setImageUrl(entity.getImageUrl());
        vo.setTitle(entity.getTitle());
        vo.setEnabled(entity.getEnabled());
        vo.setSortOrder(entity.getSortOrder());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }

    /** 公开接口只暴露轮播所需字段，避免把后台状态和排序细节变成前台契约。 */
    public SiteBackgroundPublicVO toPublicVO(SiteBackground entity) {
        SiteBackgroundPublicVO vo = new SiteBackgroundPublicVO();
        vo.setId(entity.getId());
        vo.setImageUrl(entity.getImageUrl());
        vo.setTitle(entity.getTitle());
        return vo;
    }

    private void copyEditableFields(SiteBackground entity, String imageUrl, String title,
                                    Integer enabled, Integer sortOrder) {
        entity.setImageUrl(validator.normalizeImageUrl(imageUrl));
        entity.setTitle(validator.normalizeTitle(title));
        entity.setEnabled(validator.normalizeEnabled(enabled));
        entity.setSortOrder(validator.normalizeSortOrder(sortOrder));
    }
}
