package com.myproject.devlog.pojo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProjectCreateDTO {
    @NotBlank(message = "项目名称不能为空")
    @Size(max = 100, message = "项目名称不能超过100个字符")
    private String name;

    @NotBlank(message = "项目简介不能为空")
    @Size(max = 300, message = "项目简介不能超过300个字符")
    private String summary;

    private String content;

    @Size(max = 500, message = "封面地址不能超过500个字符")
    private String coverUrl;

    @Size(max = 20, message = "技术栈不能超过20项")
    private List<@Size(max = 50, message = "技术栈名称不能超过50个字符") String> techStack;

    @Size(max = 500, message = "GitHub地址不能超过500个字符")
    private String githubUrl;

    @Size(max = 500, message = "演示地址不能超过500个字符")
    private String demoUrl;

    @NotNull(message = "项目状态不能为空")
    @Min(value = 0, message = "项目状态非法")
    @Max(value = 4, message = "项目状态非法")
    private Integer status;

    private LocalDate startedDate;
    private LocalDate completedDate;

    @Min(value = 0, message = "精选状态非法")
    @Max(value = 1, message = "精选状态非法")
    private Integer featured;

    @Min(value = -100000, message = "排序值过小")
    @Max(value = 100000, message = "排序值过大")
    private Integer sortOrder;
}
