# SMT 工单管理系统

基于 Spring Boot + Vue 3 的 SMT（表面贴装技术）工单管理系统，是我的软件工程项目式教学大作业，支持工单全生命周期管理、工序报工、产量质量统计等功能。

## 技术栈

| 层级         | 技术                          | 版本               |
|------------|-----------------------------|------------------|
| **后端框架**   | Spring Boot                 | 4.0.4            |
| **安全框架**   | Spring Security + JWT + RSA |                  |
| **ORM**    | MyBatis                     | 3.x              |
| **数据库**    | MySQL                       | 9.x              |
| **缓存**     | Redis                       | Alpine           |
| **前端框架**   | Vue 3 + Vite                | Vue 3.5 / Vite 7 |
| **UI 组件库** | Element Plus                | 2.13             |
| **图表**     | ECharts                     | 6.1              |
| **反向代理**   | Nginx                       | Alpine           |
| **容器化**    | Docker + Docker Compose     |                  |

## 功能模块

- **用户认证**：JWT 登录认证，RSA 加密传输密码，Cookie 存储 Token
- **部门管理**：部门的增删改查
- **产线管理**：产线 CRUD + 计划/完成数量统计
- **产品管理**：产品 CRUD + 产量统计
- **用户管理**：系统用户的增删改查、多角色支持
- **工单管理**：工单全生命周期管理（创建、删除、更新、查询）
- **工序报工**：工单下的工序报工记录（良品/不良统计）
- **统计报表**：产量统计、质量统计、产线效率统计

### 角色权限

| 角色       | 权限        |
|----------|-----------|
| 管理员（1）   | 全部功能      |
| 生产计划员（2） | 工单管理、工序报工 |
| 车间操作员（3） | 工序报工      |
| 企业管理层（4） | 查看数据报表    |

## 项目结构

```
smt-workorder-management/
├── backend/                         # 后端 Spring Boot 项目
│   └── smt-workorder-management/
│       ├── Dockerfile               # 构建后端容器镜像指令文件
│       ├── pom.xml
│       └── src/
│           ├── main/java/.../
│           │   ├── common/          # 通用类（异常、统一返回）
│           │   ├── config/          # Spring Security、Redis 等配置
│           │   ├── controller/      # REST 控制器
│           │   ├── dto/             # 数据传输对象
│           │   ├── entity/          # 实体类
│           │   ├── mapper/          # MyBatis Mapper
│           │   ├── service/         # 业务逻辑层
│           │   └── util/            # 工具类（JWT、RSA）
│           └── main/resources/
│               ├── mapper/          # MyBatis XML
│               └── application.yml  # 应用配置
├── frontend/                        # 前端 Vue 3 项目
│   └── vue-SMT-Work-Order-Management-System/
│       ├── package.json
│       └── dist/                    # 构建产物（部署用）
├── mysql/
│   ├── conf.d/charset.cnf          # MySQL 字符集配置
│   └── init/
│       ├── 01-schema.sql            # 建表脚本
│       └── 02-data.sql              # 初始数据
├── nginx/
│   └── conf.d/default.conf          # Nginx HTTPS 完整配置（含 gzip、缓存、安全头）
├── docker-compose.yml               # 容器编排配置文件
├── docker-compose.local.yml         # 本地开发覆盖配置（暴露 MySQL/Redis 端口）
```

## 快速开始

### 前置条件

- Java 21+
- Node.js 22+
- Maven 3.9+
- Docker & Docker Compose
- MySQL 9.x（本地开发时）

### 本地开发

**1. 启动 MySQL 和 Redis**

```bash
# 使用本地开发覆盖配置启动数据库和缓存（暴露端口到宿主机）
docker compose -f docker-compose.yml -f docker-compose.local.yml up -d mysql redis
```

> `docker-compose.local.yml` 会将 MySQL 和 Redis 端口映射到宿主机，使本地运行的后端能够连接。生产部署时使用 `docker compose up -d`（不含 local 文件），端口不会暴露到公网。

**2. 启动后端**

```bash
cd backend/smt-workorder-management
mvn spring-boot:run
```

**3. 启动前端**

```bash
cd frontend/vue-SMT-Work-Order-Management-System
npm install
npm run dev
```

前端开发服务器默认运行在 `http://localhost:5173`，Vite 代理会把 `/api/*` 转发到后端 `http://localhost:8080`。

### Docker 一键部署

```bash
# 1. 创建宿主机数据目录
sudo mkdir -p /data/mysql /data/redis /data/nginx/{html,conf,logs}

# 2. 复制前端文件到 Nginx 目录
sudo cp -r frontend/vue-SMT-Work-Order-Management-System/dist/* /data/nginx/html/
sudo cp nginx/conf.d/default.conf /data/nginx/conf/default.conf

# 3. 构建并启动所有容器
docker compose up -d

# 4. 查看运行状态
docker compose ps

# 5. 查看后端日志
docker compose logs -f backend
```

启动成功后访问：

- **前端页面**：`http://localhost`
- **后端 API**：通过 Nginx 代理 `http://localhost/api/*`

### HTTPS 配置（生产环境）

项目已内置 HTTPS 完整支持，使用 Let's Encrypt 免费证书 + Certbot 自动续期。

> **重要提示**：证书签发过程中有几个常见坑点，请严格按照以下步骤操作，不要跳步。

#### 第一步：创建证书存储目录

```bash
sudo mkdir -p /data/nginx/certs /data/nginx/certbot
```

#### 第二步：停止 Nginx（关键！）

Certbot standalone 模式需要占用 80 端口启动临时验证服务器，Nginx 必须先停掉：

```bash
docker compose stop nginx
```

> **常见错误**：不停止 Nginx 会导致 Let's Encrypt 的验证请求被 Nginx 拦截，返回 Vue 首页 HTML 而非验证令牌，报错 `Invalid response`。

#### 第三步：签发证书

```bash
sudo docker run --rm \
  -v /data/nginx/certs:/etc/letsencrypt \
  -p 80:80 \
  certbot/certbot certonly --standalone \
  -d your-domain.com \
  -d www.your-domain.com \
  --email your-email@example.com \
  --agree-tos --non-interactive
```

#### 第四步：修复证书目录权限（关键！）

Let's Encrypt 生成的证书目录默认权限为 `700`（仅 root 可访问），Nginx 容器内的 `nginx` 用户无法读取，必须手动修复：

```bash
sudo chmod 755 /data/nginx/certs
sudo chmod 755 /data/nginx/certs/live
sudo chmod 755 /data/nginx/certs/live/your-domain.com
sudo chmod 755 /data/nginx/certs/archive
sudo chmod 755 /data/nginx/certs/archive/your-domain.com
sudo chmod 644 /data/nginx/certs/archive/your-domain.com/*
```

> **常见错误**：不修复权限会导致 Nginx 报 `cannot load certificate ... No such file or directory`，即使文件确实存在。这是因为 `nginx` 用户没有权限穿越 `live` 和 `archive` 目录。

#### 第五步：启动 Nginx

```bash
# 复制 HTTPS 配置到 Nginx 配置目录
sudo cp nginx/conf.d/default.conf /data/nginx/conf/default.conf

# 启动服务
docker compose up -d
```

#### 验证 HTTPS

```bash
# 测试 HTTPS 是否正常
curl -Ik --noproxy '*' https://your-domain.com

# 测试 HTTP → HTTPS 重定向
curl -I --noproxy '*' http://your-domain.com
# 应返回 301 Moved Permanently
```

#### 证书自动续期

Certbot 容器每 12 小时自动检查并续期即将过期的证书，无需手动干预。续期通过 webroot 模式完成（利用 Nginx 的 `/.well-known/acme-challenge/` 路径），不会中断服务。

手动触发续期测试：

```bash
docker exec smt-certbot certbot renew --webroot -w /var/www/certbot --dry-run
```

#### 故障排查速查表

| 错误信息 | 原因 | 解决方案 |
|---------|------|---------|
| `Invalid response ... "<!DOCTYPE html>"` | Nginx 占用了 80 端口 | 先执行 `docker compose stop nginx` |
| `cannot load certificate ... No such file or directory` | 证书目录权限为 700 | 执行第四步的 `chmod` 命令 |
| `Failed to connect to ... port 443` | 云安全组未开放 443 端口 | 在云控制台添加入方向规则 |
| `http2 directive is deprecated` | Nginx 新版语法变更 | 已修复：使用 `http2 on;` 替代 `listen ... http2` |

#### 架构说明

Nginx 配置直接引用 Let's Encrypt 的 live 目录：

```
/etc/nginx/certs/
├── live/
│   └── your-domain.com/
│       ├── fullchain.pem  → ../../archive/.../fullchain1.pem  （证书链）
│       └── privkey.pem    → ../../archive/.../privkey1.pem    （私钥）
└── archive/
    └── your-domain.com/
        ├── fullchain1.pem   （真实证书文件）
        └── privkey1.pem     （真实私钥文件）
```

Certbot 续期时更新 archive 目录下的真实文件，live 目录下的符号链接自动指向新证书，Nginx 无需重启即可读取新证书。

> 为什么不使用手动符号链接（如 `/etc/nginx/certs/fullchain.pem → live/.../`）？
> 
> 每增加一层符号链接就增加一层权限检查，容易因目录权限问题导致解析失败。直接引用 live 路径更可靠。

### 容器架构

```
浏览器 :80 / :443
    │
    ▼
┌─────────┐    /api/*     ┌──────────┐    ┌─────────┐
│  Nginx  │ ────────────► │ Backend  │───►│  MySQL  │
│ 80/443  │               │ :8080    │    │ :3306   │
└─────────┘               └──────────┘    └─────────┘
                                    │
                                    └───►  ┌─────────┐
                                    |      │  Redis  │
                                    |      │ :6379   │
                                    |      └─────────┘
                                    └───► ┌─────────┐
                                          │ Certbot │
                                          │ 自动续期  │
                                          └─────────┘
```

## API 接口

> 所有 API 前缀：`/api`（由 Nginx 转发到后端）

### 认证

| 方法   | 路径               | 说明                     |
|------|------------------|------------------------|
| GET  | `/api/publickey` | 获取 RSA 公钥（前端加密密码用）     |
| POST | `/api/login`     | 用户登录，返回 JWT（通过 Cookie） |
| POST | `/api/logout`    | 退出登录，清除 JWT            |

### 部门管理

| 方法     | 路径                            | 说明   |
|--------|-------------------------------|------|
| POST   | `/api/department/add`         | 添加部门 |
| DELETE | `/api/department/delete/{id}` | 删除部门 |
| PUT    | `/api/department/update`      | 修改部门 |
| POST   | `/api/department/find`        | 查询部门 |

### 产线管理

| 方法     | 路径                      | 说明        |
|--------|-------------------------|-----------|
| POST   | `/api/line/add`         | 添加产线      |
| DELETE | `/api/line/delete/{id}` | 删除产线      |
| PUT    | `/api/line/update`      | 更新产线      |
| GET    | `/api/line/find/{id}`   | 查询产线      |
| GET    | `/api/line/findAll`     | 查询所有产线    |
| GET    | `/api/line/statistics`  | 产线计划/完成统计 |

### 产品管理

| 方法     | 路径                         | 说明       |
|--------|----------------------------|----------|
| POST   | `/api/product/add`         | 添加产品     |
| DELETE | `/api/product/delete/{id}` | 删除产品     |
| PUT    | `/api/product/update`      | 更新产品     |
| GET    | `/api/product/find/{id}`   | 查询产品     |
| POST   | `/api/product/query`       | 条件查询产品   |
| GET    | `/api/product/findAll`     | 分页查询所有产品 |
| GET    | `/api/product/statistics`  | 产品产量统计   |

### 用户管理

| 方法     | 路径                    | 说明         |
|--------|-----------------------|------------|
| POST   | `/api/user/add`       | 添加用户       |
| DELETE | `/api/user/delete`    | 批量删除用户     |
| POST   | `/api/user/list`      | 条件查询用户     |
| GET    | `/api/user/findAll`   | 分页查询所有用户   |
| GET    | `/api/user/find/{id}` | 根据 ID 查询用户 |
| PUT    | `/api/user/update`    | 修改用户       |

### 角色管理

| 方法     | 路径                         | 说明     |
|--------|----------------------------|--------|
| POST   | `/api/sysRole/add`         | 添加角色   |
| PUT    | `/api/sysRole/update`      | 修改角色   |
| DELETE | `/api/sysRole/delete/{id}` | 删除角色   |
| GET    | `/api/sysRole/find/{id}`   | 查询角色   |
| GET    | `/api/sysRole/list`        | 查询所有角色 |

### 工单管理

| 方法     | 路径                         | 说明       |
|--------|----------------------------|----------|
| POST   | `/api/workorder/add`       | 添加工单     |
| DELETE | `/api/workorder/delete`    | 批量删除工单   |
| PUT    | `/api/workorder/update`    | 更新工单     |
| GET    | `/api/workorder/find/{id}` | 查询工单     |
| GET    | `/api/workorder/findAll`   | 分页查询所有工单 |
| POST   | `/api/workorder/query`     | 条件查询工单   |
| GET    | `/api/workorder/detailed`  | 工单详细统计   |

### 工序报工

| 方法     | 路径                                      | 说明         |
|--------|-----------------------------------------|------------|
| POST   | `/api/workorder/process/add`            | 添加报工记录     |
| DELETE | `/api/workorder/process/delete/{id}`    | 删除报工记录     |
| PUT    | `/api/workorder/process/update`         | 更新报工记录     |
| GET    | `/api/workorder/process/find/{orderId}` | 查询工单的报工记录  |
| GET    | `/api/workorder/process/findAll`        | 分页查询所有报工记录 |
| GET    | `/api/workorder/process/statistics`     | 良品/不良统计    |

## 环境变量

后端通过环境变量配置，支持灵活切换：

| 变量                  | 说明           | 默认值                               |
|---------------------|--------------|-----------------------------------|
| `DB_URL`            | 数据库连接地址      | `jdbc:mysql://localhost:3306/...` |
| `DB_USERNAME`       | 数据库用户名       | `root`                            |
| `DB_PASSWORD`       | 数据库密码        | `88888888`                        |
| `REDIS_HOST`        | Redis 地址     | `localhost`                       |
| `REDIS_PORT`        | Redis 端口     | `6379`                            |
| `REDIS_PASSWORD`    | Redis 密码     | (无)                               |
| `REDIS_DATABASE`    | Redis 数据库    | `0`                               |
| `REDIS_TIMEOUT`     | Redis 超时时间   | `3000ms`                          |
| `JWT_SECRET`        | JWT 签名密钥     | （内置默认值）                           |
| `JWT_EXPIRATION`    | JWT 过期时间（毫秒） | `86400000`（1天）                    |
| `RSA_KEY_FILE_PATH` | RSA 密钥文件路径   | `./keys/`                         |

## 运维命令

```bash
# 查看所有容器状态
docker compose ps

# 查看后端日志
docker compose logs -f backend

# 重建后端镜像
docker compose up -d --build backend

# 更新前端（无需重启容器）
sudo cp -r frontend/vue-SMT-Work-Order-Management-System/dist/* /data/nginx/html/

# 重载 Nginx 配置
docker exec smt-nginx nginx -s reload

# 备份数据库
docker exec smt-mysql mysqldump -uroot -p smt_workorder_management > backup.sql

# 进入 MySQL 容器
docker exec -it smt-mysql mysql -uroot -p

# 停止所有容器
docker compose down

# 停止并删除数据卷（⚠️ 会清空数据库）
docker compose down -v
```

## 部署到云服务器

```bash
# 1. 上传项目到服务器
scp -r smt-workorder-management/ user@your-server:/opt/

# 2. 生成 RSA 密钥对
cd /opt/smt-workorder-management
mkdir -p keys
openssl genpkey -algorithm RSA -out keys/private.key -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in keys/private.key -out keys/public.key

# 3. 创建数据目录并启动
sudo mkdir -p /data/mysql /data/redis /data/nginx/{html,conf,logs}
sudo cp -r frontend/vue-SMT-Work-Order-Management-System/dist/* /data/nginx/html/
sudo cp nginx/conf.d/default.conf /data/nginx/conf/default.conf
docker compose up -d
```

## 安全建议

部署到生产环境前务必修改：

- [ ] 修改 `docker-compose.yml` 中的 `MYSQL_ROOT_PASSWORD` 和 `DB_PASSWORD`
- [ ] 修改 `docker-compose.yml` 中的 `JWT_SECRET`（使用随机强密钥）
- [ ] 生成新的 RSA 密钥对替换 `keys/` 目录
- [ ] 配置云服务器防火墙规则
- [ ] 生产环境不要将密钥和密码提交到 Git 仓库（已在 `.gitignore` 中排除 `keys/`）