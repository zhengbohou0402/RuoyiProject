-- =============================================
-- DKD 帝可得管理系统 - 业务表建表脚本
-- 基于后端实体类和 Mapper XML 生成
-- =============================================

-- 1. 人员表
CREATE TABLE IF NOT EXISTS `tb_emp` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_name` varchar(100) DEFAULT NULL COMMENT '人员名称',
  `region_id` bigint DEFAULT NULL COMMENT '归属区域ID',
  `region_name` varchar(100) DEFAULT NULL COMMENT '归属区域名称',
  `role_id` bigint DEFAULT NULL COMMENT '角色ID',
  `role_code` varchar(50) DEFAULT NULL COMMENT '角色编码',
  `role_name` varchar(50) DEFAULT NULL COMMENT '角色名称',
  `mobile` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `image` varchar(500) DEFAULT NULL COMMENT '头像',
  `status` bigint DEFAULT 0 COMMENT '状态 0:启用 1:停用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员表';

-- 2. 售货机表
CREATE TABLE IF NOT EXISTS `tb_vending_machine` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `inner_code` varchar(50) DEFAULT NULL COMMENT '设备编号',
  `channel_max_capacity` bigint DEFAULT NULL COMMENT '设备最大容量',
  `node_id` bigint DEFAULT NULL COMMENT '点位ID',
  `addr` varchar(500) DEFAULT NULL COMMENT '详细地址',
  `last_supply_time` datetime DEFAULT NULL COMMENT '上次补货时间',
  `business_type` bigint DEFAULT NULL COMMENT '商圈类型',
  `region_id` bigint DEFAULT NULL COMMENT '区域ID',
  `partner_id` bigint DEFAULT NULL COMMENT '合作商ID',
  `vm_type_id` bigint DEFAULT NULL COMMENT '设备型号ID',
  `vm_status` bigint DEFAULT 0 COMMENT '设备状态 0:未投放 1:运营 3:撤机',
  `running_status` varchar(50) DEFAULT NULL COMMENT '运行状态',
  `longitudes` bigint DEFAULT NULL COMMENT '经度',
  `latitude` bigint DEFAULT NULL COMMENT '纬度',
  `client_id` varchar(100) DEFAULT NULL COMMENT '客户端ID',
  `policy_id` bigint DEFAULT NULL COMMENT '策略ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='售货机表';

-- 3. 设备型号表
CREATE TABLE IF NOT EXISTS `tb_vm_type` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) DEFAULT NULL COMMENT '型号名称',
  `model` varchar(100) DEFAULT NULL COMMENT '型号编码',
  `image` varchar(500) DEFAULT NULL COMMENT '设备图片',
  `vm_row` bigint DEFAULT NULL COMMENT '货道行数',
  `vm_col` bigint DEFAULT NULL COMMENT '货道列数',
  `channel_max_capacity` bigint DEFAULT NULL COMMENT '设备容量',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备型号表';

-- 4. 货道表
CREATE TABLE IF NOT EXISTS `tb_channel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `channel_code` varchar(50) DEFAULT NULL COMMENT '货道编号',
  `sku_id` bigint DEFAULT NULL COMMENT '商品ID',
  `vm_id` bigint DEFAULT NULL COMMENT '售货机ID',
  `inner_code` varchar(50) DEFAULT NULL COMMENT '售货机软编号',
  `max_capacity` bigint DEFAULT NULL COMMENT '货道最大容量',
  `current_capacity` bigint DEFAULT NULL COMMENT '货道当前容量',
  `last_supply_time` datetime DEFAULT NULL COMMENT '上次补货时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='货道表';

-- 5. 商品表
CREATE TABLE IF NOT EXISTS `tb_sku` (
  `sku_id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `sku_name` varchar(200) DEFAULT NULL COMMENT '商品名称',
  `sku_image` varchar(500) DEFAULT NULL COMMENT '商品图片',
  `brand_name` varchar(100) DEFAULT NULL COMMENT '品牌',
  `unit` varchar(50) DEFAULT NULL COMMENT '规格(净含量)',
  `price` bigint DEFAULT NULL COMMENT '商品价格(分)',
  `class_id` bigint DEFAULT NULL COMMENT '商品类型ID',
  `is_discount` int DEFAULT 0 COMMENT '是否折扣 0:否 1:是',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 6. 商品类型表
CREATE TABLE IF NOT EXISTS `tb_sku_class` (
  `class_id` bigint NOT NULL AUTO_INCREMENT COMMENT '类型ID',
  `class_name` varchar(100) DEFAULT NULL COMMENT '类型名称',
  `parent_id` bigint DEFAULT 0 COMMENT '父类型ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品类型表';

-- 7. 订单表
CREATE TABLE IF NOT EXISTS `tb_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` varchar(50) DEFAULT NULL COMMENT '订单编号',
  `third_no` varchar(100) DEFAULT NULL COMMENT '第三方平台单号',
  `inner_code` varchar(50) DEFAULT NULL COMMENT '机器编号',
  `channel_code` varchar(50) DEFAULT NULL COMMENT '货道编号',
  `sku_id` bigint DEFAULT NULL COMMENT '商品ID',
  `sku_name` varchar(200) DEFAULT NULL COMMENT '商品名称',
  `class_id` bigint DEFAULT NULL COMMENT '商品类别ID',
  `status` bigint DEFAULT 0 COMMENT '订单状态 0:待支付 1:支付完成 2:出货成功 3:出货失败 4:已取消',
  `amount` bigint DEFAULT NULL COMMENT '支付金额(分)',
  `price` bigint DEFAULT NULL COMMENT '商品金额(分)',
  `pay_type` varchar(20) DEFAULT NULL COMMENT '支付类型 1:支付宝 2:微信',
  `pay_status` bigint DEFAULT 0 COMMENT '支付状态 0:未支付 1:支付完成 2:退款中 3:退款完成',
  `bill` bigint DEFAULT NULL COMMENT '合作商账单金额(分)',
  `addr` varchar(500) DEFAULT NULL COMMENT '点位地址',
  `region_id` bigint DEFAULT NULL COMMENT '区域ID',
  `region_name` varchar(100) DEFAULT NULL COMMENT '区域名称',
  `business_type` bigint DEFAULT NULL COMMENT '商圈类型',
  `partner_id` bigint DEFAULT NULL COMMENT '合作商ID',
  `open_id` varchar(100) DEFAULT NULL COMMENT '跨站身份验证',
  `node_id` bigint DEFAULT NULL COMMENT '点位ID',
  `node_name` varchar(200) DEFAULT NULL COMMENT '点位名称',
  `cancel_desc` varchar(500) DEFAULT NULL COMMENT '取消原因',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 8. 工单表
CREATE TABLE IF NOT EXISTS `tb_task` (
  `task_id` bigint NOT NULL AUTO_INCREMENT COMMENT '工单ID',
  `task_code` varchar(50) DEFAULT NULL COMMENT '工单编号',
  `task_status` bigint DEFAULT 0 COMMENT '工单状态 0:待办 1:进行中 2:已完成 3:已取消',
  `create_type` bigint DEFAULT 1 COMMENT '创建类型 0:自动 1:手动',
  `inner_code` varchar(50) DEFAULT NULL COMMENT '售货机编码',
  `user_id` bigint DEFAULT NULL COMMENT '执行人ID',
  `user_name` varchar(100) DEFAULT NULL COMMENT '执行人名称',
  `region_id` bigint DEFAULT NULL COMMENT '区域ID',
  `desc` varchar(500) DEFAULT NULL COMMENT '备注',
  `product_type_id` bigint DEFAULT NULL COMMENT '工单类型ID',
  `assignor_id` bigint DEFAULT NULL COMMENT '指派人ID',
  `addr` varchar(500) DEFAULT NULL COMMENT '地址',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单表';

-- 9. 工单详情表
CREATE TABLE IF NOT EXISTS `tb_task_details` (
  `details_id` bigint NOT NULL AUTO_INCREMENT COMMENT '工单详情ID',
  `task_id` bigint DEFAULT NULL COMMENT '工单ID',
  `channel_code` varchar(50) DEFAULT NULL COMMENT '货道编号',
  `expect_capacity` bigint DEFAULT NULL COMMENT '补货期望容量',
  `sku_id` bigint DEFAULT NULL COMMENT '商品ID',
  `sku_name` varchar(200) DEFAULT NULL COMMENT '商品名称',
  `sku_image` varchar(500) DEFAULT NULL COMMENT '商品图片',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`details_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单详情表';

-- 10. 工单类型表
CREATE TABLE IF NOT EXISTS `tb_task_type` (
  `type_id` bigint NOT NULL AUTO_INCREMENT COMMENT '类型ID',
  `type_name` varchar(100) DEFAULT NULL COMMENT '类型名称',
  `type` bigint DEFAULT NULL COMMENT '工单类型 1:维修工单 2:运营工单',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单类型表';

-- 11. 合作商表
CREATE TABLE IF NOT EXISTS `tb_partner` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `partner_name` varchar(200) DEFAULT NULL COMMENT '合作商名称',
  `contact_person` varchar(100) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `profit_ratio` bigint DEFAULT NULL COMMENT '分成比例',
  `account` varchar(100) DEFAULT NULL COMMENT '账号',
  `password` varchar(200) DEFAULT NULL COMMENT '密码',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合作商表';

-- 12. 区域表
CREATE TABLE IF NOT EXISTS `tb_region` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `region_name` varchar(100) DEFAULT NULL COMMENT '区域名称',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='区域表';

-- 13. 点位表
CREATE TABLE IF NOT EXISTS `tb_node` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `node_name` varchar(200) DEFAULT NULL COMMENT '点位名称',
  `address` varchar(500) DEFAULT NULL COMMENT '详细地址',
  `business_type` bigint DEFAULT NULL COMMENT '商圈类型',
  `region_id` bigint DEFAULT NULL COMMENT '区域ID',
  `partner_id` bigint DEFAULT NULL COMMENT '合作商ID',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点位表';

-- 14. 策略表
CREATE TABLE IF NOT EXISTS `tb_policy` (
  `policy_id` bigint NOT NULL AUTO_INCREMENT COMMENT '策略ID',
  `policy_name` varchar(200) DEFAULT NULL COMMENT '策略名称',
  `discount` bigint DEFAULT NULL COMMENT '策略方案(如80代表8折)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`policy_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='策略表';

-- 15. 业务角色表
CREATE TABLE IF NOT EXISTS `tb_role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_code` varchar(50) DEFAULT NULL COMMENT '角色编码',
  `role_name` varchar(100) DEFAULT NULL COMMENT '角色名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务角色表';

-- 16. 工单任务表
CREATE TABLE IF NOT EXISTS `tb_job` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `alert_value` bigint DEFAULT NULL COMMENT '警戒值百分比',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单任务表';
