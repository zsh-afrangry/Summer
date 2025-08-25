# Vue3 + Spring Boot 全栈项目 - 容器化部署指南

## 🚀 项目概述

这是一个基于Vue3前端和Spring Boot后端的全栈项目，采用Docker容器化部署。生产环境中前端和nginx合并为一个容器，通过阿里云容器镜像服务分发。

## 🏗️ 架构说明

- **Frontend**: Vue3 + Element Plus (与nginx合并部署)
- **Backend**: Spring Boot 3.x + JPA + MySQL  
- **Database**: MySQL 5.7
- **Web Server**: Nginx (静态文件服务 + API反向代理)
- **容器化**: Docker + Docker Compose
- **镜像仓库**: 阿里云容器镜像服务 (ACR)

---

## 💻 本机部署操作 (开发者)

### 📋 前置要求

- Docker Desktop 已安装并运行
- 拥有阿里云容器镜像服务访问权限

### 🔨 构建和推送镜像

```bash
# 1. 构建所有镜像
docker build -f nginx/Dockerfile -t summer-nginx:latest .
docker build -f backend/Dockerfile -t summer-backend:latest ./backend  
docker build -f mysql/Dockerfile -t summer-mysql:latest ./mysql

# 2. 打标签
docker tag summer-nginx:latest crpi-q0fu55ysz8p6bskr.cn-beijing.personal.cr.aliyuncs.com/zshlyyyds/summer-nginx:latest
docker tag summer-backend:latest crpi-q0fu55ysz8p6bskr.cn-beijing.personal.cr.aliyuncs.com/zshlyyyds/summer-backend:latest
docker tag summer-mysql:latest crpi-q0fu55ysz8p6bskr.cn-beijing.personal.cr.aliyuncs.com/zshlyyyds/summer-mysql:latest

# 3. 登录阿里云镜像仓库
docker login --username=zshlyyyds crpi-q0fu55ysz8p6bskr.cn-beijing.personal.cr.aliyuncs.com

# 4. 推送镜像
docker push crpi-q0fu55ysz8p6bskr.cn-beijing.personal.cr.aliyuncs.com/zshlyyyds/summer-nginx:latest
docker push crpi-q0fu55ysz8p6bskr.cn-beijing.personal.cr.aliyuncs.com/zshlyyyds/summer-backend:latest
docker push crpi-q0fu55ysz8p6bskr.cn-beijing.personal.cr.aliyuncs.com/zshlyyyds/summer-mysql:latest
```

### 📦 一键构建推送脚本 (Windows)

创建 `build-push.bat` 文件：

```cmd
@echo off
echo 开始构建镜像...

docker build -f nginx/Dockerfile -t summer-nginx:latest .
docker build -f backend/Dockerfile -t summer-backend:latest ./backend
docker build -f mysql/Dockerfile -t summer-mysql:latest ./mysql

echo 打标签...
docker tag summer-nginx:latest crpi-q0fu55ysz8p6bskr.cn-beijing.personal.cr.aliyuncs.com/zshlyyyds/summer-nginx:latest
docker tag summer-backend:latest crpi-q0fu55ysz8p6bskr.cn-beijing.personal.cr.aliyuncs.com/zshlyyyds/summer-backend:latest
docker tag summer-mysql:latest crpi-q0fu55ysz8p6bskr.cn-beijing.personal.cr.aliyuncs.com/zshlyyyds/summer-mysql:latest

echo 推送镜像...
docker push crpi-q0fu55ysz8p6bskr.cn-beijing.personal.cr.aliyuncs.com/zshlyyyds/summer-nginx:latest
docker push crpi-q0fu55ysz8p6bskr.cn-beijing.personal.cr.aliyuncs.com/zshlyyyds/summer-backend:latest
docker push crpi-q0fu55ysz8p6bskr.cn-beijing.personal.cr.aliyuncs.com/zshlyyyds/summer-mysql:latest

echo 构建推送完成！
pause
```

---

## 🐧 服务器部署操作 (Ubuntu)

### 📋 前置要求

- Ubuntu 18.04+ 服务器
- Docker 和 Docker Compose 已安装
- 项目代码已部署到服务器

### 🚀 一键部署脚本

在服务器项目目录下创建 `deploy.sh` 文件：

```bash
#!/bin/bash

# 一键部署脚本 - deploy.sh
set -e  # 遇到错误立即退出

echo "🚀 开始部署 Summer 项目..."

# 颜色输出
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 登录阿里云镜像仓库
echo -e "${YELLOW}📡 登录阿里云镜像仓库...${NC}"
echo "121026@ZSHLYyyds" | docker login --username=zshlyyyds --password-stdin crpi-q0fu55ysz8p6bskr.cn-beijing.personal.cr.aliyuncs.com

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ 登录成功${NC}"
else
    echo -e "${RED}❌ 登录失败${NC}"
    exit 1
fi

# 停止现有服务
echo -e "${YELLOW}🛑 停止现有服务...${NC}"
docker compose -f docker-compose.prod.yml down

# 拉取最新镜像
echo -e "${YELLOW}📥 拉取最新镜像...${NC}"
docker compose -f docker-compose.prod.yml pull

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ 镜像拉取成功${NC}"
else
    echo -e "${RED}❌ 镜像拉取失败${NC}"
    exit 1
fi

# 启动服务
echo -e "${YELLOW}🚀 启动服务...${NC}"
docker compose -f docker-compose.prod.yml up -d

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ 服务启动成功${NC}"
else
    echo -e "${RED}❌ 服务启动失败${NC}"
    exit 1
fi

# 等待几秒让服务完全启动
echo -e "${YELLOW}⏳ 等待服务完全启动...${NC}"
sleep 5

# 检查服务状态
echo -e "${YELLOW}📊 检查服务状态...${NC}"
docker compose -f docker-compose.prod.yml ps

# 检查服务健康状态
echo -e "${YELLOW}🔍 检查服务健康状态...${NC}"
if docker compose -f docker-compose.prod.yml ps | grep -q "Up"; then
    echo -e "${GREEN}✅ 所有服务运行正常${NC}"
    echo -e "${GREEN}🎉 部署完成！项目已成功启动${NC}"
    echo -e "${GREEN}🌐 访问地址: http://$(curl -s ifconfig.me):80${NC}"
else
    echo -e "${RED}❌ 部分服务可能存在问题，请检查日志${NC}"
    echo "查看日志命令: docker compose -f docker-compose.prod.yml logs"
fi

# 显示容器资源使用情况
echo -e "${YELLOW}📈 容器资源使用情况:${NC}"
docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}"

echo -e "${GREEN}✨ 部署脚本执行完成！${NC}"
```

### 🎯 使用方法

```bash
# 1. 给脚本添加执行权限
chmod +x deploy.sh

# 2. 执行一键部署
./deploy.sh
```

### 🔍 服务管理命令

```bash
# 查看服务状态
docker compose -f docker-compose.prod.yml ps

# 查看服务日志
docker compose -f docker-compose.prod.yml logs -f

# 查看特定服务日志
docker compose -f docker-compose.prod.yml logs -f nginx
docker compose -f docker-compose.prod.yml logs -f backend
docker compose -f docker-compose.prod.yml logs -f mysql

# 重启服务
docker compose -f docker-compose.prod.yml restart

# 停止服务
docker compose -f docker-compose.prod.yml down
```

---

## 🌐 访问地址

- **生产环境**: http://your-server-ip:80
- **数据库连接**: your-server-ip:3306
  - 用户名: root  
  - 密码: root
  - 数据库: my_blog

---

## 📋 部署流程总结

### 开发者流程
1. 本地修改代码
2. 运行构建脚本构建镜像
3. 推送镜像到阿里云仓库

### 服务器部署流程  
1. 执行 `./deploy.sh` 一键部署
2. 脚本自动完成：登录、拉取镜像、重启服务
3. 访问服务器IP查看效果

---

**🎉 容器化部署完成，祝您使用愉快！**