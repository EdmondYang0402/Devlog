package com.myproject.devlog.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryCreateDTO {
    @NotBlank
    @Size(max = 50)
    private String name;

    @Size(max = 255)
    private String description;

    private Integer sortOrder;
}
