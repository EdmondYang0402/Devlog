package com.myproject.devlog.service.impl;

import com.myproject.devlog.common.BusinessException;
import com.myproject.devlog.common.PageResult;
import com.myproject.devlog.mapper.ProjectMapper;
import com.myproject.devlog.pojo.dto.ProjectCreateDTO;
import com.myproject.devlog.pojo.dto.ProjectPageQueryDTO;
import com.myproject.devlog.pojo.dto.ProjectUpdateDTO;
import com.myproject.devlog.pojo.entity.ProjectShowcase;
import com.myproject.devlog.pojo.vo.ProjectDetailVO;
import com.myproject.devlog.pojo.vo.ProjectListVO;
import com.myproject.devlog.service.ProjectService;
import com.myproject.devlog.utils.ProjectConverter;
import com.myproject.devlog.utils.ProjectValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {
    private static final int DEFAULT_ADMIN_PAGE_SIZE = 10;
    private static final int DEFAULT_FRONT_PAGE_SIZE = 12;
    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_FEATURED_LIMIT = 3;
    private static final int MAX_FEATURED_LIMIT = 10;

    private final ProjectMapper projectMapper;
    private final ProjectConverter projectConverter;
    private final ProjectValidator projectValidator;

    public ProjectServiceImpl(ProjectMapper projectMapper,
                              ProjectConverter projectConverter,
                              ProjectValidator projectValidator) {
        this.projectMapper = projectMapper;
        this.projectConverter = projectConverter;
        this.projectValidator = projectValidator;
    }

    /** 新建项目时先完成规范化，再核对写库结果和数据库生成的主键。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ProjectCreateDTO dto) {
        if (dto == null) {
            throw new BusinessException("项目信息不能为空");
        }
        ProjectShowcase entity = projectConverter.fromCreateDTO(dto);
        if (projectMapper.insert(entity) != 1 || entity.getId() == null) {
            throw new BusinessException("项目创建失败");
        }
        return entity.getId();
    }

    /** PUT 使用全量更新语义，但保留数据库身份字段和创建时间。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, ProjectUpdateDTO dto) {
        if (dto == null) {
            throw new BusinessException("项目信息不能为空");
        }
        ProjectShowcase existing = getRequiredProject(id);
        projectConverter.applyUpdate(existing, dto);
        if (projectMapper.update(existing) != 1) {
            throw new BusinessException("项目更新失败");
        }
    }

    /** 当前没有项目子表；删除只移除记录，不删除可能被复用的上传文件。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        getRequiredProject(id);
        if (projectMapper.deleteById(id) != 1) {
            throw new BusinessException("项目删除失败");
        }
    }

    @Override
    public ProjectDetailVO getAdminDetail(Long id) {
        return projectConverter.toDetailVO(getRequiredProject(id));
    }

    /** 后台分页允许关键词、状态和精选标记组合筛选，筛选与分页均在数据库完成。 */
    @Override
    public PageResult<ProjectListVO> adminPage(ProjectPageQueryDTO query) {
        ProjectPageQueryDTO normalized = query == null ? new ProjectPageQueryDTO() : query;
        int page = normalizePage(normalized.getPage());
        int size = normalizeSize(normalized.getSize(), DEFAULT_ADMIN_PAGE_SIZE);
        String keyword = normalizeKeyword(normalized.getKeyword());
        validateOptionalFilters(normalized.getStatus(), normalized.getFeatured());
        int offset = calculateOffset(page, size);
        List<ProjectListVO> records = projectMapper
                .adminPage(offset, size, keyword, normalized.getStatus(), normalized.getFeatured())
                .stream().map(projectConverter::toListVO).toList();
        long total = projectMapper.adminCount(keyword, normalized.getStatus(), normalized.getFeatured());
        return new PageResult<>(records, total);
    }

    @Override
    public ProjectDetailVO getFrontDetail(Long id) {
        // V1 没有发布字段，归档项目仍属于可公开查看的历史成果。
        return projectConverter.toDetailVO(getRequiredProject(id));
    }

    /** 前台分页只按项目结构化状态与精选标记筛选，不引入内存过滤。 */
    @Override
    public PageResult<ProjectListVO> frontPage(ProjectPageQueryDTO query) {
        ProjectPageQueryDTO normalized = query == null ? new ProjectPageQueryDTO() : query;
        int page = normalizePage(normalized.getPage());
        int size = normalizeSize(normalized.getSize(), DEFAULT_FRONT_PAGE_SIZE);
        validateOptionalFilters(normalized.getStatus(), normalized.getFeatured());
        int offset = calculateOffset(page, size);
        List<ProjectListVO> records = projectMapper
                .frontPage(offset, size, normalized.getStatus(), normalized.getFeatured())
                .stream().map(projectConverter::toListVO).toList();
        long total = projectMapper.frontCount(normalized.getStatus(), normalized.getFeatured());
        return new PageResult<>(records, total);
    }

    /** 精选数量限制为最多 10 条，避免固定推荐区域被大请求拖成完整列表。 */
    @Override
    public List<ProjectListVO> getFeaturedProjects(Integer limit) {
        int normalizedLimit = limit == null ? DEFAULT_FEATURED_LIMIT : limit;
        if (normalizedLimit < 1) {
            throw new BusinessException("精选项目数量不合法");
        }
        normalizedLimit = Math.min(normalizedLimit, MAX_FEATURED_LIMIT);
        return projectMapper.featuredList(normalizedLimit).stream()
                .map(projectConverter::toListVO)
                .toList();
    }

    /** 集中提供一致的 404 语义，供详情、更新和删除流程复用。 */
    private ProjectShowcase getRequiredProject(Long id) {
        if (id == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "项目不存在");
        }
        ProjectShowcase entity = projectMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "项目不存在");
        }
        return entity;
    }

    private void validateOptionalFilters(Integer status, Integer featured) {
        if (status != null) {
            projectValidator.validateStatus(status);
        }
        if (featured != null) {
            projectValidator.normalizeFeatured(featured);
        }
    }

    private int normalizePage(Integer page) {
        int normalized = page == null ? 1 : page;
        if (normalized < 1) {
            throw new BusinessException("分页参数不合法");
        }
        return normalized;
    }

    private int normalizeSize(Integer size, int defaultSize) {
        int normalized = size == null ? defaultSize : size;
        if (normalized < 1 || normalized > MAX_PAGE_SIZE) {
            throw new BusinessException("分页参数不合法");
        }
        return normalized;
    }

    private int calculateOffset(int page, int size) {
        long offset = (long) (page - 1) * size;
        if (offset > Integer.MAX_VALUE) {
            throw new BusinessException("分页参数不合法");
        }
        return (int) offset;
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        String normalized = keyword.strip();
        if (normalized.length() > 100) {
            throw new BusinessException("搜索关键词不能超过100个字符");
        }
        return normalized;
    }
}
