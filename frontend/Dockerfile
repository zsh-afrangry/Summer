# 第一阶段：构建Vue.js应用
FROM node:18-alpine as build-stage

# 设置工作目录
WORKDIR /app

# 复制package.json和package-lock.json
COPY package*.json ./

# 设置 Node.js 内存限制并配置npm
ENV NODE_OPTIONS="--max-old-space-size=2048"
RUN npm config set registry https://registry.npmmirror.com/
RUN npm install --silent

# 复制源代码
COPY . .

# 构建生产版本
RUN npm run build

# 第二阶段：创建一个包含构建文件的容器
FROM alpine:latest

# 安装基本工具
RUN apk add --no-cache bash

# 复制构建好的文件
COPY --from=build-stage /app/dist /usr/share/nginx/html

# 创建一个保持容器运行的脚本
RUN echo '#!/bin/bash' > /keep-alive.sh && \
    echo 'while true; do sleep 3600; done' >> /keep-alive.sh && \
    chmod +x /keep-alive.sh

# 启动保持运行的脚本
CMD ["/keep-alive.sh"]