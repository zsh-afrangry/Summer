# 第一阶段：构建Vue.js应用
FROM node:16-alpine as build-stage

# 设置工作目录
WORKDIR /app

# 复制package.json和package-lock.json
COPY package*.json ./

# 设置 Node.js 内存限制并安装依赖
ENV NODE_OPTIONS="--max-old-space-size=1024"
RUN npm install

# 复制源代码
COPY . .

# 构建生产版本 (限制内存使用)
RUN npx --max-old-space-size=1024 vue-cli-service build

# 第二阶段：使用nginx提供静态文件服务
FROM nginx:alpine

# 复制构建好的文件到nginx目录
COPY --from=build-stage /app/dist /usr/share/nginx/html

# 暴露80端口
EXPOSE 80

# 启动nginx
CMD ["nginx", "-g", "daemon off;"]