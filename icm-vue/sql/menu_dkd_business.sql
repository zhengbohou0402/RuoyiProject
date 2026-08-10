-- =============================================
-- DKD 帝可得管理系统 - 业务菜单配置脚本
-- 注意：需要根据实际数据库中已有的最大 menu_id 调整起始ID
-- =============================================

-- =============================================
-- 一、设备管理（一级菜单）
-- =============================================
-- 一级菜单：设备管理
INSERT INTO sys_menu VALUES(2000, '设备管理', 0, 1, 'vm', NULL, '', 1, 0, 'M', '0', '0', '', 'monitor', 'admin', sysdate(), '', NULL, '设备管理目录');

-- 设备管理 - 设备管理
INSERT INTO sys_menu VALUES(2001, '设备管理', 2000, 1, 'index', 'manage/vm/index', '', 1, 0, 'C', '0', '0', 'manage:vm:list', 'server', 'admin', sysdate(), '', NULL, '设备管理菜单');
INSERT INTO sys_menu VALUES(20011, '设备查询', 2001, 1, '', '', '', 1, 0, 'F', '0', '0', 'manage:vm:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(20012, '设备新增', 2001, 2, '', '', '', 1, 0, 'F', '0', '0', 'manage:vm:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(20013, '设备修改', 2001, 3, '', '', '', 1, 0, 'F', '0', '0', 'manage:vm:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(20014, '设备删除', 2001, 4, '', '', '', 1, 0, 'F', '0', '0', 'manage:vm:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(20015, '设备导出', 2001, 5, '', '', '', 1, 0, 'F', '0', '0', 'manage:vm:export', '#', 'admin', sysdate(), '', NULL, '');

-- 设备管理 - 设备类型
INSERT INTO sys_menu VALUES(2002, '设备类型', 2000, 2, 'vmType', 'manage/vmType/index', '', 1, 0, 'C', '0', '0', 'manage:vmType:list', 'type', 'admin', sysdate(), '', NULL, '设备类型菜单');
INSERT INTO sys_menu VALUES(20021, '设备类型查询', 2002, 1, '', '', '', 1, 0, 'F', '0', '0', 'manage:vmType:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(20022, '设备类型新增', 2002, 2, '', '', '', 1, 0, 'F', '0', '0', 'manage:vmType:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(20023, '设备类型修改', 2002, 3, '', '', '', 1, 0, 'F', '0', '0', 'manage:vmType:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(20024, '设备类型删除', 2002, 4, '', '', '', 1, 0, 'F', '0', '0', 'manage:vmType:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(20025, '设备类型导出', 2002, 5, '', '', '', 1, 0, 'F', '0', '0', 'manage:vmType:export', '#', 'admin', sysdate(), '', NULL, '');

-- 设备管理 - 货道管理
INSERT INTO sys_menu VALUES(2003, '货道管理', 2000, 3, 'channel', 'manage/channel/index', '', 1, 0, 'C', '0', '0', 'manage:channel:list', 'channel', 'admin', sysdate(), '', NULL, '货道管理菜单');
INSERT INTO sys_menu VALUES(20031, '货道查询', 2003, 1, '', '', '', 1, 0, 'F', '0', '0', 'manage:channel:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(20032, '货道新增', 2003, 2, '', '', '', 1, 0, 'F', '0', '0', 'manage:channel:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(20033, '货道修改', 2003, 3, '', '', '', 1, 0, 'F', '0', '0', 'manage:channel:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(20034, '货道删除', 2003, 4, '', '', '', 1, 0, 'F', '0', '0', 'manage:channel:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(20035, '货道导出', 2003, 5, '', '', '', 1, 0, 'F', '0', '0', 'manage:channel:export', '#', 'admin', sysdate(), '', NULL, '');

-- =============================================
-- 二、商品管理（一级菜单）
-- =============================================
INSERT INTO sys_menu VALUES(2100, '商品管理', 0, 2, 'sku', NULL, '', 1, 0, 'M', '0', '0', '', 'shopping', 'admin', sysdate(), '', NULL, '商品管理目录');

-- 商品管理 - 商品列表
INSERT INTO sys_menu VALUES(2101, '商品列表', 2100, 1, 'index', 'manage/sku/index', '', 1, 0, 'C', '0', '0', 'manage:sku:list', 'list', 'admin', sysdate(), '', NULL, '商品列表菜单');
INSERT INTO sys_menu VALUES(21011, '商品查询', 2101, 1, '', '', '', 1, 0, 'F', '0', '0', 'manage:sku:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(21012, '商品新增', 2101, 2, '', '', '', 1, 0, 'F', '0', '0', 'manage:sku:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(21013, '商品修改', 2101, 3, '', '', '', 1, 0, 'F', '0', '0', 'manage:sku:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(21014, '商品删除', 2101, 4, '', '', '', 1, 0, 'F', '0', '0', 'manage:sku:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(21015, '商品导出', 2101, 5, '', '', '', 1, 0, 'F', '0', '0', 'manage:sku:export', '#', 'admin', sysdate(), '', NULL, '');

-- 商品管理 - 商品类型
INSERT INTO sys_menu VALUES(2102, '商品类型', 2100, 2, 'skuClass', 'manage/skuClass/index', '', 1, 0, 'C', '0', '0', 'manage:skuClass:list', 'type', 'admin', sysdate(), '', NULL, '商品类型菜单');
INSERT INTO sys_menu VALUES(21021, '商品类型查询', 2102, 1, '', '', '', 1, 0, 'F', '0', '0', 'manage:skuClass:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(21022, '商品类型新增', 2102, 2, '', '', '', 1, 0, 'F', '0', '0', 'manage:skuClass:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(21023, '商品类型修改', 2102, 3, '', '', '', 1, 0, 'F', '0', '0', 'manage:skuClass:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(21024, '商品类型删除', 2102, 4, '', '', '', 1, 0, 'F', '0', '0', 'manage:skuClass:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(21025, '商品类型导出', 2102, 5, '', '', '', 1, 0, 'F', '0', '0', 'manage:skuClass:export', '#', 'admin', sysdate(), '', NULL, '');

-- =============================================
-- 三、订单管理（一级菜单）
-- =============================================
INSERT INTO sys_menu VALUES(2200, '订单管理', 0, 3, 'order', NULL, '', 1, 0, 'M', '0', '0', '', 'form', 'admin', sysdate(), '', NULL, '订单管理目录');

-- 订单管理 - 订单列表
INSERT INTO sys_menu VALUES(2201, '订单列表', 2200, 1, 'index', 'manage/order/index', '', 1, 0, 'C', '0', '0', 'manage:order:list', 'list', 'admin', sysdate(), '', NULL, '订单列表菜单');
INSERT INTO sys_menu VALUES(22011, '订单查询', 2201, 1, '', '', '', 1, 0, 'F', '0', '0', 'manage:order:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(22012, '订单新增', 2201, 2, '', '', '', 1, 0, 'F', '0', '0', 'manage:order:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(22013, '订单修改', 2201, 3, '', '', '', 1, 0, 'F', '0', '0', 'manage:order:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(22014, '订单删除', 2201, 4, '', '', '', 1, 0, 'F', '0', '0', 'manage:order:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(22015, '订单导出', 2201, 5, '', '', '', 1, 0, 'F', '0', '0', 'manage:order:export', '#', 'admin', sysdate(), '', NULL, '');

-- =============================================
-- 四、工单管理（一级菜单）
-- =============================================
INSERT INTO sys_menu VALUES(2300, '工单管理', 0, 4, 'task', NULL, '', 1, 0, 'M', '0', '0', '', 'job', 'admin', sysdate(), '', NULL, '工单管理目录');

-- 工单管理 - 工单列表
INSERT INTO sys_menu VALUES(2301, '工单列表', 2300, 1, 'index', 'manage/task/index', '', 1, 0, 'C', '0', '0', 'manage:task:list', 'list', 'admin', sysdate(), '', NULL, '工单列表菜单');
INSERT INTO sys_menu VALUES(23011, '工单查询', 2301, 1, '', '', '', 1, 0, 'F', '0', '0', 'manage:task:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(23012, '工单新增', 2301, 2, '', '', '', 1, 0, 'F', '0', '0', 'manage:task:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(23013, '工单修改', 2301, 3, '', '', '', 1, 0, 'F', '0', '0', 'manage:task:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(23014, '工单删除', 2301, 4, '', '', '', 1, 0, 'F', '0', '0', 'manage:task:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(23015, '工单导出', 2301, 5, '', '', '', 1, 0, 'F', '0', '0', 'manage:task:export', '#', 'admin', sysdate(), '', NULL, '');

-- 工单管理 - 工单类型
INSERT INTO sys_menu VALUES(2302, '工单类型', 2300, 2, 'taskType', 'manage/taskType/index', '', 1, 0, 'C', '0', '0', 'manage:taskType:list', 'type', 'admin', sysdate(), '', NULL, '工单类型菜单');
INSERT INTO sys_menu VALUES(23021, '工单类型查询', 2302, 1, '', '', '', 1, 0, 'F', '0', '0', 'manage:taskType:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(23022, '工单类型新增', 2302, 2, '', '', '', 1, 0, 'F', '0', '0', 'manage:taskType:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(23023, '工单类型修改', 2302, 3, '', '', '', 1, 0, 'F', '0', '0', 'manage:taskType:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(23024, '工单类型删除', 2302, 4, '', '', '', 1, 0, 'F', '0', '0', 'manage:taskType:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(23025, '工单类型导出', 2302, 5, '', '', '', 1, 0, 'F', '0', '0', 'manage:taskType:export', '#', 'admin', sysdate(), '', NULL, '');

-- =============================================
-- 五、合作商管理（一级菜单）
-- =============================================
INSERT INTO sys_menu VALUES(2400, '合作商管理', 0, 5, 'partner', NULL, '', 1, 0, 'M', '0', '0', '', 'people', 'admin', sysdate(), '', NULL, '合作商管理目录');

-- 合作商管理 - 合作商列表
INSERT INTO sys_menu VALUES(2401, '合作商列表', 2400, 1, 'index', 'manage/partner/index', '', 1, 0, 'C', '0', '0', 'manage:partner:list', 'list', 'admin', sysdate(), '', NULL, '合作商列表菜单');
INSERT INTO sys_menu VALUES(24011, '合作商查询', 2401, 1, '', '', '', 1, 0, 'F', '0', '0', 'manage:partner:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(24012, '合作商新增', 2401, 2, '', '', '', 1, 0, 'F', '0', '0', 'manage:partner:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(24013, '合作商修改', 2401, 3, '', '', '', 1, 0, 'F', '0', '0', 'manage:partner:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(24014, '合作商删除', 2401, 4, '', '', '', 1, 0, 'F', '0', '0', 'manage:partner:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(24015, '合作商导出', 2401, 5, '', '', '', 1, 0, 'F', '0', '0', 'manage:partner:export', '#', 'admin', sysdate(), '', NULL, '');

-- =============================================
-- 六、点位管理（一级菜单）
-- =============================================
INSERT INTO sys_menu VALUES(2500, '点位管理', 0, 6, 'node', NULL, '', 1, 0, 'M', '0', '0', '', 'tree', 'admin', sysdate(), '', NULL, '点位管理目录');

-- 点位管理 - 区域管理
INSERT INTO sys_menu VALUES(2501, '区域管理', 2500, 1, 'region', 'manage/region/index', '', 1, 0, 'C', '0', '0', 'manage:region:list', 'international', 'admin', sysdate(), '', NULL, '区域管理菜单');
INSERT INTO sys_menu VALUES(25011, '区域查询', 2501, 1, '', '', '', 1, 0, 'F', '0', '0', 'manage:region:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(25012, '区域新增', 2501, 2, '', '', '', 1, 0, 'F', '0', '0', 'manage:region:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(25013, '区域修改', 2501, 3, '', '', '', 1, 0, 'F', '0', '0', 'manage:region:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(25014, '区域删除', 2501, 4, '', '', '', 1, 0, 'F', '0', '0', 'manage:region:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(25015, '区域导出', 2501, 5, '', '', '', 1, 0, 'F', '0', '0', 'manage:region:export', '#', 'admin', sysdate(), '', NULL, '');

-- 点位管理 - 点位列表
INSERT INTO sys_menu VALUES(2502, '点位列表', 2500, 2, 'index', 'manage/node/index', '', 1, 0, 'C', '0', '0', 'manage:node:list', 'list', 'admin', sysdate(), '', NULL, '点位列表菜单');
INSERT INTO sys_menu VALUES(25021, '点位查询', 2502, 1, '', '', '', 1, 0, 'F', '0', '0', 'manage:node:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(25022, '点位新增', 2502, 2, '', '', '', 1, 0, 'F', '0', '0', 'manage:node:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(25023, '点位修改', 2502, 3, '', '', '', 1, 0, 'F', '0', '0', 'manage:node:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(25024, '点位删除', 2502, 4, '', '', '', 1, 0, 'F', '0', '0', 'manage:node:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(25025, '点位导出', 2502, 5, '', '', '', 1, 0, 'F', '0', '0', 'manage:node:export', '#', 'admin', sysdate(), '', NULL, '');

-- =============================================
-- 七、策略管理（一级菜单）
-- =============================================
INSERT INTO sys_menu VALUES(2600, '策略管理', 0, 7, 'policy', NULL, '', 1, 0, 'M', '0', '0', '', 'star', 'admin', sysdate(), '', NULL, '策略管理目录');

-- 策略管理 - 策略列表
INSERT INTO sys_menu VALUES(2601, '策略列表', 2600, 1, 'index', 'manage/policy/index', '', 1, 0, 'C', '0', '0', 'manage:policy:list', 'list', 'admin', sysdate(), '', NULL, '策略列表菜单');
INSERT INTO sys_menu VALUES(26011, '策略查询', 2601, 1, '', '', '', 1, 0, 'F', '0', '0', 'manage:policy:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(26012, '策略新增', 2601, 2, '', '', '', 1, 0, 'F', '0', '0', 'manage:policy:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(26013, '策略修改', 2601, 3, '', '', '', 1, 0, 'F', '0', '0', 'manage:policy:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(26014, '策略删除', 2601, 4, '', '', '', 1, 0, 'F', '0', '0', 'manage:policy:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(26015, '策略导出', 2601, 5, '', '', '', 1, 0, 'F', '0', '0', 'manage:policy:export', '#', 'admin', sysdate(), '', NULL, '');

-- =============================================
-- 八、人员管理（一级菜单）
-- =============================================
INSERT INTO sys_menu VALUES(2700, '人员管理', 0, 8, 'emp', NULL, '', 1, 0, 'M', '0', '0', '', 'user', 'admin', sysdate(), '', NULL, '人员管理目录');

-- 人员管理 - 人员列表
INSERT INTO sys_menu VALUES(2701, '人员列表', 2700, 1, 'index', 'manage/emp/index', '', 1, 0, 'C', '0', '0', 'manage:emp:list', 'peoples', 'admin', sysdate(), '', NULL, '人员列表菜单');
INSERT INTO sys_menu VALUES(27011, '人员查询', 2701, 1, '', '', '', 1, 0, 'F', '0', '0', 'manage:emp:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(27012, '人员新增', 2701, 2, '', '', '', 1, 0, 'F', '0', '0', 'manage:emp:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(27013, '人员修改', 2701, 3, '', '', '', 1, 0, 'F', '0', '0', 'manage:emp:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(27014, '人员删除', 2701, 4, '', '', '', 1, 0, 'F', '0', '0', 'manage:emp:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(27015, '人员导出', 2701, 5, '', '', '', 1, 0, 'F', '0', '0', 'manage:emp:export', '#', 'admin', sysdate(), '', NULL, '');
