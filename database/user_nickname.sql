SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE user
    ADD COLUMN nickname VARCHAR(30) NULL AFTER username;

UPDATE user
SET nickname = username
WHERE nickname IS NULL OR TRIM(nickname) = '';

ALTER TABLE user
    MODIFY COLUMN nickname VARCHAR(30) NOT NULL;
