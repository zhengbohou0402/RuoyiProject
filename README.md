<p align="center">
<img alt="logo" src="https://likede2-admin.itheima.net/img/logo.3673fab5.png" style="background-color:#5373e0; padding:12px; border-radius:8px;">
</p>
<h1 align="center">插码管理平台 (icm)</h1>
<h4 align="center">基于 RuoYi（SpringBoot + Vue 前后端分离）的用户行为数据采集与插码流程管理平台</h4>

---

## 一、项目简介

插码管理平台是一套**用户行为数据采集（埋点）全流程管理平台**，面向移动 App / H5 / 小程序的埋点接入与数据治理场景。平台围绕"插码（埋点）"这一核心流程，提供从**字段元数据维护、渠道号管理、数据等级标注、数据质量校验、自定义规则统计**到**用户行为日志/轨迹实时监控**的完整能力，帮助运营与技术人员验证埋点上报是否成功、数据是否符合预期。

本仓库为后端主工程（Spring Boot 多模块），前端工程位于 `icm-vue` 目录。

## 二、技术栈

| 分类 | 选型 |
|---|---|
| 语言/框架 | Java 11 · Spring Boot 2.5.15 · Spring Security + JWT |
| 持久层 | MyBatis · PageHelper · Druid 连接池 |
| 数据存储 | **MySQL**（业务主数据）· **ClickHouse**（埋点行为大数据）· **Redis**（缓存/登录态） |
| 实时推送 | **WebSocket**（行为数据实时监控长链接） |
| 前端 | Vue 3 · Vite · Element Plus · ECharts |
| 构建 | Maven 多模块 · `icm.sh` / `icm.bat` 启动脚本 |

## 三、模块结构

```
icm-parent
├── icm-admin        # 启动入口（Application + 系统 Web 层 + 运行时配置）
├── icm-framework    # 框架层：安全、WebSocket、Redis、多数据源、MyBatis 配置
├── icm-common       # 通用：工具类、常量、注解、基础模型
├── icm-system       # 系统管理：用户/角色/菜单/字典/部门/岗位/日志
├── icm-manage       # 业务核心：插码管理、行为日志/轨迹、数据质量、规则统计
├── icm-quartz       # 定时任务
├── icm-generator    # 代码生成
├── icm-websocket    # WebSocket 长链接模块（连接池/心跳/业务推送）
├── icm-vue          # 前端工程（Vite + Vue3）
└── sql              # 数据库脚本（建表 + 初始化数据）
```

## 四、核心功能

### 4.1 插码流程管理（业务核心）
- **字段维护**：埋点字段元数据（字段标识、类型、适用范围 H5/原生/小程序、必传/长度/内容规则、业务/技术口径）
- **渠道号管理**：渠道号与所属接口、需求部门、联系人的登记与维护
- **数据等级标注**：渠道 × 事件 双维度等级标注（核心/重要），支持批量笛卡尔积新增、冲突检测 + 强制覆盖
- **数据质量校验**：字段非空/加密/格式/正则/长度/枚举等规则配置，对 ClickHouse 采样数据做质量核查
- **自定义规则统计**：按渠道/事件/营销码/页面链接多条件筛选，任意维度分组统计数量与占比，支持统计期 vs 对比期的变化率分析

### 4.2 用户行为数据查询与实时监控
- **用户行为日志**（MySQL `tb_real_time_data_verification`）：实时验证埋点上报数据，列表查询 + 增量轮询
- **用户行为轨迹**（ClickHouse 轨迹表）：按手机号/时间范围查询行为轨迹明细，支持导出 Excel
- **模拟长连接 → 真实长链接升级**：
  - 原实现：HTTP 3 秒轮询增量查询（`/incremental` 接口，`queryTime/queryEndTime` 游标）
  - 升级实现：**WebSocket 长链接**（`/ws/{scene}/{userMobile}`），数据生成器写入后**毫秒级实时推送**，支持心跳保活、token 握手鉴权、断线指数退避重连、轮询降级兜底
- **测试数据生成器**：MySQL / ClickHouse 双通道模拟埋点数据（线程安全、批量插入、TPS 统计、自动停止）

### 4.3 系统管理（RuoYi 原生能力）
用户/角色/菜单/部门/字典/岗位、操作日志、登录日志、定时任务、代码生成、缓存监控、服务监控、Druid 监控等。

## 五、技术亮点

1. **多数据源架构**：`@DataSource` 注解 + 自定义 `DynamicDataSource`（继承 AbstractRoutingDataSource），按业务自动切换 MySQL / ClickHouse，try/finally 清理 ThreadLocal 防串库
2. **ClickHouse 分页踩坑治理**：PageHelper 三参数重载会生成双 ORDER BY、且无 ClickHouse 方言——行为轨迹查询绕开 PageHelper，改走全量查询 + Redis/Caffeine 多级缓存 + 内存分页
3. **多级缓存降级**：Redis（分布式）+ Caffeine（本地）双级缓存，Redis 不可用时 `@Autowired(required=false)` 自动降级本地缓存，功能不中断
4. **WebSocket 业务化**：复用连接池与消息协议，新增按「场景 × 手机号」订阅端点，生成器写入后服务端主动推送
5. **自定义规则动态 SQL**：CTE 分层（筛选→时间口径统一→分组计数→占比），`${checkField}` 动态维度，FULL OUTER JOIN 实现统计/对比期变化率

## 六、快速开始

### 6.1 环境准备
- JDK 11+、Maven 3.6+、Node 16+
- MySQL 8.0、ClickHouse（可选，轨迹/统计功能需要）、Redis 5+

### 6.2 初始化数据库
```bash
# 创建数据库 dkd（如需改库名请同步修改 application-druid.yml）
mysql -uroot -p -e "CREATE DATABASE dkd DEFAULT CHARACTER SET utf8mb4;"
# 依次导入
mysql -uroot -p dkd < sql/ry_20231130.sql        # 若依基础表 + 数据
mysql -uroot -p dkd < sql/quartz.sql             # 定时任务表
mysql -uroot -p dkd < sql/create_missing_metadata_tables.sql  # 插码管理业务表
mysql -uroot -p dkd < sql/tb_user_behavior_log.sql  # 实时数据验证表（含测试数据）
mysql -uroot -p dkd < sql/sys_white_mobile.sql   # 行为轨迹白名单表
```

### 6.3 配置
- `icm-admin/src/main/resources/application.yml`：Redis、Token、文件上传路径
- `icm-admin/src/main/resources/application-druid.yml`：MySQL / ClickHouse 数据源
- 云存储密钥通过环境变量注入：`ALIYUN_OSS_ACCESS_KEY` / `ALIYUN_OSS_SECRET_KEY`

### 6.4 启动后端
```bash
# Linux / macOS
./icm.sh start
# Windows
icm.bat
# 或直接
mvn -pl icm-admin -am clean package -DskipTests
java -jar icm-admin/target/icm-admin.jar
```
默认地址：http://localhost:8080 · 默认账号：admin / admin123

### 6.5 启动前端
```bash
cd icm-vue
npm install
npm run dev
```
默认地址：http://localhost:8081（开发环境通过 `/dev-api` 代理到后端 8080）

## 七、数据库脚本说明

| 脚本 | 用途 |
|---|---|
| `ry_20231130.sql` | 若依基础表（用户/角色/菜单/字典等）+ 初始化数据 |
| `quartz.sql` | Quartz 定时任务表 |
| `create_missing_metadata_tables.sql` | 插码管理业务表（字段维护/渠道号/自定义规则/校验规则/元数据事件） |
| `tb_user_behavior_log.sql` | 实时数据验证表 + 测试数据 |
| `sys_white_mobile.sql` | 行为轨迹查询手机号白名单 |

> ⚠️ `cleanup_vending_machine.sql` 为清理脚本（删除售货机旧表），如无需要请勿执行。

## 八、相关文档

- [需求文档-用户行为实时监控-WebSocket长链接](docs/需求文档-用户行为实时监控-WebSocket长链接.md)
- [面试题库-插码管理平台](docs/面试题库-插码管理平台.md)

## 九、许可证

MIT License
