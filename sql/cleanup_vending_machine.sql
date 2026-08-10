-- 清理售货机相关的表
DROP TABLE IF EXISTS tb_channel;
DROP TABLE IF EXISTS tb_emp;
DROP TABLE IF EXISTS tb_node;
DROP TABLE IF EXISTS tb_order;
DROP TABLE IF EXISTS tb_partner;
DROP TABLE IF EXISTS tb_policy;
DROP TABLE IF EXISTS tb_region;
DROP TABLE IF EXISTS tb_role;
DROP TABLE IF EXISTS tb_sku;
DROP TABLE IF EXISTS tb_sku_class;
DROP TABLE IF EXISTS tb_task;
DROP TABLE IF EXISTS tb_task_details;
DROP TABLE IF EXISTS tb_task_type;
DROP TABLE IF EXISTS tb_vending_machine;
DROP TABLE IF EXISTS tb_vm_type;

-- 清理售货机相关的菜单
-- 请根据实际的菜单名称自行调整，这里的删除操作较为危险，建议在执行前备份 sys_menu 表。
-- 示例：
-- DELETE FROM sys_menu WHERE menu_name IN ('点位管理', '设备管理', '工单管理', '商品管理', '策略管理', '合作商管理', '区域管理', '人员管理');
