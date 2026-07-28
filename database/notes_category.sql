SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

-- MySQL 8.0：category.name 受唯一索引 uk_category_name 约束，表中没有 slug 或启用状态字段。
-- 因此使用唯一分类名“手记”作为数据库稳定标识，不依赖任何环境中的固定 categoryId。
-- 项目没有 Flyway/Liquibase 等自动迁移工具；部署或本地联调前需手动执行本文件。
INSERT INTO category (name, description, sort_order)
SELECT '手记', '项目开发、学习、生活与阶段思考', next_sort
FROM (
    SELECT COALESCE(MAX(sort_order), 0) + 10 AS next_sort
    FROM category
) AS category_sort
WHERE NOT EXISTS (
    SELECT 1 FROM category WHERE name = '手记'
);
