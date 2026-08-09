SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

-- category_tag 表达分类与标签之间的知识关联；article_tag 继续独立表达文章实际使用的标签。
-- 项目现有关系表不使用数据库外键，关联一致性由 Service 事务维护。
CREATE TABLE IF NOT EXISTS category_tag (
    category_id BIGINT NOT NULL COMMENT '分类ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (category_id, tag_id),
    KEY idx_category_tag_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类标签关联表';
