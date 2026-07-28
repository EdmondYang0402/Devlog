-- 修复“UTF-8 字节曾按 MySQL latin1/cp1252 解码”后写回 utf8mb4 列的数据。
-- 执行前必须备份。此脚本不会由 Docker 自动执行，只用于已有数据卷的一次性修复。
SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
START TRANSACTION;

-- HEX() 只返回 ASCII，可避免旧库中 0900_ai_ci 与 unicode_ci 混用时的比较冲突。
SET @mojibake_hex_pattern = 'C383|C382|C3A2|C3A4|C3A5|C3A6|C3A7|C3A8|C3A9|C3AF';

UPDATE article SET
    title = IF(HEX(title) REGEXP @mojibake_hex_pattern, CONVERT(CAST(CONVERT(title USING latin1) AS BINARY) USING utf8mb4), title),
    summary = IF(HEX(summary) REGEXP @mojibake_hex_pattern, CONVERT(CAST(CONVERT(summary USING latin1) AS BINARY) USING utf8mb4), summary),
    content = IF(HEX(content) REGEXP @mojibake_hex_pattern, CONVERT(CAST(CONVERT(content USING latin1) AS BINARY) USING utf8mb4), content);

UPDATE category SET
    name = IF(HEX(name) REGEXP @mojibake_hex_pattern, CONVERT(CAST(CONVERT(name USING latin1) AS BINARY) USING utf8mb4), name),
    description = IF(HEX(description) REGEXP @mojibake_hex_pattern, CONVERT(CAST(CONVERT(description USING latin1) AS BINARY) USING utf8mb4), description);

UPDATE comment SET
    content = IF(HEX(content) REGEXP @mojibake_hex_pattern, CONVERT(CAST(CONVERT(content USING latin1) AS BINARY) USING utf8mb4), content);

UPDATE media_review SET
    title = IF(HEX(title) REGEXP @mojibake_hex_pattern, CONVERT(CAST(CONVERT(title USING latin1) AS BINARY) USING utf8mb4), title),
    short_review = IF(HEX(short_review) REGEXP @mojibake_hex_pattern, CONVERT(CAST(CONVERT(short_review USING latin1) AS BINARY) USING utf8mb4), short_review),
    content = IF(HEX(content) REGEXP @mojibake_hex_pattern, CONVERT(CAST(CONVERT(content USING latin1) AS BINARY) USING utf8mb4), content);

UPDATE site_background SET
    title = IF(HEX(title) REGEXP @mojibake_hex_pattern, CONVERT(CAST(CONVERT(title USING latin1) AS BINARY) USING utf8mb4), title);

UPDATE site_config SET
    site_title = IF(HEX(site_title) REGEXP @mojibake_hex_pattern, CONVERT(CAST(CONVERT(site_title USING latin1) AS BINARY) USING utf8mb4), site_title),
    hero_subtitle = IF(HEX(hero_subtitle) REGEXP @mojibake_hex_pattern, CONVERT(CAST(CONVERT(hero_subtitle USING latin1) AS BINARY) USING utf8mb4), hero_subtitle),
    hero_keywords = IF(HEX(hero_keywords) REGEXP @mojibake_hex_pattern, CONVERT(CAST(CONVERT(hero_keywords USING latin1) AS BINARY) USING utf8mb4), hero_keywords),
    author_name = IF(HEX(author_name) REGEXP @mojibake_hex_pattern, CONVERT(CAST(CONVERT(author_name USING latin1) AS BINARY) USING utf8mb4), author_name),
    author_bio = IF(HEX(author_bio) REGEXP @mojibake_hex_pattern, CONVERT(CAST(CONVERT(author_bio USING latin1) AS BINARY) USING utf8mb4), author_bio),
    announcement = IF(HEX(announcement) REGEXP @mojibake_hex_pattern, CONVERT(CAST(CONVERT(announcement USING latin1) AS BINARY) USING utf8mb4), announcement);

UPDATE tag SET
    name = IF(HEX(name) REGEXP @mojibake_hex_pattern, CONVERT(CAST(CONVERT(name USING latin1) AS BINARY) USING utf8mb4), name);

UPDATE user SET
    username = IF(HEX(username) REGEXP @mojibake_hex_pattern, CONVERT(CAST(CONVERT(username USING latin1) AS BINARY) USING utf8mb4), username),
    bio = IF(HEX(bio) REGEXP @mojibake_hex_pattern, CONVERT(CAST(CONVERT(bio USING latin1) AS BINARY) USING utf8mb4), bio);

COMMIT;

ALTER DATABASE devlog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE article CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE article_tag CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE category CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE comment CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE media_review CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE site_background CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE site_config CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE tag CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE user CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
