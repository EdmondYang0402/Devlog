SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS site_config (
    id BIGINT PRIMARY KEY COMMENT '站点配置ID',
    site_title VARCHAR(100) NOT NULL COMMENT '站点标题',
    hero_subtitle VARCHAR(255) DEFAULT NULL COMMENT '首页诗句或副标题',
    hero_keywords VARCHAR(1000) DEFAULT NULL COMMENT '首页展示关键词JSON',
    author_name VARCHAR(50) NOT NULL COMMENT '博主展示名称',
    author_bio VARCHAR(500) DEFAULT NULL COMMENT '博主公开简介',
    avatar_url VARCHAR(500) DEFAULT NULL COMMENT '博主资料卡头像URL',
    profile_background_url VARCHAR(500) DEFAULT NULL COMMENT '博主资料卡背景URL',
    announcement VARCHAR(1000) DEFAULT NULL COMMENT '首页公告',
    github_url VARCHAR(500) DEFAULT NULL COMMENT 'GitHub链接',
    gitee_url VARCHAR(500) DEFAULT NULL COMMENT 'Gitee链接',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站点配置表';

INSERT INTO site_config (
    id, site_title, hero_subtitle, hero_keywords, author_name, author_bio, announcement
) VALUES (
    1, 'Hathaway''s Blog', '此身合是诗人未？细雨骑驴入剑门。',
    '["Spring Boot","安全开发","东京","随笔","Multi-Agent"]',
    'Hathaway', 'Java 后端 · 在日留学\n喜欢动漫和电影',
    '这里是 Hathaway 的个人博客，记录技术笔记、在日生活和读书感想。欢迎留言交流！'
) ON DUPLICATE KEY UPDATE id = id;
