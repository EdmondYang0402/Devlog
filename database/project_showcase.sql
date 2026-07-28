SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS project_showcase (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    name VARCHAR(100) NOT NULL COMMENT '项目名称',
    summary VARCHAR(300) NOT NULL COMMENT '一句话简介',
    content TEXT DEFAULT NULL COMMENT '项目详细介绍',
    cover_url VARCHAR(500) DEFAULT NULL COMMENT '项目封面地址',
    tech_stack VARCHAR(1000) DEFAULT NULL COMMENT '技术栈，保存为JSON数组字符串',
    github_url VARCHAR(500) DEFAULT NULL COMMENT '代码仓库地址',
    demo_url VARCHAR(500) DEFAULT NULL COMMENT '在线演示地址',
    status TINYINT NOT NULL COMMENT '项目状态：0规划中，1开发中，2已完成，3持续维护，4已归档',
    started_date DATE DEFAULT NULL COMMENT '开始日期',
    completed_date DATE DEFAULT NULL COMMENT '完成日期',
    featured TINYINT NOT NULL DEFAULT 0 COMMENT '是否精选：0否，1是',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '手动排序权重，数值越大越靠前',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_project_status (status),
    INDEX idx_project_featured (featured),
    INDEX idx_project_sort_order (sort_order),
    CONSTRAINT chk_project_status CHECK (status BETWEEN 0 AND 4),
    CONSTRAINT chk_project_featured CHECK (featured IN (0, 1)),
    CONSTRAINT chk_project_dates CHECK (
        completed_date IS NULL OR started_date IS NULL OR completed_date >= started_date
    )
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='项目展示表';
