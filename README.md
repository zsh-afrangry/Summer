# Vue3 + Spring Boot 全栈项目 - Docker部署指南

## 🚀 项目概述

这是一个基于Vue3前端和Spring Boot后端的全栈项目，支持Docker容器化部署。项目采用前后端分离架构，使用Docker Compose进行服务编排。

## 🏗️ 架构说明

- **Frontend**: Vue3 + Element Plus
- **Backend**: Spring Boot 3.x + JPA + MySQL
- **Database**: MySQL 5.7
- **Web Server**: Nginx (反向代理)
- **容器化**: Docker + Docker Compose

---

## 💻 Windows开发环境操作指南

### 📋 系统要求

- **操作系统**: Windows 10/11
- **Docker Desktop**: 最新版本
- **Java JDK**: 17+
- **Node.js**: 18+ (可选，Docker会自动处理)
- **Git**: 用于代码管理

### 🔧 环境准备

1. **安装Docker Desktop**
   ```cmd
   # 下载并安装Docker Desktop for Windows
   # 确保Docker Desktop正在运行
   docker --version
   ```

2. **登录Docker Hub**
   ```cmd
   docker login
   # 输入你的Docker Hub用户名和密码
   ```

### 🚀 本地开发部署

#### 方法一：使用部署脚本（推荐）
```cmd
# 运行自动化部署脚本
deploy-all.bat

# 选择部署方式：
# [1] 完整部署 - 首次使用
# [2] 快速启动 - 日常开发
```

#### 方法二：手动命令部署
```cmd
# 1. 构建并启动所有服务
docker compose up --build

# 2. 后台运行
docker compose up -d

# 3. 查看服务状态
docker compose ps

# 4. 查看日志
docker compose logs -f
```

### 📤 推送镜像到Docker Hub

```cmd
# 1. 构建镜像（会自动打标签）
docker compose build

# 2. 推送前端镜像
docker push afrangry/summer-frontend:latest

# 3. 推送后端镜像  
docker push afrangry/summer-backend:latest

# 4. 验证推送成功
docker images | grep afrangry
```

### 📝 代码提交

```cmd
# 提交代码到Git仓库
git add .
git commit -m "更新Docker配置和镜像"
git push origin main
```

---

## 🐧 Ubuntu服务器部署指南

### 📋 系统要求

- **操作系统**: Ubuntu 18.04+ / Debian 10+
- **Docker**: 最新版本
- **Docker Compose**: v2.0+
- **Git**: 用于拉取代码

### 🔧 环境准备

1. **安装Docker**
   ```bash
   # 更新系统包
   sudo apt update && sudo apt upgrade -y
   
   # 安装Docker
   curl -fsSL https://get.docker.com -o get-docker.sh
   sudo sh get-docker.sh
   
   # 将当前用户添加到docker组
   sudo usermod -aG docker $USER
   
   # 重新登录或执行以下命令
   newgrp docker
   
   # 验证安装
   docker --version
   docker compose version
   ```

2. **克隆项目代码**
   ```bash
   # 克隆项目到服务器
   git clone https://github.com/yourusername/summer.git
   cd summer
   ```

### 🚀 生产环境部署

#### 首次部署
```bash
# 1. 拉取最新代码
git pull origin main

# 2. 拉取Docker镜像
docker pull afrangry/summer-frontend:latest
docker pull afrangry/summer-backend:latest

# 3. 创建必要的目录
mkdir -p mysql/init

# 4. 启动服务（使用生产配置）
docker compose -f docker-compose.prod.yml up -d

# 5. 查看服务状态
docker compose -f docker-compose.prod.yml ps
```

#### 日常更新流程
```bash
# 1. 拉取最新代码
git pull origin main

# 2. 拉取最新镜像
docker pull afrangry/summer-frontend:latest
docker pull afrangry/summer-backend:latest

# 3. 重启服务
docker compose -f docker-compose.prod.yml up -d --force-recreate

# 4. 清理旧镜像（可选）
docker image prune -f
```

### 🔍 服务管理命令

```bash
# 查看服务状态
docker compose -f docker-compose.prod.yml ps

# 查看服务日志
docker compose -f docker-compose.prod.yml logs -f

# 查看特定服务日志
docker compose -f docker-compose.prod.yml logs -f backend
docker compose -f docker-compose.prod.yml logs -f frontend

# 重启特定服务
docker compose -f docker-compose.prod.yml restart backend

# 停止所有服务
docker compose -f docker-compose.prod.yml down

# 停止并删除数据卷（谨慎使用）
docker compose -f docker-compose.prod.yml down -v
```

---

## 🌐 服务访问地址

### Windows开发环境
- **前端应用**: http://localhost:80
- **后端API**: http://localhost:8081
- **数据库**: localhost:3307

### Ubuntu生产环境  
- **前端应用**: http://your-server-ip:80
- **后端API**: http://your-server-ip:8081
- **数据库**: your-server-ip:3307

### 数据库连接信息
- **用户名**: root
- **密码**: root  
- **数据库名**: my_blog

## 🛠️ 故障排除

### Windows环境常见问题

#### 1. Docker相关问题
```cmd
# 问题：Docker未安装或未启动
# 解决：安装Docker Desktop并确保其正常运行
docker --version

# 问题：Docker Desktop启动失败
# 解决：检查Hyper-V是否启用，重启Docker Desktop
```

#### 2. 端口占用问题
```cmd
# 检查端口占用情况
netstat -ano | findstr :80
netstat -ano | findstr :8081

# 强制停止占用端口的进程
taskkill /PID <进程ID> /F
```

#### 3. 镜像推送问题
```cmd
# 问题：推送失败，认证错误
# 解决：重新登录Docker Hub
docker logout
docker login

# 问题：镜像标签错误
# 解决：检查镜像名称格式
docker images | grep afrangry
```

### Ubuntu服务器常见问题

#### 1. Docker安装问题
```bash
# 问题：权限不足
# 解决：添加用户到docker组
sudo usermod -aG docker $USER
newgrp docker

   # 问题：Docker Compose版本过低
   # 解决：Docker Compose现在集成在Docker中，确保Docker版本足够新
   # 或者单独安装Compose插件
   sudo apt install docker-compose-plugin
```

#### 2. 网络连接问题
```bash
# 检查防火墙状态
sudo ufw status

# 开放必要端口
sudo ufw allow 80
sudo ufw allow 8081

# 检查Docker网络
docker network ls
```

#### 3. 镜像拉取问题
```bash
# 问题：镜像拉取失败
# 解决：检查网络连接，使用镜像加速器
# 配置Docker镜像加速器（以阿里云为例）
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://your-accelerator-address.com"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker
```

### 通用调试命令

```bash
# 查看系统资源使用情况
docker stats

# 查看Docker系统信息
docker system info

# 清理Docker资源
docker system prune -a

# 查看容器详细信息
docker inspect <container_name>

# 进入容器调试
docker exec -it <container_name> /bin/bash
```

## 📚 项目结构

```
summer/
├── backend/                    # Spring Boot后端
│   ├── src/main/java/         # Java源码
│   ├── src/main/resources/    # 配置文件
│   ├── Dockerfile             # 后端镜像构建文件
│   └── .dockerignore          # Docker忽略文件
├── frontend/                  # Vue3前端
│   ├── src/                   # Vue源码
│   ├── Dockerfile             # 前端镜像构建文件
│   └── .dockerignore          # Docker忽略文件
├── nginx/                     # Nginx配置
│   └── default.conf           # Nginx配置文件
├── mysql/                     # MySQL配置
│   └── init/                  # 数据库初始化脚本
├── docker-compose.yml         # 开发环境Docker编排
├── docker-compose.prod.yml    # 生产环境Docker编排
└── deploy-all.bat            # Windows部署脚本
```

## 🔒 安全注意事项

### 生产环境建议

1. **修改默认密码**
   - 修改MySQL root密码
   - 修改应用管理员密码

2. **网络安全**
   ```bash
   # 配置防火墙，只开放必要端口
   sudo ufw enable
   sudo ufw allow ssh
   sudo ufw allow 80
   sudo ufw allow 443
   ```

3. **SSL证书**
   ```bash
   # 使用Let's Encrypt获取免费SSL证书
   sudo apt install certbot python3-certbot-nginx
   sudo certbot --nginx -d your-domain.com
   ```

## 📊 性能监控

### 资源监控命令
```bash
# 查看容器资源使用
docker stats

# 查看系统负载
htop

# 查看磁盘使用
df -h

# 查看内存使用
free -h
```

## 🎯 默认账户信息

- **数据库**:
  - 用户名: root
  - 密码: root
  - 数据库: my_blog

- **应用管理员** (如果有):
  - 用户名: admin
  - 密码: 123456

## 📞 技术支持

遇到问题时的排查顺序：

1. **检查服务状态**: `docker compose ps`
2. **查看日志**: `docker compose logs -f`
3. **检查网络**: `docker network ls`
4. **验证镜像**: `docker images`
5. **检查资源**: `docker stats`

---

## 🚀 快速命令参考

### Windows开发
```cmd
# 完整部署
deploy-all.bat

# 手动部署
docker compose up -d --build

# 推送镜像
docker compose build && docker push afrangry/summer-frontend:latest && docker push afrangry/summer-backend:latest
```

### Ubuntu生产
```bash
# 首次部署
git clone <repo> && cd summer
docker pull afrangry/summer-frontend:latest && docker pull afrangry/summer-backend:latest
docker compose -f docker-compose.prod.yml up -d

# 更新部署
git pull && docker pull afrangry/summer-frontend:latest && docker pull afrangry/summer-backend:latest
docker compose -f docker-compose.prod.yml up -d --force-recreate
```

---

**🎉 部署完成，祝您使用愉快！**