package com.myproject.devlog.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.pojo.dto.ProjectCreateDTO;
import com.myproject.devlog.pojo.dto.ProjectUpdateDTO;
import com.myproject.devlog.pojo.entity.ProjectShowcase;
import com.myproject.devlog.pojo.vo.ProjectDetailVO;
import com.myproject.devlog.pojo.vo.ProjectListVO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ProjectConverter {
    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() { };

    private final ProjectValidator projectValidator;
    private final ObjectMapper objectMapper;

    public ProjectConverter(ProjectValidator projectValidator, ObjectMapper objectMapper) {
        this.projectValidator = projectValidator;
        this.objectMapper = objectMapper;
    }

    /** 新建时集中完成字段规范化，接口使用数组而数据库以 JSON 字符串紧凑保存技术栈。 */
    public ProjectShowcase fromCreateDTO(ProjectCreateDTO dto) {
        ProjectShowcase entity = new ProjectShowcase();
        copyEditableFields(entity, dto.getName(), dto.getSummary(), dto.getContent(), dto.getCoverUrl(),
                dto.getTechStack(), dto.getGithubUrl(), dto.getDemoUrl(), dto.getStatus(),
                dto.getStartedDate(), dto.getCompletedDate(), dto.getFeatured(), dto.getSortOrder());
        return entity;
    }

    /** 更新只覆盖可编辑字段，因此原实体的 id 与 createTime 会自然保留。 */
    public void applyUpdate(ProjectShowcase existing, ProjectUpdateDTO dto) {
        copyEditableFields(existing, dto.getName(), dto.getSummary(), dto.getContent(), dto.getCoverUrl(),
                dto.getTechStack(), dto.getGithubUrl(), dto.getDemoUrl(), dto.getStatus(),
                dto.getStartedDate(), dto.getCompletedDate(), dto.getFeatured(), dto.getSortOrder());
    }

    /** 列表接口把数据库 JSON 还原为数组，但不携带详细介绍正文。 */
    public ProjectListVO toListVO(ProjectShowcase entity) {
        ProjectListVO vo = new ProjectListVO();
        copyListFields(entity, vo);
        return vo;
    }

    /** 详情接口在列表字段基础上补充项目详细介绍。 */
    public ProjectDetailVO toDetailVO(ProjectShowcase entity) {
        ProjectDetailVO vo = new ProjectDetailVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setSummary(entity.getSummary());
        vo.setContent(entity.getContent());
        vo.setCoverUrl(entity.getCoverUrl());
        vo.setTechStack(readTechStack(entity.getTechStack()));
        vo.setGithubUrl(entity.getGithubUrl());
        vo.setDemoUrl(entity.getDemoUrl());
        vo.setStatus(entity.getStatus());
        vo.setStartedDate(entity.getStartedDate());
        vo.setCompletedDate(entity.getCompletedDate());
        vo.setFeatured(entity.getFeatured());
        vo.setSortOrder(entity.getSortOrder());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }

    private void copyEditableFields(ProjectShowcase entity, String name, String summary, String content,
                                    String coverUrl, List<String> techStack, String githubUrl, String demoUrl,
                                    Integer status, LocalDate startedDate, LocalDate completedDate,
                                    Integer featured, Integer sortOrder) {
        projectValidator.validateStatus(status);
        projectValidator.validateDates(startedDate, completedDate);
        entity.setName(projectValidator.normalizeName(name));
        entity.setSummary(projectValidator.normalizeSummary(summary));
        entity.setContent(projectValidator.normalizeContent(content));
        entity.setCoverUrl(projectValidator.normalizeCoverUrl(coverUrl));
        entity.setTechStack(writeTechStack(projectValidator.normalizeTechStack(techStack)));
        entity.setGithubUrl(projectValidator.normalizeGithubUrl(githubUrl));
        entity.setDemoUrl(projectValidator.normalizeDemoUrl(demoUrl));
        entity.setStatus(status);
        entity.setStartedDate(startedDate);
        entity.setCompletedDate(completedDate);
        entity.setFeatured(projectValidator.normalizeFeatured(featured));
        entity.setSortOrder(projectValidator.normalizeSortOrder(sortOrder));
    }

    private void copyListFields(ProjectShowcase entity, ProjectListVO vo) {
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setSummary(entity.getSummary());
        vo.setCoverUrl(entity.getCoverUrl());
        vo.setTechStack(readTechStack(entity.getTechStack()));
        vo.setGithubUrl(entity.getGithubUrl());
        vo.setDemoUrl(entity.getDemoUrl());
        vo.setStatus(entity.getStatus());
        vo.setStartedDate(entity.getStartedDate());
        vo.setCompletedDate(entity.getCompletedDate());
        vo.setFeatured(entity.getFeatured());
        vo.setSortOrder(entity.getSortOrder());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
    }

    private String writeTechStack(List<String> techStack) {
        if (techStack == null || techStack.isEmpty()) {
            return null;
        }
        try {
            String json = objectMapper.writeValueAsString(techStack);
            projectValidator.validateSerializedTechStack(json);
            return json;
        } catch (JsonProcessingException exception) {
            throw new BusinessException("技术栈数据序列化失败");
        }
    }

    private List<String> readTechStack(String techStackJson) {
        if (techStackJson == null || techStackJson.isBlank()) {
            return List.of();
        }
        try {
            List<String> values = objectMapper.readValue(techStackJson, STRING_LIST_TYPE);
            return projectValidator.normalizeTechStack(values);
        } catch (JsonProcessingException exception) {
            throw new BusinessException("技术栈数据解析失败");
        }
    }
}
