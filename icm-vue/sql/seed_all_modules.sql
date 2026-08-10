-- =============================================
-- DKD 全模块测试数据
-- 按依赖顺序插入，保证数据一致性
-- =============================================

-- 1. 设备类型
INSERT INTO tb_vm_type (name, model, image, vm_row, vm_col, channel_max_capacity, create_time, update_time) VALUES
('标准饮料机', 'VM-STD-60', NULL, 6, 10, 60, NOW(), NOW()),
('小型零食机', 'VM-SNK-30', NULL, 5, 6, 30, NOW(), NOW()),
('综合售卖机', 'VM-MIX-80', NULL, 8, 10, 80, NOW(), NOW()),
('鲜食柜', 'VM-FSH-20', NULL, 4, 5, 20, NOW(), NOW());

-- 2. 商品分类
INSERT INTO tb_sku_class (class_name, parent_id, create_time, update_time) VALUES
('饮料', 0, NOW(), NOW()),
('零食', 0, NOW(), NOW()),
('日用品', 0, NOW(), NOW()),
('鲜食', 0, NOW(), NOW());

-- 3. 商品
INSERT INTO tb_sku (sku_name, sku_image, brand_name, unit, price, class_id, is_discount, create_time, update_time) VALUES
('农夫山泉550ml', NULL, '农夫山泉', '瓶', 200, 1, 0, NOW(), NOW()),
('可口可乐330ml', NULL, '可口可乐', '罐', 300, 1, 0, NOW(), NOW()),
('元气森林白桃味', NULL, '元气森林', '瓶', 500, 1, 1, NOW(), NOW()),
('康师傅冰红茶500ml', NULL, '康师傅', '瓶', 300, 1, 0, NOW(), NOW()),
('乐事薯片原味', NULL, '乐事', '包', 600, 2, 0, NOW(), NOW()),
('奥利奥夹心饼干', NULL, '奥利奥', '包', 800, 2, 1, NOW(), NOW()),
('德芙巧克力43g', NULL, '德芙', '块', 900, 2, 0, NOW(), NOW()),
('维达纸巾3包装', NULL, '维达', '包', 500, 3, 0, NOW(), NOW()),
('三明治-火腿芝士', NULL, '自有品牌', '个', 1200, 4, 0, NOW(), NOW()),
('饭团-金枪鱼', NULL, '自有品牌', '个', 1000, 4, 0, NOW(), NOW());

-- 4. 区域
INSERT INTO tb_region (region_name, create_by, create_time, update_by, update_time, remark) VALUES
('朝阳区', 'admin', NOW(), '', NULL, '北京朝阳区'),
('海淀区', 'admin', NOW(), '', NULL, '北京海淀区'),
('浦东新区', 'admin', NOW(), '', NULL, '上海浦东新区'),
('天河区', 'admin', NOW(), '', NULL, '广州天河区'),
('南山区', 'admin', NOW(), '', NULL, '深圳南山区');

-- 5. 合作商
INSERT INTO tb_partner (partner_name, contact_person, contact_phone, profit_ratio, account, password, create_by, create_time, update_by, update_time, remark) VALUES
('北京优选商贸', '张经理', '13800001111', 30, 'bjyouxuan', 'e10adc3949ba59abbe56e057f20f883e', 'admin', NOW(), '', NULL, '北京区域合作商'),
('上海便利达', '李经理', '13800002222', 25, 'shbianlida', 'e10adc3949ba59abbe56e057f20f883e', 'admin', NOW(), '', NULL, '上海区域合作商'),
('广州乐享科技', '王经理', '13800003333', 28, 'gzlexiang', 'e10adc3949ba59abbe56e057f20f883e', 'admin', NOW(), '', NULL, '广州区域合作商'),
('深圳智汇通', '赵经理', '13800004444', 32, 'szzhihuit', 'e10adc3949ba59abbe56e057f20f883e', 'admin', NOW(), '', NULL, '深圳区域合作商');

-- 6. 点位
INSERT INTO tb_node (node_name, address, business_type, region_id, partner_id, create_by, create_time, update_by, update_time, remark) VALUES
('国贸CBD写字楼', '北京市朝阳区建国门外大街1号', 1, 1, 1, 'admin', NOW(), '', NULL, '写字楼大厅'),
('望京SOHO', '北京市朝阳区望京街10号', 1, 1, 1, 'admin', NOW(), '', NULL, '大堂入口'),
('中关村软件园', '北京市海淀区东北旺西路8号', 2, 2, 1, 'admin', NOW(), '', NULL, '园区食堂旁'),
('陆家嘴金融中心', '上海市浦东新区银城中路200号', 1, 3, 2, 'admin', NOW(), '', NULL, '一楼大厅'),
('天河城广场', '广州市天河区天河路208号', 3, 4, 3, 'admin', NOW(), '', NULL, 'B1层入口'),
('科技园北区', '深圳市南山区高新南一道', 2, 5, 4, 'admin', NOW(), '', NULL, 'A栋一楼'),
('三里屯太古里', '北京市朝阳区三里屯路19号', 3, 1, 1, 'admin', NOW(), '', NULL, '南区B1层'),
('张江高科', '上海市浦东新区张江路650号', 2, 3, 2, 'admin', NOW(), '', NULL, '孵化器大厅');

-- 7. 自动补货策略
INSERT INTO tb_policy (policy_name, discount, create_time, update_time) VALUES
('标准补货策略', 0, NOW(), NOW()),
('促销期补货策略', 10, NOW(), NOW()),
('节假日加强策略', 0, NOW(), NOW());

-- 8. 设备
INSERT INTO tb_vending_machine (inner_code, channel_max_capacity, node_id, addr, last_supply_time, business_type, region_id, partner_id, vm_type_id, vm_status, running_status, longitudes, latitude, client_id, policy_id, create_time, update_time) VALUES
('VM001', 60, 1, '国贸CBD写字楼1楼大厅', '2026-03-08 09:00:00', 1, 1, 1, 1, 1, '正常', 116460000, 39910000, 'client-001', 1, NOW(), NOW()),
('VM002', 60, 2, '望京SOHO大堂入口', '2026-03-07 14:00:00', 1, 1, 1, 1, 1, '正常', 116480000, 39990000, 'client-002', 1, NOW(), NOW()),
('VM003', 30, 3, '中关村软件园食堂旁', '2026-03-06 10:30:00', 2, 2, 1, 2, 1, '正常', 116310000, 40030000, 'client-003', 1, NOW(), NOW()),
('VM004', 80, 4, '陆家嘴金融中心1楼', '2026-03-08 08:00:00', 1, 3, 2, 3, 1, '正常', 121500000, 31230000, 'client-004', 2, NOW(), NOW()),
('VM005', 60, 5, '天河城广场B1层', '2026-03-05 16:00:00', 3, 4, 3, 1, 0, '离线', 113320000, 23130000, 'client-005', 1, NOW(), NOW()),
('VM006', 30, 6, '科技园北区A栋', '2026-03-07 11:00:00', 2, 5, 4, 2, 1, '正常', 113940000, 22530000, 'client-006', 1, NOW(), NOW()),
('VM007', 60, 7, '三里屯太古里南区B1', '2026-03-08 10:00:00', 3, 1, 1, 1, 1, '正常', 116450000, 39930000, 'client-007', 3, NOW(), NOW()),
('VM008', 80, 8, '张江高科孵化器', '2026-03-04 09:00:00', 2, 3, 2, 3, 2, '故障', 121590000, 31210000, 'client-008', 1, NOW(), NOW());

-- 9. 货道 (每个设备6条货道)
INSERT INTO tb_channel (channel_code, sku_id, vm_id, inner_code, max_capacity, current_capacity, last_supply_time, create_time, update_time) VALUES
('A1', 1, 1, 'VM001', 15, 12, '2026-03-08 09:00:00', NOW(), NOW()),
('A2', 2, 1, 'VM001', 15, 8, '2026-03-08 09:00:00', NOW(), NOW()),
('A3', 3, 1, 'VM001', 15, 15, '2026-03-08 09:00:00', NOW(), NOW()),
('B1', 5, 1, 'VM001', 10, 6, '2026-03-08 09:00:00', NOW(), NOW()),
('B2', 6, 1, 'VM001', 10, 3, '2026-03-08 09:00:00', NOW(), NOW()),
('B3', 7, 1, 'VM001', 10, 9, '2026-03-08 09:00:00', NOW(), NOW()),
('A1', 1, 2, 'VM002', 15, 10, '2026-03-07 14:00:00', NOW(), NOW()),
('A2', 2, 2, 'VM002', 15, 5, '2026-03-07 14:00:00', NOW(), NOW()),
('A3', 4, 2, 'VM002', 15, 14, '2026-03-07 14:00:00', NOW(), NOW()),
('B1', 5, 2, 'VM002', 10, 7, '2026-03-07 14:00:00', NOW(), NOW()),
('A1', 1, 3, 'VM003', 10, 4, '2026-03-06 10:30:00', NOW(), NOW()),
('A2', 3, 3, 'VM003', 10, 8, '2026-03-06 10:30:00', NOW(), NOW()),
('B1', 5, 3, 'VM003', 10, 2, '2026-03-06 10:30:00', NOW(), NOW()),
('A1', 1, 4, 'VM004', 15, 13, '2026-03-08 08:00:00', NOW(), NOW()),
('A2', 2, 4, 'VM004', 15, 11, '2026-03-08 08:00:00', NOW(), NOW()),
('A3', 3, 4, 'VM004', 15, 9, '2026-03-08 08:00:00', NOW(), NOW()),
('A4', 4, 4, 'VM004', 15, 15, '2026-03-08 08:00:00', NOW(), NOW()),
('B1', 5, 4, 'VM004', 10, 5, '2026-03-08 08:00:00', NOW(), NOW()),
('B2', 6, 4, 'VM004', 10, 8, '2026-03-08 08:00:00', NOW(), NOW()),
('C1', 9, 4, 'VM004', 10, 3, '2026-03-08 08:00:00', NOW(), NOW()),
('A1', 2, 5, 'VM005', 15, 2, '2026-03-05 16:00:00', NOW(), NOW()),
('A2', 4, 5, 'VM005', 15, 1, '2026-03-05 16:00:00', NOW(), NOW()),
('B1', 5, 5, 'VM005', 10, 0, '2026-03-05 16:00:00', NOW(), NOW()),
('A1', 1, 6, 'VM006', 10, 7, '2026-03-07 11:00:00', NOW(), NOW()),
('A2', 3, 6, 'VM006', 10, 5, '2026-03-07 11:00:00', NOW(), NOW()),
('B1', 8, 6, 'VM006', 10, 9, '2026-03-07 11:00:00', NOW(), NOW()),
('A1', 1, 7, 'VM007', 15, 11, '2026-03-08 10:00:00', NOW(), NOW()),
('A2', 2, 7, 'VM007', 15, 14, '2026-03-08 10:00:00', NOW(), NOW()),
('B1', 5, 7, 'VM007', 10, 4, '2026-03-08 10:00:00', NOW(), NOW()),
('B2', 7, 7, 'VM007', 10, 6, '2026-03-08 10:00:00', NOW(), NOW()),
('A1', 1, 8, 'VM008', 15, 0, '2026-03-04 09:00:00', NOW(), NOW()),
('A2', 2, 8, 'VM008', 15, 2, '2026-03-04 09:00:00', NOW(), NOW());

-- 10. 工单角色
INSERT INTO tb_role (role_code, role_name, create_time, update_time) VALUES
('operator', '运营员', NOW(), NOW()),
('repairman', '维修员', NOW(), NOW()),
('supplyman', '补货员', NOW(), NOW()),
('inspector', '巡检员', NOW(), NOW());

-- 11. 人员
INSERT INTO tb_emp (user_name, region_id, region_name, role_id, role_code, role_name, mobile, image, status, create_time, update_time) VALUES
('陈小明', 1, '朝阳区', 1, 'operator', '运营员', '13900001111', NULL, 0, NOW(), NOW()),
('刘大伟', 1, '朝阳区', 3, 'supplyman', '补货员', '13900002222', NULL, 0, NOW(), NOW()),
('王志强', 2, '海淀区', 2, 'repairman', '维修员', '13900003333', NULL, 0, NOW(), NOW()),
('李芳芳', 3, '浦东新区', 1, 'operator', '运营员', '13900004444', NULL, 0, NOW(), NOW()),
('赵六', 3, '浦东新区', 3, 'supplyman', '补货员', '13900005555', NULL, 0, NOW(), NOW()),
('孙七', 4, '天河区', 2, 'repairman', '维修员', '13900006666', NULL, 0, NOW(), NOW()),
('周八', 5, '南山区', 4, 'inspector', '巡检员', '13900007777', NULL, 1, NOW(), NOW()),
('吴九', 1, '朝阳区', 2, 'repairman', '维修员', '13900008888', NULL, 0, NOW(), NOW());

-- 12. 工单类型
INSERT INTO tb_task_type (type_name, type, create_time, update_time) VALUES
('设备补货', 1, NOW(), NOW()),
('设备维修', 2, NOW(), NOW()),
('设备巡检', 3, NOW(), NOW()),
('设备撤机', 4, NOW(), NOW());

-- 13. 工单
INSERT INTO tb_task (task_code, task_status, create_type, inner_code, user_id, user_name, region_id, `desc`, product_type_id, assignor_id, addr, create_time, update_time) VALUES
('WO202603080001', 1, 1, 'VM001', 2, '刘大伟', 1, '国贸CBD饮料机可乐库存不足', 1, 1, '国贸CBD写字楼1楼大厅', '2026-03-08 09:30:00', NOW()),
('WO202603080002', 0, 1, 'VM005', 6, '孙七', 4, '天河城设备离线，需现场检修', 2, 1, '天河城广场B1层', '2026-03-08 10:00:00', NULL),
('WO202603070003', 2, 1, 'VM003', 2, '刘大伟', 2, '中关村软件园零食机补货', 1, 1, '中关村软件园食堂旁', '2026-03-07 08:00:00', NOW()),
('WO202603070004', 1, 2, 'VM008', 3, '王志强', 3, '张江高科设备故障报修', 2, 4, '张江高科孵化器', '2026-03-07 14:00:00', NOW()),
('WO202603060005', 2, 1, 'VM002', 5, '赵六', 1, '望京SOHO例行巡检', 3, 4, '望京SOHO大堂入口', '2026-03-06 09:00:00', NOW()),
('WO202603060006', 0, 1, 'VM007', 2, '刘大伟', 1, '三里屯周末前补货', 1, 1, '三里屯太古里南区B1', '2026-03-06 15:00:00', NULL);

-- 14. 工单详情
INSERT INTO tb_task_details (task_id, channel_code, expect_capacity, sku_id, sku_name, sku_image, create_time, update_time) VALUES
(1, 'A2', 15, 2, '可口可乐330ml', NULL, NOW(), NULL),
(1, 'B2', 10, 6, '奥利奥夹心饼干', NULL, NOW(), NULL),
(3, 'B1', 10, 5, '乐事薯片原味', NULL, NOW(), NULL),
(3, 'A1', 10, 1, '农夫山泉550ml', NULL, NOW(), NULL),
(6, 'A1', 15, 1, '农夫山泉550ml', NULL, NOW(), NULL),
(6, 'B1', 10, 5, '乐事薯片原味', NULL, NOW(), NULL),
(6, 'B2', 10, 7, '德芙巧克力43g', NULL, NOW(), NULL);

-- 15. 订单
INSERT INTO tb_order (order_no, third_no, inner_code, channel_code, sku_id, sku_name, class_id, status, amount, price, pay_type, pay_status, bill, addr, region_id, region_name, business_type, partner_id, open_id, node_id, node_name, cancel_desc, create_time, update_time) VALUES
('ORD20260308001', 'WX202603080001', 'VM001', 'A1', 1, '农夫山泉550ml', 1, 1, 1, 200, 'wechat', 1, 200, '国贸CBD写字楼1楼大厅', 1, '朝阳区', 1, 1, 'oXyz1234', 1, '国贸CBD写字楼', NULL, '2026-03-08 10:15:00', NOW()),
('ORD20260308002', 'ZFB20260308001', 'VM001', 'B1', 5, '乐事薯片原味', 2, 1, 1, 600, 'alipay', 1, 600, '国贸CBD写字楼1楼大厅', 1, '朝阳区', 1, 1, '20881234', 1, '国贸CBD写字楼', NULL, '2026-03-08 10:30:00', NOW()),
('ORD20260308003', 'WX202603080002', 'VM004', 'A3', 3, '元气森林白桃味', 1, 1, 2, 1000, 'wechat', 1, 1000, '陆家嘴金融中心1楼', 3, '浦东新区', 1, 2, 'oXyz5678', 4, '陆家嘴金融中心', NULL, '2026-03-08 11:00:00', NOW()),
('ORD20260308004', 'WX202603080003', 'VM007', 'A1', 1, '农夫山泉550ml', 1, 0, 1, 200, 'wechat', 0, 200, '三里屯太古里南区B1', 1, '朝阳区', 3, 1, 'oXyz9012', 7, '三里屯太古里', NULL, '2026-03-08 12:00:00', NULL),
('ORD20260307005', 'WX202603070001', 'VM002', 'A1', 1, '农夫山泉550ml', 1, 1, 1, 200, 'wechat', 1, 200, '望京SOHO大堂入口', 1, '朝阳区', 1, 1, 'oXyz3456', 2, '望京SOHO', NULL, '2026-03-07 09:20:00', NOW()),
('ORD20260307006', 'ZFB20260307001', 'VM004', 'C1', 9, '三明治-火腿芝士', 4, 2, 1, 1200, 'alipay', 2, 0, '陆家嘴金融中心1楼', 3, '浦东新区', 1, 2, '20885678', 4, '陆家嘴金融中心', '用户主动取消', '2026-03-07 12:30:00', NOW()),
('ORD20260306007', 'WX202603060001', 'VM003', 'A2', 3, '元气森林白桃味', 1, 1, 1, 500, 'wechat', 1, 500, '中关村软件园食堂旁', 2, '海淀区', 2, 1, 'oXyz7890', 3, '中关村软件园', NULL, '2026-03-06 14:45:00', NOW()),
('ORD20260306008', 'WX202603060002', 'VM006', 'B1', 8, '维达纸巾3包装', 3, 1, 2, 1000, 'wechat', 1, 1000, '科技园北区A栋', 5, '南山区', 2, 4, 'oXyz1122', 6, '科技园北区', NULL, '2026-03-06 16:10:00', NOW()),
('ORD20260305009', 'WX202603050001', 'VM005', 'A1', 2, '可口可乐330ml', 1, 1, 1, 300, 'wechat', 1, 300, '天河城广场B1层', 4, '天河区', 3, 3, 'oXyz3344', 5, '天河城广场', NULL, '2026-03-05 17:00:00', NOW()),
('ORD20260305010', 'ZFB20260305001', 'VM001', 'A3', 3, '元气森林白桃味', 1, 3, 1, 500, 'alipay', 2, 0, '国贸CBD写字楼1楼大厅', 1, '朝阳区', 1, 1, '20889900', 1, '国贸CBD写字楼', '出货失败自动退款', '2026-03-05 08:30:00', NOW());

-- 16. 自动补货任务
INSERT INTO tb_job (alert_value, create_time, update_time) VALUES
(5, NOW(), NOW()),
(3, NOW(), NOW()),
(10, NOW(), NOW());

-- 17. 数据等级标注
INSERT INTO tb_data_level_label (channel_id, event_code, data_level, create_by, create_time, update_by, update_time) VALUES
('VM001-A1', 'PAY_SUCCESS', 'HIGH', 'admin', NOW(), '', NULL),
('VM001-A2', 'PAY_SUCCESS', 'HIGH', 'admin', NOW(), '', NULL),
('VM004-C1', 'DELIVER_FAIL', 'CRITICAL', 'admin', NOW(), '', NULL),
('VM005-B1', 'STOCK_EMPTY', 'CRITICAL', 'admin', NOW(), '', NULL),
('VM008-A1', 'OFFLINE', 'HIGH', 'admin', NOW(), '', NULL),
('VM003-B1', 'STOCK_LOW', 'MEDIUM', 'admin', NOW(), '', NULL);

-- 18. 元数据字段维护
INSERT INTO tb_coding_scheme_field_maintenance (field_name, field_identification, maintenance_time, apply_scope, is_scope_h5, is_scope_native, is_scope_mini, is_required, max_length, is_content_digit, is_content_letter, is_content_chinese, is_content_underscore, business_description, technical_description, create_time, update_time, create_by, update_by) VALUES
('设备编号', 'inner_code', NOW(), '设备管理', 1, 1, 1, 1, 50, 1, 1, 0, 1, '售货机唯一编号，格式VM+三位数字', 'varchar(50)，全局唯一索引', NOW(), NULL, 'admin', ''),
('商品名称', 'sku_name', NOW(), '商品管理', 1, 1, 1, 1, 200, 0, 0, 1, 0, '商品的显示名称', 'varchar(200)，支持模糊搜索', NOW(), NULL, 'admin', ''),
('订单号', 'order_no', NOW(), '订单管理', 1, 1, 0, 1, 50, 1, 1, 0, 0, '系统生成的唯一订单编号，格式ORD+日期+序号', 'varchar(50)，自动递增', NOW(), NULL, 'admin', ''),
('支付类型', 'pay_type', NOW(), '订单管理', 1, 1, 1, 1, 20, 0, 1, 0, 0, '支付渠道标识：wechat/alipay', '枚举值 varchar(20)', NOW(), NULL, 'admin', ''),
('区域名称', 'region_name', NOW(), '点位管理', 1, 1, 1, 1, 100, 0, 0, 1, 0, '行政区划名称', 'varchar(100)', NOW(), NULL, 'admin', ''),
('工单状态', 'task_status', NOW(), '工单管理', 1, 1, 1, 0, NULL, 1, 0, 0, 0, '0-待处理 1-处理中 2-已完成', 'bigint，枚举0/1/2', NOW(), NULL, 'admin', '');
