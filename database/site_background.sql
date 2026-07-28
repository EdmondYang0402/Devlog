SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS site_background (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    image_url VARCHAR(500) NOT NULL COMMENT '背景图片地址',
    title VARCHAR(100) DEFAULT NULL COMMENT '后台识别名称',
    enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：0否，1是',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序权重，数值越大越靠前',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_background_enabled_sort (enabled, sort_order),
    CONSTRAINT chk_site_background_enabled CHECK (enabled IN (0, 1))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='站点背景图片表';
