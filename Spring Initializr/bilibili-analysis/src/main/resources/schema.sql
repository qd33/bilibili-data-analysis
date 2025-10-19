-- 创建数据库
CREATE DATABASE IF NOT EXISTS bilibili_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE bilibili_db;

-- UP主表
CREATE TABLE IF NOT EXISTS up (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  uid VARCHAR(50) UNIQUE NOT NULL,
                                  name VARCHAR(255),
                                  avatar VARCHAR(500),
                                  follower_count BIGINT DEFAULT 0,
                                  created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 视频表（包含完整的统计字段）
CREATE TABLE IF NOT EXISTS video (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     bv_id VARCHAR(20) UNIQUE NOT NULL,
                                     title VARCHAR(500),
                                     cover_url VARCHAR(500),
                                     description TEXT,
                                     up_id BIGINT,
                                     publish_time DATETIME,
                                     video_partition VARCHAR(100),

    -- 统计字段
                                     play_count INT DEFAULT 0,
                                     like_count INT DEFAULT 0,
                                     danmaku_count INT DEFAULT 0,
                                     comment_count INT DEFAULT 0,
                                     coin_count INT DEFAULT 0,
                                     share_count INT DEFAULT 0,
                                     favorite_count INT DEFAULT 0,
                                     duration INT DEFAULT 0,

                                     created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                     FOREIGN KEY (up_id) REFERENCES up(id) ON DELETE CASCADE,
                                     INDEX idx_up_id (up_id),
                                     INDEX idx_publish_time (publish_time),
                                     INDEX idx_partition (video_partition)
);

-- UP主统计数据表
CREATE TABLE IF NOT EXISTS up_stat (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       up_id BIGINT,
                                       record_date DATE,
                                       follower_count BIGINT DEFAULT 0,
                                       total_view_count BIGINT DEFAULT 0,
                                       created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                       FOREIGN KEY (up_id) REFERENCES up(id) ON DELETE CASCADE,
                                       UNIQUE KEY unique_up_date (up_id, record_date),
                                       INDEX idx_record_date (record_date)
);

-- 显示表结构
DESC up;
DESC video;