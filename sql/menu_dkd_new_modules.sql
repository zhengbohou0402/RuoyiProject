-- =============================================
-- DKD 帝可得管理系统 - 新增业务菜单配置脚本
-- 包含 5 个新模块：工单详情、工单角色、自动补货、元数据管理、数据等级标注
-- 注意：执行前请确认 menu_id 不与已有菜单冲突
-- 导入命令：mysql --default-character-set=utf8 -u root -p dkd < menu_dkd_new_modules.sql
-- =============================================

-- =============================================
-- 一、工单详情（挂在现有工单管理目录下，parent_id = 2300）
-- =============================================
INSERT INTO sys_menu VALUES(2303, '工单详情', 2300, 3, 'taskDetails', 'manage/taskDetails/index', '', 1, 0, 'C', '0', '0', 'manage:taskDetails:list', 'people', 'admin', sysdate(), '', NULL, '工单详情菜单');
INSERT INTO sys_menu VALUES(23031, '工单详情查询', 2303, 1, '', '', '', 1, 0, 'F', '0', '0', 'manage:taskDetails:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(23032, '工单详情新增', 2303, 2, '', '', '', 1, 0, 'F', '0', '0', 'manage:taskDetails:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(23033, '工单详情修改', 2303, 3, '', '', '', 1, 0, 'F', '0', '0', 'manage:taskDetails:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(23034, '工单详情删除', 2303, 4, '', '', '', 1, 0, 'F', '0', '0', 'manage:taskDetails:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(23035, '工单详情导出', 2303, 5, '', '', '', 1, 0, 'F', '0', '0', 'manage:taskDetails:export', '#', 'admin', sysdate(), '', NULL, '');

-- =============================================
-- 二、工单角色（挂在现有工单管理目录下，parent_id = 2300）
-- =============================================
INSERT INTO sys_menu VALUES(2304, '工单角色', 2300, 4, 'manageRole', 'manage/manageRole/index', '', 1, 0, 'C', '0', '0', 'manage:role:list', 'peoples', 'admin', sysdate(), '', NULL, '工单角色菜单');
INSERT INTO sys_menu VALUES(23041, '工单角色查询', 2304, 1, '', '', '', 1, 0, 'F', '0', '0', 'manage:role:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(23042, '工单角色新增', 2304, 2, '', '', '', 1, 0, 'F', '0', '0', 'manage:role:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(23043, '工单角色修改', 2304, 3, '', '', '', 1, 0, 'F', '0', '0', 'manage:role:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(23044, '工单角色删除', 2304, 4, '', '', '', 1, 0, 'F', '0', '0', 'manage:role:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(23045, '工单角色导出', 2304, 5, '', '', '', 1, 0, 'F', '0', '0', 'manage:role:export', '#', 'admin', sysdate(), '', NULL, '');

-- =============================================
-- 三、自动补货（新建一级菜单，排序 9）
-- =============================================
INSERT INTO sys_menu VALUES(2800, '自动补货', 0, 9, 'job', NULL, '', 1, 0, 'M', '0', '0', '', 'job', 'admin', sysdate(), '', NULL, '自动补货目录');

-- 自动补货 - 补货任务
INSERT INTO sys_menu VALUES(2801, '补货任务', 2800, 1, 'index', 'manage/job/index', '', 1, 0, 'C', '0', '0', 'manage:job:list', 'list', 'admin', sysdate(), '', NULL, '补货任务菜单');
INSERT INTO sys_menu VALUES(28011, '补货任务查询', 2801, 1, '', '', '', 1, 0, 'F', '0', '0', 'manage:job:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(28012, '补货任务新增', 2801, 2, '', '', '', 1, 0, 'F', '0', '0', 'manage:job:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(28013, '补货任务修改', 2801, 3, '', '', '', 1, 0, 'F', '0', '0', 'manage:job:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(28014, '补货任务删除', 2801, 4, '', '', '', 1, 0, 'F', '0', '0', 'manage:job:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(28015, '补货任务导出', 2801, 5, '', '', '', 1, 0, 'F', '0', '0', 'manage:job:export', '#', 'admin', sysdate(), '', NULL, '');

-- =============================================
-- 四、元数据管理（新建一级菜单，排序 10）
-- =============================================
INSERT INTO sys_menu VALUES(2900, '元数据管理', 0, 10, 'metaData', NULL, '', 1, 0, 'M', '0', '0', '', 'documentation', 'admin', sysdate(), '', NULL, '元数据管理目录');

-- 元数据管理 - 元数据列表
INSERT INTO sys_menu VALUES(2901, '元数据列表', 2900, 1, 'index', 'manage/metaData/index', '', 1, 0, 'C', '0', '0', 'metaData:list', 'list', 'admin', sysdate(), '', NULL, '元数据列表菜单');
INSERT INTO sys_menu VALUES(29011, '元数据查询', 2901, 1, '', '', '', 1, 0, 'F', '0', '0', 'metaData:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(29012, '元数据新增', 2901, 2, '', '', '', 1, 0, 'F', '0', '0', 'metaData:add', '#', 'admin', sysdate(), '', NULL, '');

-- =============================================
-- 五、数据等级标注（新建一级菜单，排序 11）
-- =============================================
INSERT INTO sys_menu VALUES(3000, '数据等级标注', 0, 11, 'dataLevelLabel', NULL, '', 1, 0, 'M', '0', '0', '', 'log', 'admin', sysdate(), '', NULL, '数据等级标注目录');

-- 数据等级标注 - 标注列表
INSERT INTO sys_menu VALUES(3001, '标注列表', 3000, 1, 'index', 'manage/dataLevelLabel/index', '', 1, 0, 'C', '0', '0', 'system:level:list', 'list', 'admin', sysdate(), '', NULL, '数据等级标注菜单');
INSERT INTO sys_menu VALUES(30011, '数据等级查询', 3001, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:level:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(30012, '数据等级新增', 3001, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:level:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(30013, '数据等级修改', 3001, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:level:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(30014, '数据等级删除', 3001, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:level:remove', '#', 'admin', sysdate(), '', NULL, '');
