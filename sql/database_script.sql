
-- ==================== DB1: 观众服务数据库 ====================
DROP DATABASE IF EXISTS audience_db;
CREATE DATABASE audience_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE audience_db;

-- 观众信息表
CREATE TABLE audience (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                          audience_id VARCHAR(32) NOT NULL UNIQUE COMMENT '观众ID',
                          nickname VARCHAR(100) NOT NULL COMMENT '昵称',
                          gender TINYINT NOT NULL COMMENT '性别 1-男 2-女',
                          create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          INDEX idx_audience_id (audience_id),
                          INDEX idx_create_time (create_time)
) COMMENT '观众信息表' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 主播信息表
CREATE TABLE anchor (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                        anchor_id VARCHAR(32) NOT NULL UNIQUE COMMENT '主播ID',
                        nickname VARCHAR(100) NOT NULL COMMENT '昵称',
                        status TINYINT DEFAULT 1 COMMENT '状态 1-正常 0-禁用',
                        create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        INDEX idx_anchor_id (anchor_id),
                        INDEX idx_status (status)
) COMMENT '主播信息表' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 打赏记录表（观众服务专用）
CREATE TABLE reward_record (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                               record_id VARCHAR(64) NOT NULL UNIQUE COMMENT '打赏记录ID(业务幂等性)',
                               audience_id VARCHAR(32) NOT NULL COMMENT '观众ID',
                               anchor_id VARCHAR(32) NOT NULL COMMENT '主播ID',
                               amount DECIMAL(10,2) NOT NULL COMMENT '打赏金额',
                               reward_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '打赏时间',
                               sync_status TINYINT DEFAULT 0 COMMENT '同步状态 0-未同步 1-已同步 2-同步失败',
                               sync_retry_count INT DEFAULT 0 COMMENT '同步重试次数',
                               trace_id VARCHAR(64) COMMENT '链路追踪ID',
                               create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               INDEX idx_audience_id (audience_id),
                               INDEX idx_anchor_id (anchor_id),
                               INDEX idx_reward_time (reward_time),
                               INDEX idx_sync_status (sync_status),
                               INDEX idx_record_id (record_id),
                               INDEX idx_trace_id (trace_id),
                               INDEX idx_audience_anchor_amount (audience_id, anchor_id, amount DESC)
) COMMENT '打赏记录表' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================== DB2: 财务+经营分析服务共享数据库 ====================
DROP DATABASE IF EXISTS business_db;
CREATE DATABASE business_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE business_db;

-- ==================== 财务服务相关表 ====================

-- 主播分成配置表
CREATE TABLE anchor_commission_config (
                                          id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                          anchor_id VARCHAR(32) NOT NULL COMMENT '主播ID',
                                          commission_rate DECIMAL(5,4) NOT NULL COMMENT '分成比例 0.0000-1.0000',
                                          effective_time TIMESTAMP NOT NULL COMMENT '生效时间',
                                          expire_time TIMESTAMP NULL COMMENT '失效时间',
                                          create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          INDEX idx_anchor_effective (anchor_id, effective_time),
                                          INDEX idx_anchor_id (anchor_id),
                                          INDEX idx_effective_time (effective_time)
) COMMENT '主播分成配置表' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 财务结算记录表
CREATE TABLE settlement_record (
                                   id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                   record_id VARCHAR(64) NOT NULL UNIQUE COMMENT '原始打赏记录ID',
                                   audience_id VARCHAR(32) NOT NULL COMMENT '观众ID',
                                   anchor_id VARCHAR(32) NOT NULL COMMENT '主播ID',
                                   original_amount DECIMAL(10,2) NOT NULL COMMENT '原始打赏金额',
                                   commission_rate DECIMAL(5,4) NOT NULL COMMENT '使用的分成比例',
                                   platform_amount DECIMAL(10,2) NOT NULL COMMENT '平台收入',
                                   anchor_amount DECIMAL(10,2) NOT NULL COMMENT '主播收入',
                                   settlement_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '结算时间',
                                   status TINYINT DEFAULT 1 COMMENT '状态 1-正常 0-作废',
                                   trace_id VARCHAR(64) COMMENT '链路追踪ID',
                                   create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   INDEX idx_anchor_id (anchor_id),
                                   INDEX idx_record_id (record_id),
                                   INDEX idx_settlement_time (settlement_time),
                                   INDEX idx_trace_id (trace_id)
) COMMENT '财务结算记录表' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 主播余额表
CREATE TABLE anchor_balance (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                anchor_id VARCHAR(32) NOT NULL UNIQUE COMMENT '主播ID',
                                available_amount DECIMAL(12,2) DEFAULT 0 COMMENT '可提取金额',
                                total_income DECIMAL(12,2) DEFAULT 0 COMMENT '总收入',
                                withdrawn_amount DECIMAL(12,2) DEFAULT 0 COMMENT '已提取金额',
                                create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                INDEX idx_anchor_id (anchor_id),
                                INDEX idx_update_time (update_time)
) COMMENT '主播余额表' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================== 经营分析服务相关表 ====================

-- 观众标签表
CREATE TABLE audience_tag (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                              audience_id VARCHAR(32) NOT NULL UNIQUE COMMENT '观众ID',
                              tag_level VARCHAR(20) NOT NULL COMMENT '标签等级 high/medium/low',
                              total_amount DECIMAL(12,2) DEFAULT 0 COMMENT '总消费金额',
                              rank_percentage DECIMAL(5,2) COMMENT '消费排名百分比',
                              last_calc_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后计算时间',
                              create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              INDEX idx_audience_id (audience_id),
                              INDEX idx_tag_level (tag_level),
                              INDEX idx_last_calc_time (last_calc_time)
) COMMENT '观众标签表' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 小时维度分析表
CREATE TABLE hourly_analysis (
                                 id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                 anchor_id VARCHAR(32) NOT NULL COMMENT '主播ID',
                                 gender TINYINT NOT NULL COMMENT '观众性别 1-男 2-女',
                                 hour_time VARCHAR(2) NOT NULL COMMENT '小时时间 HH格式',
                                 date_time DATE NOT NULL COMMENT '日期',
                                 total_amount DECIMAL(12,2) DEFAULT 0 COMMENT '该小时总打赏金额',
                                 total_count INT DEFAULT 0 COMMENT '该小时打赏次数',
                                 create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 UNIQUE KEY uk_anchor_gender_hour_date (anchor_id, gender, hour_time, date_time),
                                 INDEX idx_anchor_date (anchor_id, date_time),
                                 INDEX idx_gender_date (gender, date_time),
                                 INDEX idx_date_time (date_time)
) COMMENT '小时维度分析表' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 打赏数据同步表（供经营分析服务使用）
CREATE TABLE reward_data_sync (
                                  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                  record_id VARCHAR(64) NOT NULL UNIQUE COMMENT '打赏记录ID',
                                  audience_id VARCHAR(32) NOT NULL COMMENT '观众ID',
                                  anchor_id VARCHAR(32) NOT NULL COMMENT '主播ID',
                                  amount DECIMAL(10,2) NOT NULL COMMENT '打赏金额',
                                  gender TINYINT NOT NULL COMMENT '观众性别 1-男 2-女',
                                  reward_time TIMESTAMP NOT NULL COMMENT '打赏时间',
                                  sync_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '同步时间',
                                  processed TINYINT DEFAULT 0 COMMENT '是否已处理 0-未处理 1-已处理',
                                  trace_id VARCHAR(64) COMMENT '链路追踪ID',
                                  INDEX idx_record_id (record_id),
                                  INDEX idx_anchor_id (anchor_id),
                                  INDEX idx_audience_id (audience_id),
                                  INDEX idx_reward_time (reward_time),
                                  INDEX idx_processed (processed),
                                  INDEX idx_anchor_gender_hour (anchor_id, gender, reward_time)
) COMMENT '打赏数据同步表' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================== 插入测试数据 ====================

-- 插入观众服务测试数据
USE audience_db;
INSERT INTO anchor (anchor_id, nickname, status) VALUES
                                                     ('anchor_001', '主播小美', 1),
                                                     ('anchor_002', '主播小帅', 1),
                                                     ('anchor_003', '主播小可爱', 1),
                                                     ('anchor_004', '主播小酷', 1),
                                                     ('anchor_005', '主播小萌', 1);

INSERT INTO audience (audience_id, nickname, gender) VALUES
                                                         ('audience_001', '观众小王', 1),
                                                         ('audience_002', '观众小李', 2),
                                                         ('audience_003', '观众小张', 1),
                                                         ('audience_004', '观众小赵', 2),
                                                         ('audience_005', '观众小陈', 1);

-- 批量插入更多观众数据
INSERT INTO audience (audience_id, nickname, gender)
SELECT
    CONCAT('audience_', LPAD(id + 5, 3, '0')) as audience_id,
    CONCAT('观众', id + 5) as nickname,
    (id % 2) + 1 as gender
FROM (
         SELECT @row := @row + 1 as id
         FROM (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4) t1,
             (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4) t2,
             (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4) t3,
             (SELECT @row := 0) r
             LIMIT 95
     ) numbers;

-- 插入财务和分析服务测试数据
USE business_db;
INSERT INTO anchor_commission_config (anchor_id, commission_rate, effective_time) VALUES
                                                                                      ('anchor_001', 0.7000, '2025-01-01 00:00:00'),
                                                                                      ('anchor_002', 0.6500, '2025-01-01 00:00:00'),
                                                                                      ('anchor_003', 0.7500, '2025-01-01 00:00:00'),
                                                                                      ('anchor_004', 0.6000, '2025-01-01 00:00:00'),
                                                                                      ('anchor_005', 0.8000, '2025-01-01 00:00:00');

INSERT INTO anchor_balance (anchor_id, available_amount, total_income, withdrawn_amount) VALUES
                                                                                             ('anchor_001', 0.00, 0.00, 0.00),
                                                                                             ('anchor_002', 0.00, 0.00, 0.00),
                                                                                             ('anchor_003', 0.00, 0.00, 0.00),
                                                                                             ('anchor_004', 0.00, 0.00, 0.00),
                                                                                             ('anchor_005', 0.00, 0.00, 0.00);

-- ==================== 验证数据库创建 ====================
SELECT 'audience_db tables:' as info;
USE audience_db;
SHOW TABLES;

SELECT 'business_db tables:' as info;
USE business_db;
SHOW TABLES;

SELECT 'Test data verification:' as info;
USE audience_db;
SELECT COUNT(*) as anchor_count FROM anchor;
SELECT COUNT(*) as audience_count FROM audience;

USE business_db;
SELECT COUNT(*) as commission_config_count FROM anchor_commission_config;
SELECT COUNT(*) as anchor_balance_count FROM anchor_balance;