# Mini-Mall 部署文档

## 环境要求

| 依赖 | 最低版本 | 说明 |
|------|---------|------|
| Docker | 20.10+ | 容器引擎 |
| Docker Compose | 2.0+ (`docker compose` 或 `docker-compose`) | 多容器编排 |

> 不再需要本地安装 Java、Maven、Node.js、MySQL、Redis —— 一切由 Docker 提供。

## 快速开始

```bash
# 1. 进入项目根目录
cd /path/to/2026-07-21-23-31-52

# 2. 构建并启动所有服务（首次约 5-10 分钟）
docker compose up -d --build

# 3. 查看启动日志（确认所有服务 healthy）
docker compose logs -f
# 看到 "Started MallApplication" 表示后端就绪

# 4. 等待 MySQL 健康检查通过后，查看初始化是否成功
docker compose exec mysql mysql -uroot -proot -e "USE mini_mall; SHOW TABLES;"
```

## 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 管理后台 | http://localhost | React 前端 (Nginx 80端口) |
| 后端 API | http://localhost:8080/api | SpringBoot 接口 |
| Swagger 文档 | http://localhost:8080/api/doc.html | Knife4j 在线文档 |

### 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 超级管理员 | admin | admin123 |
| 普通用户（通过注册创建） | — | — |

## 服务架构

```
┌──────────────────────────────────────────┐
│                 Browser                   │
│              http://localhost             │
└───────────────┬──────────────────────────┘
                │
┌───────────────▼──────────────────────────┐
│          frontend (Nginx :80)             │
│  ├─ /             → 静态 SPA             │
│  └─ /api/*        → proxy → backend      │
└───────────────┬──────────────────────────┘
                │
┌───────────────▼──────────────────────────┐
│          backend (Java :8080)            │
│     SpringBoot 2.7 + MyBatis + Redis     │
└──────┬──────────────────────┬────────────┘
       │                      │
┌──────▼──────┐        ┌──────▼──────┐
│    MySQL    │        │    Redis    │
│    :3306    │        │    :6379    │
└─────────────┘        └─────────────┘
```

## 目录结构（部署相关文件）

```
.
├── docker-compose.yml              # 服务编排
├── DEPLOY.md                       # 本文档
├── mall-backend/
│   ├── Dockerfile                  # 后端多阶段构建 (Maven → JRE)
│   └── src/main/resources/
│       ├── application.yml         # 本地开发配置
│       ├── application-docker.yml  # Docker 环境配置
│       └── sql/init.sql            # 库表 + 种子数据（自动执行）
└── mall-admin/
    ├── Dockerfile                  # 前端多阶段构建 (Node → Nginx)
    └── nginx.conf                  # Nginx SPA + API 反向代理
```

## 常用命令

```bash
# 启动（已构建过，跳过 --build 更快）
docker compose up -d

# 查看所有容器状态
docker compose ps

# 查看某个服务日志
docker compose logs backend -f --tail=50

# 重启单个服务
docker compose restart backend

# 停止所有服务
docker compose down

# 停止并删除数据卷（重置数据库！）
docker compose down -v

# 进入容器调试
docker compose exec backend sh
docker compose exec mysql mysql -uroot -proot mini_mall
docker compose exec redis redis-cli
```

## 配置说明

### 后端环境变量（可在 docker-compose.yml 中覆盖）

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `SPRING_PROFILES_ACTIVE` | `docker` | 激活 application-docker.yml |
| `TZ` | `Asia/Shanghai` | 时区 |

> 要修改数据库密码、Redis 连接等，编辑 `mall-backend/src/main/resources/application-docker.yml` 后重建：
> ```bash
> docker compose up -d --build backend
> ```

### 前端代理

生产环境中 Nginx 作为反向代理，`/api/*` 请求自动转发到后端容器。前端构建时不依赖 Vite 的 dev proxy。

### 数据库初始化

MySQL 容器首次启动时自动执行 `/docker-entrypoint-initdb.d/` 下的 `.sql` 文件。
`init.sql` 包含：
- 创建数据库 `mini_mall`
- 20 张业务表（用户、商品、订单、积分、优惠券、营销、权限等）
- 种子数据（管理员账号、角色权限、示例商品、示例优惠券等）

### 数据持久化

| 卷名 | 路径 | 用途 |
|------|------|------|
| `mysql-data` | 容器内 `/var/lib/mysql` | 数据库文件 |
| `redis-data` | 容器内 `/data` | Redis 持久化 (AOF) |

删除数据卷会丢失所有业务数据：
```bash
docker compose down -v && docker compose up -d --build
```

## 生产环境建议

1. **修改默认密码**：
   - `docker-compose.yml` 中 `MYSQL_ROOT_PASSWORD`
   - `application-docker.yml` 中 `spring.datasource.password`
   - `init.sql` 中管理员密码（BCrypt 哈希）

2. **修改 JWT 密钥**：`application-docker.yml` 中 `jwt.secret`，使用 `openssl rand -base64 64` 生成

3. **移除调试日志**：将 `application-docker.yml` 中 `logging.level` 改为 `info`

4. **不对外暴露数据库端口**：移除 `docker-compose.yml` 中 mysql/redis 的 `ports` 映射

5. **使用外部 Redis/MySQL**：将 `backend` 容器的 `SPRING_PROFILES_ACTIVE` 设为自定义 profile，指向外部地址

6. **HTTPS**：在前端 Nginx 前加一层 Caddy / Nginx 反代并配置 SSL 证书

## 故障排查

| 问题 | 可能原因 | 解决 |
|------|---------|------|
| 后端启动失败，连不上 MySQL | MySQL 初始化未完成 | 等待 `docker compose ps` 显示 mysql 为 healthy |
| 页面 404 | Nginx 未正确代理 | 检查 `nginx.conf` 中 `/api/` 是否指向 `backend:8080` |
| 前端构建慢 | npm 源慢 | Dockerfile 已使用 npmmirror 镜像源 |
| 数据表不存在 | init.sql 未执行 | `docker compose down -v && docker compose up -d` 重建 |
| 端口冲突 | 80 / 3306 / 6379 / 8080 被占用 | 修改 `docker-compose.yml` 中的 `ports` 映射 |
