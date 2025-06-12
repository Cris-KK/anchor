# test/resources/test-data.sql
-- 插入测试主播数据
INSERT INTO anchor (anchor_id, nickname, status) VALUES
('anchor_001', '测试主播1', 1),
('anchor_002', '测试主播2', 1);

-- 插入测试观众数据
INSERT INTO audience (audience_id, nickname, gender) VALUES
                                                         ('audience_001', '测试观众1', 1),
                                                         ('audience_002', '测试观众2', 2),
                                                         ('audience_003', '测试观众3', 1);

-- 插入测试打赏记录
INSERT INTO reward_record (record_id, audience_id, anchor_id, amount, sync_status) VALUES
                                                                                       ('record_001', 'audience_001', 'anchor_001', 100.00, 1),
                                                                                       ('record_002', 'audience_002', 'anchor_001', 200.00, 1),
                                                                                       ('record_003', 'audience_003', 'anchor_001', 50.00, 0);