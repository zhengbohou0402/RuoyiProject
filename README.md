# RuoYi Vending Machine Management Platform

This repository is a RuoYi-based Spring Boot backend for a vending machine
management platform. It keeps the standard RuoYi administration foundation and
adds business modules for vending machine operations, metadata governance,
quality checks, behavior tracking, and operational analytics.

## Tech Stack

- Java 11
- Spring Boot 2.5.x
- Spring Security and JWT
- MyBatis
- MySQL
- Redis
- Druid datasource pool
- Nacos Config Center
- ClickHouse for selected analytics/query workloads
- RuoYi admin framework modules

## Project Structure

- `dkd-admin`: application entrypoint and runtime configuration.
- `dkd-framework`: common framework configuration, security, web, Redis, and datasource support.
- `dkd-common`: shared utilities, constants, annotations, and base models.
- `dkd-system`: RuoYi system administration features.
- `dkd-quartz`: scheduled job support.
- `dkd-generator`: code generation support.
- `dkd-manage`: vending machine and data management business modules.
- `docker`: local infrastructure configuration.
- `docs`: operational notes and setup documentation.
- `sql`: database scripts and seed data.

## Business Modules

The platform currently covers these functional areas:

- Vending machine management: machine types, devices, channels, regions, and nodes.
- Product management: SKU categories, SKUs, and channel/product associations.
- Partner and employee operations: partners, employees, roles, and related management data.
- Task management: task types, task orders, and task details.
- Order and policy operations: orders, pricing/policy data, and operational rules.
- Dashboard analytics: task statistics, sales statistics, SKU ranking, regional sales collection, partner node ranking, and abnormal equipment views.
- Metadata management: coding scheme field metadata maintenance and attribute lookup.
- Data level labeling: batch label management with duplicate detection and confirmation flows.
- Event management: event API entrypoint for future event metadata and event tracking features.
- Data quality configuration: datasource options, field lists, rule creation, and rule lookup.
- Customized data management: custom rule lifecycle, channel lookup, start/stop controls, and detail queries.
- Real-time data verification: list, incremental query, and cursor/max-id endpoints for mobile data verification.
- Behavior logs: behavior event CRUD, export, and incremental log query support.
- User behavior tracking: query and export support for user behavior records.

## Nacos Configuration

Application configuration is prepared for Nacos Config Center.

Default data IDs:

- `dkd-admin.yaml`
- `dkd-admin-druid.yaml`

Local Nacos startup:

```powershell
docker compose -f docker-compose.nacos.yml up -d
```

Publish local configuration templates to Nacos:

```powershell
.\bin\publish-nacos-config.ps1
```

The local Nacos console is available at:

```text
http://127.0.0.1:8848/nacos
```

Default local credentials:

```text
nacos / nacos
```

More details are documented in `docs/nacos-config.md`.

## Runtime Configuration

Secrets and environment-specific values should be provided through environment
variables instead of being committed into source control.

Common variables:

- `NACOS_SERVER_ADDR`
- `NACOS_NAMESPACE`
- `NACOS_GROUP`
- `MYSQL_HOST`
- `MYSQL_PORT`
- `MYSQL_DATABASE`
- `MYSQL_USERNAME`
- `MYSQL_PASSWORD`
- `REDIS_HOST`
- `REDIS_PORT`
- `REDIS_PASSWORD`
- `TOKEN_SECRET`
- `CLICKHOUSE_HOST`
- `CLICKHOUSE_PORT`
- `CLICKHOUSE_DATABASE`
- `CLICKHOUSE_USERNAME`
- `CLICKHOUSE_PASSWORD`
- `ALIYUN_OSS_ACCESS_KEY`
- `ALIYUN_OSS_SECRET_KEY`

Swagger and Druid monitoring are disabled by default in the provided templates.
Enable them explicitly only in trusted environments.

## Local Development

Prerequisites:

- JDK 11
- Maven
- Docker Desktop
- MySQL
- Redis
- Optional: ClickHouse for analytics-related modules

Typical backend startup flow:

```powershell
docker compose -f docker-compose.nacos.yml up -d
.\bin\publish-nacos-config.ps1
mvn -pl dkd-admin -am spring-boot:run
```

If Nacos is not available, set:

```powershell
$env:NACOS_CONFIG_ENABLED = "false"
```

Then the application can fall back to local Spring configuration files.

## Security Notes

- Do not commit database passwords, cloud access keys, JWT secrets, or production tokens.
- Keep `allowMultiQueries=false` unless there is a reviewed and tested reason to enable it.
- Keep Swagger and Druid console access disabled or restricted outside local development.
- Rotate any secret that was previously committed to Git history.

## Verification Status

The Nacos configuration was verified locally by starting the Nacos container,
publishing both data IDs, and reading them back through the Nacos API.

Maven compilation was not executed in this workspace because neither `mvn` nor
`mvnw` is currently available.
