@echo off
echo "========================================="
echo "测试 Docker 开发环境启动"
echo "========================================="

echo "1. 停止所有现有容器..."
docker compose -f docker-compose.dev.yml down

echo "2. 清理后端镜像缓存..."
docker rmi summer-backend:latest 2>nul || echo "后端镜像不存在，跳过删除"

echo "3. 启动所有服务..."
docker compose -f docker-compose.dev.yml up -d

echo "4. 等待服务启动..."
timeout /t 30 /nobreak

echo "5. 查看服务状态..."
docker compose -f docker-compose.dev.yml ps

echo "6. 查看后端日志..."
echo "如果后端启动失败，查看详细日志："
echo "docker logs backend-dev"

echo "7. 测试地址："
echo "   前端: http://localhost:3000"
echo "   后端: http://localhost:9000" 
echo "   完整应用: http://localhost:8080"

pause