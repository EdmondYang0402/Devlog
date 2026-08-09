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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {
    private static final int DEFAULT_ADMIN_PAGE_SIZE = 10;
    private static final int DEFAULT_FRONT_PAGE_SIZE = 12;
    private static final int DEFAULT_FEATURED_LIMIT = 3;
    private static final int MAX_FEATURED_LIMIT = 10;

    private final ProjectMapper projectMapper;
    private final ProjectConverter projectConverter;

    public ProjectServiceImpl(ProjectMapper projectMapper,
                              ProjectConverter projectConverter) {
        this.projectMapper = projectMapper;
        this.projectConverter = projectConverter;
    }

    /** 新建项目时先完成规范化，再核对写库结果和数据库生成的主键。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ProjectCreateDTO dto) {
        ProjectShowcase entity = projectConverter.fromCreateDTO(dto);
        if (projectMapper.insert(entity) != 1 || entity.getId() == null) {
            throw new IllegalStateException("项目创建未返回预期写入结果");
        }
        return entity.getId();
    }

    /** PUT 使用全量更新语义，但保留数据库身份字段和创建时间。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, ProjectUpdateDTO dto) {
        ProjectShowcase existing = getRequiredProject(id);
        projectConverter.applyUpdate(existing, dto);
        if (projectMapper.updateById(existing) != 1) {
            throw new IllegalStateException("项目更新未影响预期记录数");
        }
    }

    /** 当前没有项目子表；删除只移除记录，不删除可能被复用的上传文件。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        getRequiredProject(id);
        if (projectMapper.deleteById(id) != 1) {
            throw new IllegalStateException("项目删除未影响预期记录数");
        }
    }

    @Override
    public ProjectDetailVO getAdminById(Long id) {
        return projectConverter.toDetailVO(getRequiredProject(id));
    }

    /** 后台分页允许关键词、状态和精选标记组合筛选，筛选与分页均在数据库完成。 */
    @Override
    public PageResult<ProjectListVO> pageAdmin(ProjectPageQueryDTO query) {
        ProjectPageQueryDTO normalized = query == null ? new ProjectPageQueryDTO() : query;
        int page = normalized.getPage() == null ? 1 : normalized.getPage();
        int size = normalized.getSize() == null ? DEFAULT_ADMIN_PAGE_SIZE : normalized.getSize();
        String keyword = normalizeKeyword(normalized.getKeyword());
        int offset = calculateOffset(page, size);
        List<ProjectListVO> records = projectMapper
                .selectAdminPage(offset, size, keyword, normalized.getStatus(), normalized.getFeatured())
                .stream().map(projectConverter::toListVO).toList();
        long total = projectMapper.countAdmin(keyword, normalized.getStatus(), normalized.getFeatured());
        return new PageResult<>(records, total);
    }

    @Override
    public ProjectDetailVO getById(Long id) {
        // V1 没有发布字段，归档项目仍属于可公开查看的历史成果。
        return projectConverter.toDetailVO(getRequiredProject(id));
    }

    /** 前台分页只按项目结构化状态与精选标记筛选，不引入内存过滤。 */
    @Override
    public PageResult<ProjectListVO> page(ProjectPageQueryDTO query) {
        ProjectPageQueryDTO normalized = query == null ? new ProjectPageQueryDTO() : query;
        int page = normalized.getPage() == null ? 1 : normalized.getPage();
        int size = normalized.getSize() == null ? DEFAULT_FRONT_PAGE_SIZE : normalized.getSize();
        int offset = calculateOffset(page, size);
        List<ProjectListVO> records = projectMapper
                .selectFrontPage(offset, size, normalized.getStatus(), normalized.getFeatured())
                .stream().map(projectConverter::toListVO).toList();
        long total = projectMapper.countFront(normalized.getStatus(), normalized.getFeatured());
        return new PageResult<>(records, total);
    }

    /** 精选数量限制为最多 10 条，避免固定推荐区域被大请求拖成完整列表。 */
    @Override
    public List<ProjectListVO> listFeatured(Integer limit) {
        int normalizedLimit = limit == null ? DEFAULT_FEATURED_LIMIT : limit;
        if (normalizedLimit < 1) {
            throw new BusinessException("精选项目数量不合法");
        }
        normalizedLimit = Math.min(normalizedLimit, MAX_FEATURED_LIMIT);
        return projectMapper.selectFeaturedList(normalizedLimit).stream()
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
        return keyword.strip();
    }
}
