-- 根据现有 Java 代码（domain 实体类 + mapper XML/接口）反向推导生成的建表语句。
-- 这些表在仓库现有的 SQL 文件里都没有建表语句，是以下功能正常运行所必需的：
--   数据分级标签的筛选下拉、插码管理平台字段维护、自定义数据管理规则、字段校验规则配置、渠道号管理。
-- 字段类型/长度是根据 Java 字段类型和 @Size/@Excel 等注解推断的，不是从真实生产库还原出来的，
-- 如果后续和实际业务需求有出入（尤其是字符串长度、是否允许为空），请按需调整。

-- ----------------------------
-- 1、自定义数据管理信息表 tb_customize_data_manage_info
-- ----------------------------
DROP TABLE IF EXISTS tb_customize_data_manage_info;
CREATE TABLE tb_customize_data_manage_info (
  id                     BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  rule_name              VARCHAR(200) NOT NULL COMMENT '规则名称',
  rule_desc              VARCHAR(500) NOT NULL COMMENT '规则描述',
  creator                VARCHAR(64) DEFAULT NULL COMMENT '创建人',
  create_time            DATETIME DEFAULT NULL COMMENT '创建时间',
  update_time            DATETIME DEFAULT NULL COMMENT '更新时间',
  check_field            VARCHAR(200) NOT NULL COMMENT '校验字段',
  data_source_id         VARCHAR(100) NOT NULL COMMENT '数据源ID',
  event_type             VARCHAR(100) DEFAULT NULL COMMENT '事件类型',
  market_code            VARCHAR(100) DEFAULT NULL COMMENT '营销活动码',
  page_url               VARCHAR(500) DEFAULT NULL COMMENT '页面链接',
  statistics_time_start  DATETIME NOT NULL COMMENT '统计时间-开始',
  statistics_time_end    DATETIME NOT NULL COMMENT '统计时间-结束',
  compare_time_start     DATETIME DEFAULT NULL COMMENT '对比时间-开始',
  compare_time_end       DATETIME DEFAULT NULL COMMENT '对比时间-结束',
  consume_time           VARCHAR(50) DEFAULT NULL COMMENT '查询耗时ms',
  status                 VARCHAR(10) DEFAULT '0' COMMENT '任务状态：0=默认，1=运行中，2=成功，3=失败',
  create_by              VARCHAR(64) DEFAULT NULL COMMENT '创建人id',
  update_by              VARCHAR(64) DEFAULT NULL COMMENT '修改人id',
  statistics_content     TEXT COMMENT '具体的数据内容(JSON)',
  query_start_time       DATETIME DEFAULT NULL COMMENT 'clickhouse任务开始时间',
  query_end_time         DATETIME DEFAULT NULL COMMENT 'clickhouse任务结束时间',
  task_id                VARCHAR(64) DEFAULT NULL COMMENT '异步任务id',
  error_msg              VARCHAR(1000) DEFAULT NULL COMMENT '异常原因',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自定义数据管理信息表';

-- ----------------------------
-- 2、字段校验规则配置表 tb_field_validation_config
-- ----------------------------
DROP TABLE IF EXISTS tb_field_validation_config;
CREATE TABLE tb_field_validation_config (
  id                      BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  datasource_id           VARCHAR(500) NOT NULL COMMENT '数据源标识（支持多个，逗号分隔）',
  table_name              VARCHAR(100) DEFAULT NULL COMMENT 'ClickHouse表名',
  sample_count            INT NOT NULL COMMENT '抽样条数',
  field_identification    VARCHAR(100) NOT NULL COMMENT '字段标识',
  field_name              VARCHAR(100) DEFAULT NULL COMMENT '字段中文名',
  is_not_null             TINYINT(1) NOT NULL DEFAULT 0 COMMENT '非空校验 0-否 1-是',
  is_encrypted            TINYINT(1) DEFAULT 0 COMMENT '是否加密字段 0-否 1-是',
  format_validation_type  VARCHAR(20) DEFAULT NULL COMMENT '格式校验类型 date/url/none',
  date_format             VARCHAR(50) DEFAULT NULL COMMENT '日期格式',
  url_protocol            VARCHAR(20) DEFAULT NULL COMMENT 'URL协议 http/https/both',
  is_content_number       TINYINT(1) DEFAULT 0 COMMENT '包含数字 0-否 1-是',
  is_content_chinese      TINYINT(1) DEFAULT 0 COMMENT '包含汉字 0-否 1-是',
  is_content_lowercase    TINYINT(1) DEFAULT 0 COMMENT '包含小写字母 0-否 1-是',
  is_content_uppercase    TINYINT(1) DEFAULT 0 COMMENT '包含大写字母 0-否 1-是',
  custom_regex            VARCHAR(500) DEFAULT NULL COMMENT '自定义正则表达式',
  length_min              INT DEFAULT NULL COMMENT '最小长度',
  length_max              INT DEFAULT NULL COMMENT '最大长度',
  enum_values             VARCHAR(1000) DEFAULT NULL COMMENT '枚举值（逗号分隔）',
  is_enabled              TINYINT(1) DEFAULT 1 COMMENT '是否启用 0-否 1-是',
  create_by               VARCHAR(64) DEFAULT '' COMMENT '创建者',
  create_time             DATETIME DEFAULT NULL COMMENT '创建时间',
  update_by               VARCHAR(64) DEFAULT '' COMMENT '更新者',
  update_time             DATETIME DEFAULT NULL COMMENT '更新时间',
  remark                  VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id),
  KEY idx_field_identification (field_identification)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字段校验规则配置表';

-- ----------------------------
-- 3、插码管理平台-字段维护表 tb_coding_scheme_field_maintenance
-- ----------------------------
DROP TABLE IF EXISTS tb_coding_scheme_field_maintenance;
CREATE TABLE tb_coding_scheme_field_maintenance (
  id                       BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  field_name               VARCHAR(100) DEFAULT NULL COMMENT '字段名称',
  field_identification     VARCHAR(100) DEFAULT NULL COMMENT '字段标识',
  field_type               VARCHAR(50) DEFAULT NULL COMMENT '字段数据类型',
  maintenance_time         VARCHAR(50) DEFAULT NULL COMMENT '维护时间',
  apply_scope              VARCHAR(100) DEFAULT NULL COMMENT '适用范围',
  is_scope_h5              TINYINT(1) DEFAULT 0 COMMENT '是否适用H5 0-否 1-是',
  is_scope_native          TINYINT(1) DEFAULT 0 COMMENT '是否适用原生 0-否 1-是',
  is_scope_mini            TINYINT(1) DEFAULT 0 COMMENT '是否适用小程序 0-否 1-是',
  is_required              TINYINT(1) DEFAULT 0 COMMENT '是否必传 0-否 1-是',
  max_length               INT DEFAULT NULL COMMENT '最大长度',
  is_content_digit         TINYINT(1) DEFAULT 0 COMMENT '是否包含数字 0-否 1-是',
  is_content_letter        TINYINT(1) DEFAULT 0 COMMENT '是否包含字母 0-否 1-是',
  is_content_chinese       TINYINT(1) DEFAULT 0 COMMENT '是否包含汉字 0-否 1-是',
  is_content_underscore    TINYINT(1) DEFAULT 0 COMMENT '是否包含下划线 0-否 1-是',
  business_description     VARCHAR(500) DEFAULT NULL COMMENT '业务口径说明',
  technical_description    VARCHAR(500) DEFAULT NULL COMMENT '技术口径说明',
  create_by                VARCHAR(64) DEFAULT NULL COMMENT '创建者',
  create_time              DATETIME DEFAULT NULL COMMENT '创建时间',
  update_by                VARCHAR(64) DEFAULT NULL COMMENT '更新者',
  update_time              DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_field_identification (field_identification)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='插码管理平台-字段维护表';

-- ----------------------------
-- 4、渠道号管理表 tb_coding_scheme_channel_number_manage
-- ----------------------------
DROP TABLE IF EXISTS tb_coding_scheme_channel_number_manage;
CREATE TABLE tb_coding_scheme_channel_number_manage (
  id                     BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  channel_name           VARCHAR(100) DEFAULT NULL COMMENT '渠道号名称（不可重复）',
  channel_number         VARCHAR(100) DEFAULT NULL COMMENT '渠道号',
  stand_time             VARCHAR(50) DEFAULT NULL COMMENT '维护时间',
  jie_kou                VARCHAR(500) DEFAULT NULL COMMENT '所属接口，如 51006,51007,51010,270_csap_77011',
  contact                VARCHAR(64) DEFAULT NULL COMMENT '联系人',
  contact_depart         VARCHAR(100) DEFAULT NULL COMMENT '联系人部门',
  user_name              VARCHAR(64) DEFAULT NULL COMMENT '当前用户名',
  user_role_name         VARCHAR(64) DEFAULT NULL COMMENT '当前用户角色',
  requirement_depart     VARCHAR(100) DEFAULT NULL COMMENT '需求部门',
  requirement_content    VARCHAR(500) DEFAULT NULL COMMENT '需求内容',
  contact_phone          VARCHAR(20) DEFAULT NULL COMMENT '联系方式',
  is_disabled            TINYINT(1) DEFAULT 0 COMMENT '是否禁用',
  create_by              VARCHAR(64) DEFAULT NULL COMMENT '创建者',
  create_time            DATETIME DEFAULT NULL COMMENT '创建时间',
  update_by              VARCHAR(64) DEFAULT NULL COMMENT '更新者',
  update_time            DATETIME DEFAULT NULL COMMENT '更新时间',
  remark                 VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id),
  UNIQUE KEY uk_channel_name (channel_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='渠道号管理表';

-- ----------------------------
-- 5、渠道管理表 tb_coding_scheme_channel_manage（渠道选项下拉查询用）
-- ----------------------------
DROP TABLE IF EXISTS tb_coding_scheme_channel_manage;
CREATE TABLE tb_coding_scheme_channel_manage (
  id            BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  channel_id    VARCHAR(100) DEFAULT NULL COMMENT '渠道ID',
  channel_name  VARCHAR(100) DEFAULT NULL COMMENT '渠道名称',
  create_by     VARCHAR(64) DEFAULT NULL COMMENT '创建者',
  create_time   DATETIME DEFAULT NULL COMMENT '创建时间',
  update_by     VARCHAR(64) DEFAULT NULL COMMENT '更新者',
  update_time   DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='渠道管理表（供渠道选项下拉查询使用）';

-- ----------------------------
-- 6、元数据事件表 tb_metadata_event
-- 注意：EventController 目前是空实现（没有任何接口方法），代码里除了
-- "SELECT DISTINCT event_code FROM tb_metadata_event" 这一条查询之外，没有其它地方引用这张表，
-- 这里只能按这一条查询补最小结构，后续需要业务实际落地后再补充字段。
-- ----------------------------
DROP TABLE IF EXISTS tb_metadata_event;
CREATE TABLE tb_metadata_event (
  id            BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  event_code    VARCHAR(100) DEFAULT NULL COMMENT '事件编码',
  create_by     VARCHAR(64) DEFAULT NULL COMMENT '创建者',
  create_time   DATETIME DEFAULT NULL COMMENT '创建时间',
  update_by     VARCHAR(64) DEFAULT NULL COMMENT '更新者',
  update_time   DATETIME DEFAULT NULL COMMENT '更新时间',
  remark        VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='元数据事件表（占位表，字段尚不完整，仅按现有查询补齐）';
