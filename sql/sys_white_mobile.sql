-- 用户行为轨迹查询手机号白名单表
-- 在 dkd 数据库中执行此SQL

CREATE TABLE IF NOT EXISTS `sys_white_mobile` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `mobile` VARCHAR(20) NOT NULL COMMENT '手机号码',
    `mobile_status` CHAR(1) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `is_delete` CHAR(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0未删除 1已删除）',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_mobile` (`mobile`) COMMENT '手机号唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='手机号白名单表';

-- 插入测试数据（可选）
-- INSERT INTO `sys_white_mobile` (`mobile`, `mobile_status`, `is_delete`, `create_by`, `remark`) VALUES
-- ('13800138000', '0', '0', 'admin', '测试白名单手机号');