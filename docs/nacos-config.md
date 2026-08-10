# Nacos configuration

This project can load backend configuration from Nacos.

## Start Nacos

```powershell
docker compose -f docker-compose.nacos.yml up -d
```

Console:

```text
http://127.0.0.1:8848/nacos
```

Local credentials are `nacos` / `nacos`.

## Publish configuration

```powershell
.\bin\publish-nacos-config.ps1
```

The script publishes:

- `icm-admin.yaml`
- `icm-admin-druid.yaml`

The backend loads these through `spring.application.name=icm-admin` and `spring.profiles.active=druid`.

## Runtime variables

Set real secrets as environment variables before starting the backend:

```powershell
$env:MYSQL_PASSWORD = "your-mysql-password"
$env:REDIS_PASSWORD = "your-redis-password"
$env:TOKEN_SECRET = "your-long-random-jwt-secret"
$env:ALIYUN_OSS_ACCESS_KEY = "your-access-key"
$env:ALIYUN_OSS_SECRET_KEY = "your-secret-key"
```

Use `NACOS_CONFIG_ENABLED=false` to start with local fallback configuration only.
