# test/resources/test-schema.sql
CREATE TABLE IF NOT EXISTS audience (
                                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                        audience_id VARCHAR(32) NOT NULL UNIQUE,
    nickname VARCHAR(100) NOT NULL,
    gender TINYINT NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS anchor (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      anchor_id VARCHAR(32) NOT NULL UNIQUE,
    nickname VARCHAR(100) NOT NULL,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS reward_record (
                                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                             record_id VARCHAR(64) NOT NULL UNIQUE,
    audience_id VARCHAR(32) NOT NULL,
    anchor_id VARCHAR(32) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    reward_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sync_status TINYINT DEFAULT 0,
    trace_id VARCHAR(64),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

