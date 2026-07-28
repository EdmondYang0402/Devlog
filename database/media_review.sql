SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

-- DevLog 作品档案（Media Review）模块迁移脚本
CREATE TABLE IF NOT EXISTS media_review (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    title VARCHAR(200) NOT NULL COMMENT '作品名称',
    media_type TINYINT NOT NULL COMMENT '类型：0书籍，1电影，2番剧，3游戏',
    status TINYINT NOT NULL COMMENT '状态：0计划，1进行中，2已完成，3搁置',
    cover_url VARCHAR(500) DEFAULT NULL COMMENT '封面URL',
    rating TINYINT DEFAULT NULL COMMENT '个人评分，1-10整数；1分对应半星，10分对应五星',
    short_review VARCHAR(500) DEFAULT NULL COMMENT '短评，用于列表和时间轴',
    content TEXT DEFAULT NULL COMMENT '详细评价',
    finished_date DATE DEFAULT NULL COMMENT '完成日期',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    CONSTRAINT chk_media_review_rating CHECK (rating IS NULL OR rating BETWEEN 1 AND 10),
    INDEX idx_media_type (media_type),
    INDEX idx_status (status),
    INDEX idx_finished_date (finished_date),
    INDEX idx_rating (rating)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='作品档案表';

-- CREATE TABLE IF NOT EXISTS 不会修补已经存在的表，因此为旧环境按需补上评分约束。
SET @rating_constraint_exists = (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'media_review'
      AND CONSTRAINT_NAME = 'chk_media_review_rating'
      AND CONSTRAINT_TYPE = 'CHECK'
);
SET @rating_constraint_sql = IF(
    @rating_constraint_exists = 0,
    'ALTER TABLE media_review ADD CONSTRAINT chk_media_review_rating CHECK (rating IS NULL OR rating BETWEEN 1 AND 10)',
    'SELECT 1'
);
PREPARE rating_constraint_statement FROM @rating_constraint_sql;
EXECUTE rating_constraint_statement;
DEALLOCATE PREPARE rating_constraint_statement;
