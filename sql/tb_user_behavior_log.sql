-- ----------------------------
-- 实时数据验证表
-- ----------------------------
DROP TABLE IF EXISTS `tb_real_time_data_verification`;
CREATE TABLE `tb_real_time_data_verification` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_mobile` varchar(11) DEFAULT NULL COMMENT '用户手机号',
  `event_key` varchar(100) DEFAULT NULL COMMENT '请求类型',
  `client_time` datetime(3) DEFAULT NULL COMMENT '客户端时间',
  `event_type` varchar(50) DEFAULT NULL COMMENT '数据类型',
  `event_time` datetime(3) DEFAULT NULL COMMENT '服务端时间',
  `device_id` varchar(100) DEFAULT NULL COMMENT '设备ID',
  `session_id` varchar(100) DEFAULT NULL COMMENT '用户会话ID',
  `platform` varchar(50) DEFAULT NULL COMMENT '平台类型',
  `browser` varchar(100) DEFAULT NULL COMMENT '系统信息',
  `client_version` varchar(50) DEFAULT NULL COMMENT '客户端版本号',
  `domain` varchar(200) DEFAULT NULL COMMENT '域名',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '用户浏览器信息',
  `sdk_version` varchar(50) DEFAULT NULL COMMENT 'SDK版本',
  `data_source_id` varchar(100) DEFAULT NULL COMMENT '数据源ID',
  `attributes` text COMMENT '属性(JSON格式)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_mobile` (`user_mobile`),
  KEY `idx_event_time` (`event_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='实时数据验证表';

-- ----------------------------
-- 插入测试数据（手机号：18480655439）
-- ----------------------------
INSERT INTO `tb_real_time_data_verification` 
(`user_mobile`, `event_key`, `client_time`, `event_type`, `event_time`, `device_id`, `session_id`, `platform`, `browser`, `client_version`, `domain`, `ip`, `user_agent`, `sdk_version`, `data_source_id`, `attributes`, `create_time`) 
VALUES 
('18480655439', 'imp', '2026-01-27 12:00:00.000', 'CUSTOM', '2026-01-27 12:00:00.100', 'device001', 'session001', 'web', 'Chrome', '1.0.0', 'dev.coc.10086.cn', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '3.8.6', 'aba9de4ce446b2d2', '{"WT_es":"https://dev.coc.10086.cn/coc3/canvas/rightsmarket-h5-canvas/online/gyhyfilmlist"}', NOW()),

('18480655439', 'click', '2026-01-27 12:00:05.000', 'CUSTOM', '2026-01-27 12:00:05.200', 'device001', 'session001', 'web', 'Chrome', '1.0.0', 'dev.coc.10086.cn', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '3.8.6', 'aba9de4ce446b2d2', '{"WT_es":"https://dev.coc.10086.cn/coc3/canvas/rightsmarket-h5-canvas/online","button":"购买按钮"}', NOW()),

('18480655439', 'imp', '2026-01-27 12:00:10.000', 'CUSTOM', '2026-01-27 12:00:10.300', 'device001', 'session001', 'web', 'Chrome', '1.0.0', 'dev.coc.10086.cn', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '3.8.6', 'aba9de4ce446b2d2', '{"WT_es":"https://dev.coc.10086.cn/coc3/canvas/product-detail","product_id":"12345"}', NOW()),

('18480655439', 'page_view', '2026-01-27 12:00:15.000', 'CUSTOM', '2026-01-27 12:00:15.400', 'device001', 'session001', 'web', 'Chrome', '1.0.0', 'dev.coc.10086.cn', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '3.8.6', 'aba9de4ce446b2d2', '{"WT_es":"https://dev.coc.10086.cn/coc3/canvas/order-confirm","page":"确认订单页"}', NOW()),

('18480655439', 'submit', '2026-01-27 12:00:20.000', 'CUSTOM', '2026-01-27 12:00:20.500', 'device001', 'session001', 'web', 'Chrome', '1.0.0', 'dev.coc.10086.cn', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '3.8.6', 'aba9de4ce446b2d2', '{"WT_es":"https://dev.coc.10086.cn/coc3/canvas/order-success","order_id":"ORD20260127001"}', NOW()),

('13800138000', 'imp', '2026-01-27 12:05:00.000', 'CUSTOM', '2026-01-27 12:05:00.100', 'device002', 'session002', 'mobile', 'Safari', '1.0.0', 'dev.coc.10086.cn', '192.168.1.200', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X)', '3.8.6', 'bcd1ef5da557c3e3', '{"WT_es":"https://dev.coc.10086.cn/mobile/home"}', NOW());
