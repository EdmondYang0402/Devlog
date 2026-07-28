SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

-- DevLog 标签模块迁移脚本
-- article 与 tag 通过 article_tag 建立多对多关系；不向 article 表写入标签字符串或标签 ID 列表。
CREATE TABLE IF NOT EXISTS tag (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签ID',
    name VARCHAR(50) NOT NULL COMMENT '标签名称',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_tag_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

CREATE TABLE IF NOT EXISTS article_tag (
    article_id BIGINT NOT NULL COMMENT '文章ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (article_id, tag_id),
    KEY idx_article_tag_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- DevLog 现有业务表不使用数据库外键，关系一致性由 Service 事务维护。
-- 兼容曾执行过早期外键版本的数据库：仅在约束存在时移除，新的空库执行也不会报错。
SET @drop_article_fk = IF(
    EXISTS(
        SELECT 1 FROM information_schema.TABLE_CONSTRAINTS
        WHERE CONSTRAINT_SCHEMA = DATABASE()
          AND TABLE_NAME = 'article_tag'
          AND CONSTRAINT_NAME = 'fk_article_tag_article'
          AND CONSTRAINT_TYPE = 'FOREIGN KEY'
    ),
    'ALTER TABLE article_tag DROP FOREIGN KEY fk_article_tag_article',
    'SELECT 1'
);
PREPARE drop_article_fk_stmt FROM @drop_article_fk;
EXECUTE drop_article_fk_stmt;
DEALLOCATE PREPARE drop_article_fk_stmt;

SET @drop_tag_fk = IF(
    EXISTS(
        SELECT 1 FROM information_schema.TABLE_CONSTRAINTS
        WHERE CONSTRAINT_SCHEMA = DATABASE()
          AND TABLE_NAME = 'article_tag'
          AND CONSTRAINT_NAME = 'fk_article_tag_tag'
          AND CONSTRAINT_TYPE = 'FOREIGN KEY'
    ),
    'ALTER TABLE article_tag DROP FOREIGN KEY fk_article_tag_tag',
    'SELECT 1'
);
PREPARE drop_tag_fk_stmt FROM @drop_tag_fk;
EXECUTE drop_tag_fk_stmt;
DEALLOCATE PREPARE drop_tag_fk_stmt;
