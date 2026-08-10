-- =============================================
-- DKD 新模块建表脚本
-- =============================================

-- 一、元数据管理表
CREATE TABLE IF NOT EXISTS `tb_coding_scheme_field_maintenance` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `field_name` varchar(255) DEFAULT NULL COMMENT '字段名称',
  `field_identification` varchar(255) DEFAULT NULL COMMENT '字段标识',
  `maintenance_time` datetime DEFAULT NULL COMMENT '维护时间',
  `apply_scope` varchar(255) DEFAULT NULL COMMENT '适用范围',
  `is_scope_h5` tinyint(1) DEFAULT 0 COMMENT '适用H5（0否 1是）',
  `is_scope_native` tinyint(1) DEFAULT 0 COMMENT '适用原生（0否 1是）',
  `is_scope_mini` tinyint(1) DEFAULT 0 COMMENT '适用小程序（0否 1是）',
  `is_required` tinyint(1) DEFAULT 0 COMMENT '是否必填（0否 1是）',
  `max_length` int DEFAULT NULL COMMENT '最大长度',
  `is_content_digit` tinyint(1) DEFAULT 0 COMMENT '含数字（0否 1是）',
  `is_content_letter` tinyint(1) DEFAULT 0 COMMENT '含字母（0否 1是）',
  `is_content_chinese` tinyint(1) DEFAULT 0 COMMENT '含中文（0否 1是）',
  `is_content_underscore` tinyint(1) DEFAULT 0 COMMENT '含下划线（0否 1是）',
  `business_description` text DEFAULT NULL COMMENT '业务描述',
  `technical_description` text DEFAULT NULL COMMENT '技术描述',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='编码方案字段维护表';

-- 二、自动补货任务表（确认是否存在）
CREATE TABLE IF NOT EXISTS `tb_job` (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) NOT NULL DEFAULT '' COMMENT '任务名称',
  `channel_id` bigint DEFAULT NULL COMMENT '渠道ID',
  `sku_id` bigint DEFAULT NULL COMMENT '商品ID',
  `alert_value` int DEFAULT NULL COMMENT '阈值',
  `status` tinyint(1) DEFAULT 0 COMMENT '状态（0正常 1暂停）',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  PRIMARY KEY (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自动补货任务表';
