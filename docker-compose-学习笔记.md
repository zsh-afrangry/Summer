## 基于本项目的 Docker Compose 学习笔记（dev 与 prod 对照）

本笔记结合仓库中的 `docker-compose.dev.yml` 与 `docker-compose.prod.yml`，逐项讲解开发/生产两套编排的用途、结构与差异，并给出常用命令与排错要点。

### 1. 整体架构概览

- **开发环境（docker-compose.dev.yml）**：
  - `frontend`（Vue 开发服务器，热更新）
  - `backend`（Spring Boot 以 `mvn spring-boot:run` 方式运行，便于热加载）
  - `mysql`（5.7，挂载初始化 SQL）
  - `nginx`（反向代理到前端 dev server 与后端 API，统一入口）

- **生产环境（docker-compose.prod.yml）**：
  - `nginx`（直接承载前端静态文件并反代后端 `/api/`）
  - `backend`（Spring Boot 可执行 JAR 进程）
  - `mysql`（持久化数据卷 + 健康检查）
  - 三个服务均来自阿里云 ACR 的预构建镜像。

### 2. 开发环境编排详解（docker-compose.dev.yml）

- **frontend**：
  - 构建自 `frontend/Dockerfile.dev`，暴露 `3000` 端口（Vue CLI dev server）。
  - 通过多个 bind mount 将 `src/`、`public/`、`package.json`、`vue.config.js` 等映射到容器，支持热更新。
  - 使用命名卷 `frontend-node-modules` 作为容器内 `/app/node_modules`，避免宿主机与容器依赖冲突。
  - `depends_on: backend`，确保后端先起，前端接口可用。

- **backend**：
  - 构建自 `backend/Dockerfile.dev`，启动命令为 `mvn spring-boot:run`。
  - 将 `./backend/src` 与 `pom.xml` 映射到容器，便于代码改动后自动重新编译/热启动。
  - 暴露 `9000:8080`（容器内 Spring Boot 仍是 8080，宿主访问 9000）。
  - 使用命名卷 `backend-m2` 缓存 Maven 仓库（`/root/.m2`），加快依赖下载。

- **mysql**：
  - 基于 `mysql:5.7`，初始化环境变量：`MYSQL_ROOT_PASSWORD=root`，`MYSQL_DATABASE=my_blog`。
  - 端口映射 `3307:3306`（避免与宿主机已有 MySQL 冲突）。
  - 将 `./mysql/init` 挂载到 `/docker-entrypoint-initdb.d`，容器首次启动会自动执行 `database_init.sql` 初始化表与示例数据。
  - 持久化卷 `mysql-dev-data` 存储数据文件。

- **nginx（dev）**：
  - 使用镜像 `nginx:latest`，映射 `8080:80`。
  - 通过挂载 `nginx/nginx-dev.conf` 实现：
    - `/` 代理到 `frontend-dev:3000`（即 dev server，支持 WebSocket，用于热更新）。
    - `/api/` 代理到 `backend-dev:8080`（后端 API）。
  - 作用：在开发阶段统一入口为 `http://localhost:8080`，既可访问前端页面，也可走到后端 API，避免跨域与端口散乱。

- **与应用配置的对应关系**：
  - `backend/src/main/resources/application.properties` 中的 `spring.datasource.url` 指向主机名 `mysql`（容器名即服务名），与 compose 中的 `mysql` 服务保持一致。

#### 开发环境常用命令

```bash
# 首次或需要重建镜像
docker compose -f docker-compose.dev.yml up -d --build

# 查看日志
docker compose -f docker-compose.dev.yml logs -f --tail=200

# 停止并清理容器（保留卷数据）
docker compose -f docker-compose.dev.yml down

# 如需连同命名卷一并清理（会删除数据库与依赖缓存等持久化数据）
docker compose -f docker-compose.dev.yml down -v
```

#### 访问方式（dev）

- 前端（dev server）：`http://localhost:3000`
- 统一入口（经 Nginx 代理，推荐）：`http://localhost:8080`
- 后端 API：`http://localhost:9000/api/...`（或走 `8080` 的 `/api/` 代理）
- MySQL：`localhost:3307`（root/root）

### 3. 生产环境编排详解（docker-compose.prod.yml）

- **镜像来源**：
  - `backend`：`crpi-.../summer-backend:latest`
  - `mysql`：`crpi-.../summer-mysql:latest`
  - `nginx`：`crpi-.../summer-nginx:latest`
  - 这些镜像应由 CI/CD 或手动构建并推送至阿里云 ACR。仓库中提供了用于构建的 Dockerfile：
    - 后端：`backend/Dockerfile`（多阶段构建，产出瘦身 JRE 镜像，`EXPOSE 8080`）
    - Nginx：`nginx/Dockerfile`（第一阶段构建前端，第二阶段拷贝到 `nginx:latest` 的 `/usr/share/nginx/html`，并替换 `default.conf`）
    - MySQL：`mysql/Dockerfile`（将 `./init` 拷入镜像，容器首次启动自动初始化）

- **nginx（prod）**：
  - 暴露 `80:80`，作为唯一对外入口。
  - `nginx/default.conf`：
    - `/` 直接返回前端打包后的静态文件（history 模式通过 `try_files ... /index.html` 兜底）。
    - `/api/` 代理到 `backend:8080`。

- **backend（prod）**：
  - 来自预构建镜像（内部运行 `java -jar`）。
  - 依赖 `mysql`，`restart: always` 以增强稳定性。

- **mysql（prod）**：
  - 使用命名卷 `mysql-data` 持久化。
  - `healthcheck` 通过 `mysqladmin ping` 做存活探测，便于编排层判断依赖就绪。

#### 生产环境常用命令

```bash
# 启动（基于已存在的远端镜像）
docker compose -f docker-compose.prod.yml up -d

# 查看日志
docker compose -f docker-compose.prod.yml logs -f --tail=200

# 停止
docker compose -f docker-compose.prod.yml down
```

#### 本地演示生产编排的注意事项

- 如无阿里云镜像访问权限，可将 `docker-compose.prod.yml` 中的 `image:` 替换为本地构建的镜像标签：
  1. 在项目根目录执行：
     ```bash
     # 构建 backend（标签示例：summer-backend:local）
     docker build -t summer-backend:local ./backend

     # 构建 mysql（包含 init 脚本）
     docker build -t summer-mysql:local ./mysql

     # 构建 nginx（会先构建前端并拷贝进 nginx 镜像）
     docker build -t summer-nginx:local ./
     # 或进入 nginx 目录： docker build -t summer-nginx:local ./nginx
     ```
  2. 修改 compose：
     ```yaml
     image: summer-backend:local
     image: summer-mysql:local
     image: summer-nginx:local
     ```

### 4. 关键差异速览（dev vs prod）

- **前端承载方式**：
  - dev：独立 `frontend` 容器跑 dev server（3000），Nginx 仅作反代。
  - prod：不再运行 dev server。前端在构建阶段产出静态文件，由 Nginx 直接服务。

- **后端启动方式**：
  - dev：`mvn spring-boot:run`，支持热重载与快速迭代。
  - prod：`java -jar` 运行打包完成的 JAR（更快启动、更少依赖、更稳定）。

- **数据库**：
  - dev：5.7 官方镜像 + bind mount 注入脚本；端口映射为 `3307:3306`。
  - prod：自有镜像（含 init），命名卷持久化 + `healthcheck`。

- **端口与入口**：
  - dev：前端 3000，Nginx 8080，后端 9000，MySQL 3307；推荐通过 `http://localhost:8080` 统一访问。
  - prod：统一 `80` 端口，由 Nginx 承载静态与反代 API。

### 5. 相关 Dockerfile 速读

- `backend/Dockerfile`（生产）：
  - 多阶段：`maven:3-eclipse-temurin-17` 构建 → `openjdk:17-slim` 运行。
  - 复制 `target/backend-0.0.1-SNAPSHOT.jar` 为 `app.jar`，`EXPOSE 8080`。
  - 设置 `JAVA_OPTS` 以控制内存与 GC。

- `backend/Dockerfile.dev`（开发）：
  - 预先 `dependency:go-offline` 缓存依赖，容器内 `mvn spring-boot:run`。
  - `ENV SPRING_PROFILES_ACTIVE=dev`（如需按 profile 切换，可在应用中使用）。

- `nginx/Dockerfile`（生产）：
  - 第一阶段构建前端（`npm ci && npm run build`）。
  - 第二阶段使用 `nginx:latest` 承载静态资源，并替换 `default.conf`。

- `frontend/Dockerfile.dev`（开发）：
  - 设置 `CHOKIDAR_USEPOLLING=true` 以提升容器中文件变更感知（Windows/Mac 常用）。
  - `npm install` 后运行 `npm run serve -- --host 0.0.0.0 --port 3000`。

### 6. 常见问题与排错

- **端口冲突**：启动失败时检查宿主机 `3000/8080/9000/3307/80` 是否被占用，更换左侧宿主端口即可。
- **依赖下载慢**：
  - Node 已切换 `npmmirror`，Maven 依赖通过 `go-offline` 预拉取；首次仍可能耗时，请耐心等待。
- **文件热更新不生效（Windows 或网盘目录）**：
  - 已开启 `CHOKIDAR_USEPOLLING`；若仍无效，确认宿主机与容器时钟同步、卷挂载路径正确、IDE 未阻断文件变更事件。
- **MySQL 初始化重复执行**：
  - `docker-entrypoint-initdb.d` 仅在数据目录为空时执行。若需要重来，请 `down -v` 清理卷后再 `up`。

### 7. 快速清单（你应该记住的）

- 开发启动：`docker compose -f docker-compose.dev.yml up -d --build`
- 统一访问入口（dev）：`http://localhost:8080`
- 生产启动：`docker compose -f docker-compose.prod.yml up -d`
- 生产镜像来源：阿里云 ACR（或改成本地构建标签）
- 服务名即容器内主机名：`mysql`、`backend`、`frontend-dev` 等可在同一网络下直接互访

—— 完 ——

