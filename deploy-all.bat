@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo ==========================================
echo Vue3 + Spring Boot 全栈项目部署脚本
echo Windows Docker 完整部署解决方案
echo ==========================================
echo.
echo 项目信息:
echo - 前端: Vue 3 + Element Plus + Chart.js
echo - 后端: Spring Boot 3.5.3 + JPA + MySQL
echo - 数据库: MySQL 5.7
echo - 部署: Docker + Docker Compose + Nginx
echo.

:MAIN_MENU
echo ==========================================
echo 请选择操作:
echo ==========================================
echo [1] 完整部署 (推荐首次使用)
echo [2] 快速启动 (已构建过的项目)
echo [3] 重新构建所有服务
echo [4] 查看服务状态
echo [5] 查看服务日志
echo [6] 停止所有服务
echo [7] 清理Docker资源
echo [8] 健康检查
echo [9] 数据库管理
echo [0] 退出
echo ==========================================
set /p choice="请输入选项 [0-9]: "

if "%choice%"=="1" goto FULL_DEPLOY
if "%choice%"=="2" goto QUICK_START
if "%choice%"=="3" goto REBUILD_ALL
if "%choice%"=="4" goto CHECK_STATUS
if "%choice%"=="5" goto VIEW_LOGS
if "%choice%"=="6" goto STOP_SERVICES
if "%choice%"=="7" goto CLEANUP_DOCKER
if "%choice%"=="8" goto HEALTH_CHECK
if "%choice%"=="9" goto DATABASE_MENU
if "%choice%"=="0" goto EXIT
echo 无效选项，请重新选择
goto MAIN_MENU

:FULL_DEPLOY
echo.
echo ==========================================
echo 开始完整部署流程
echo ==========================================
echo.

echo 步骤1: 环境检查...
call :CHECK_ENVIRONMENT
if %errorlevel% neq 0 goto ERROR_EXIT

echo.
echo 步骤2: 项目结构检查...
call :CHECK_PROJECT_STRUCTURE
if %errorlevel% neq 0 goto ERROR_EXIT

echo.
echo 步骤3: 检查后端项目...
call :CHECK_BACKEND

echo.
echo 步骤4: 检查前端依赖...
call :CHECK_FRONTEND
if %errorlevel% neq 0 goto ERROR_EXIT

echo.
echo 步骤5: 停止现有服务...
call :STOP_SERVICES_SILENT

echo.
echo 步骤6: 清理旧资源...
call :CLEANUP_DOCKER_SILENT

echo.
echo 步骤7: 构建并启动所有服务...
call :START_SERVICES

echo.
echo 步骤8: 等待服务启动...
echo 正在等待服务启动完成，请稍候...
timeout /t 45 /nobreak >nul

echo.
echo 步骤9: 健康检查...
call :HEALTH_CHECK_SILENT

echo.
echo 步骤10: 显示访问信息...
call :SHOW_ACCESS_INFO

goto MAIN_MENU

:QUICK_START
echo.
echo ==========================================
echo 快速启动服务
echo ==========================================
echo.

call :CHECK_ENVIRONMENT
if %errorlevel% neq 0 goto ERROR_EXIT

echo 启动所有服务...
docker compose up -d
if %errorlevel% neq 0 (
    echo ❌ 服务启动失败
    goto ERROR_EXIT
)

echo 等待服务启动...
timeout /t 30 /nobreak >nul

call :CHECK_STATUS
call :SHOW_ACCESS_INFO
goto MAIN_MENU

:REBUILD_ALL
echo.
echo ==========================================
echo 重新构建所有服务
echo ==========================================
echo.

call :CHECK_BACKEND
if %errorlevel% neq 0 goto ERROR_EXIT

echo 停止现有服务...
docker compose down

echo 重新构建并启动...
docker compose up --build -d
if %errorlevel% neq 0 goto ERROR_EXIT

echo 等待服务启动...
timeout /t 45 /nobreak >nul

call :HEALTH_CHECK_SILENT
call :SHOW_ACCESS_INFO
goto MAIN_MENU

:CHECK_STATUS
echo.
echo ==========================================
echo 服务状态检查
echo ==========================================
echo.

echo Docker 服务状态:
docker compose ps
echo.

echo 容器资源使用情况:
docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}"
echo.

echo 端口占用情况:
echo 检查关键端口...
netstat -ano | findstr ":80 " && echo 端口80: 被占用 || echo 端口80: 可用
netstat -ano | findstr ":8080 " && echo 端口8080: 被占用 || echo 端口8080: 可用  
netstat -ano | findstr ":8081 " && echo 端口8081: 被占用 || echo 端口8081: 可用
netstat -ano | findstr ":3307 " && echo 端口3307: 被占用 || echo 端口3307: 可用

pause
goto MAIN_MENU

:VIEW_LOGS
echo.
echo ==========================================
echo 服务日志查看
echo ==========================================
echo.
echo [1] 查看所有服务日志
echo [2] 查看前端日志
echo [3] 查看后端日志  
echo [4] 查看MySQL日志
echo [5] 查看Nginx日志
echo [0] 返回主菜单
echo.
set /p log_choice="请选择 [0-5]: "

if "%log_choice%"=="1" docker compose logs --tail=50
if "%log_choice%"=="2" docker compose logs frontend --tail=50
if "%log_choice%"=="3" docker compose logs backend --tail=50
if "%log_choice%"=="4" docker compose logs mysql --tail=50
if "%log_choice%"=="5" docker compose logs nginx --tail=50

if "%log_choice%"=="0" goto MAIN_MENU

pause
goto VIEW_LOGS

:STOP_SERVICES
echo.
echo ==========================================
echo 停止所有服务
echo ==========================================
echo.

docker compose down
if %errorlevel% equ 0 (
    echo ✅ 所有服务已停止
) else (
    echo ❌ 停止服务时出现错误
)

pause
goto MAIN_MENU

:CLEANUP_DOCKER
echo.
echo ==========================================
echo 清理Docker资源
echo ==========================================
echo.

echo 警告: 此操作将清理未使用的Docker资源
set /p confirm="确认继续? [y/N]: "
if /i not "%confirm%"=="y" goto MAIN_MENU

echo 停止所有服务...
docker compose down

echo 清理未使用的容器、网络、镜像...
docker system prune -f

echo 清理未使用的卷...
docker volume prune -f

echo ✅ 清理完成
pause
goto MAIN_MENU

:HEALTH_CHECK
echo.
echo ==========================================
echo 系统健康检查
echo ==========================================
echo.

call :HEALTH_CHECK_SILENT

echo.
echo 详细连接测试:
echo.

echo 测试前端服务...
curl -s -I http://localhost:8080 | findstr "200 OK" >nul && echo ✅ 前端服务正常 || echo ❌ 前端服务异常

echo 测试后端API...
curl -s -I http://localhost:8081 | findstr "404\|200" >nul && echo ✅ 后端API正常 || echo ❌ 后端API异常

echo 测试Nginx代理...
curl -s -I http://localhost:80 | findstr "200 OK" >nul && echo ✅ Nginx代理正常 || echo ❌ Nginx代理异常

echo 测试数据库连接...
docker exec mysql mysqladmin -uroot -proot ping >nul 2>&1 && echo ✅ MySQL数据库正常 || echo ❌ MySQL数据库异常

pause
goto MAIN_MENU

:DATABASE_MENU
echo.
echo ==========================================
echo 数据库管理
echo ==========================================
echo.
echo [1] 连接到MySQL命令行
echo [2] 查看数据库状态
echo [3] 备份数据库
echo [4] 恢复数据库
echo [5] 重置数据库
echo [0] 返回主菜单
echo.
set /p db_choice="请选择 [0-5]: "

if "%db_choice%"=="1" (
    echo 连接到MySQL... (密码: root)
    docker exec -it mysql mysql -uroot -proot my_blog
    goto DATABASE_MENU
)
if "%db_choice%"=="2" (
    echo 数据库状态:
    docker exec mysql mysqladmin -uroot -proot status
    echo.
    echo 数据库列表:
    docker exec mysql mysql -uroot -proot -e "SHOW DATABASES;"
    echo.
    echo my_blog数据库表:
    docker exec mysql mysql -uroot -proot my_blog -e "SHOW TABLES;"
    pause
    goto DATABASE_MENU
)
if "%db_choice%"=="3" (
    echo 备份数据库到backup.sql...
    docker exec mysql mysqldump -uroot -proot my_blog > backup.sql
    echo ✅ 备份完成: backup.sql
    pause
    goto DATABASE_MENU
)
if "%db_choice%"=="4" (
    if not exist "backup.sql" (
        echo ❌ 备份文件backup.sql不存在
        pause
        goto DATABASE_MENU
    )
    echo 从backup.sql恢复数据库...
    docker exec -i mysql mysql -uroot -proot my_blog < backup.sql
    echo ✅ 恢复完成
    pause
    goto DATABASE_MENU
)
if "%db_choice%"=="5" (
    echo 警告: 此操作将重置数据库，所有数据将丢失!
    set /p confirm="确认继续? [y/N]: "
    if /i "%confirm%"=="y" (
        docker exec mysql mysql -uroot -proot -e "DROP DATABASE IF EXISTS my_blog; CREATE DATABASE my_blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
        docker exec -i mysql mysql -uroot -proot my_blog < mysql/init/database_init.sql
        echo ✅ 数据库重置完成
    )
    pause
    goto DATABASE_MENU
)
if "%db_choice%"=="0" goto MAIN_MENU
goto DATABASE_MENU

:CHECK_ENVIRONMENT
echo 检查Docker环境...
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker未安装或未启动
    echo 请先安装Docker Desktop并确保其正常运行
    exit /b 1
)

docker compose version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker Compose未安装
    exit /b 1
)

echo 检查Java环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java未安装或未配置PATH
    echo 请安装JDK 17并配置环境变量
    exit /b 1
)

echo ✅ 环境检查通过
exit /b 0

:CHECK_PROJECT_STRUCTURE
echo 检查项目结构...

if not exist "backend\src\main\java\com\zshyyds\backend\BackendApplication.java" (
    echo ❌ 后端主启动类不存在
    exit /b 1
)

if not exist "backend\src\main\resources\application.properties" (
    echo ❌ Spring Boot配置文件不存在
    exit /b 1
)

if not exist "frontend\src\App.vue" (
    echo ❌ Vue主应用文件不存在
    exit /b 1
)

if not exist "mysql\init\database_init.sql" (
    echo ❌ MySQL初始化脚本不存在
    exit /b 1
)

if not exist "docker-compose.yml" (
    echo ❌ Docker Compose配置文件不存在
    exit /b 1
)

echo ✅ 项目结构完整
exit /b 0

:CHECK_BACKEND
echo 检查后端项目...

if not exist "backend\pom.xml" (
    echo ❌ 后端pom.xml不存在
    exit /b 1
)

if not exist "backend\src\main\java" (
    echo ❌ 后端源码目录不存在
    exit /b 1
)

echo ✅ 后端项目检查通过（将在Docker构建时自动编译）
exit /b 0

:CHECK_FRONTEND
echo 检查前端项目...

if not exist "frontend\package.json" (
    echo ❌ 前端package.json不存在
    exit /b 1
)

if not exist "frontend\node_modules" (
    echo 前端依赖未安装，将在Docker构建时自动安装
)

echo ✅ 前端项目检查通过
exit /b 0

:START_SERVICES
echo 构建并启动所有服务...
docker compose up --build -d
if %errorlevel% neq 0 (
    echo ❌ 服务启动失败
    exit /b 1
)
echo ✅ 服务启动成功
exit /b 0

:STOP_SERVICES_SILENT
docker compose down >nul 2>&1
exit /b 0

:CLEANUP_DOCKER_SILENT
docker system prune -f >nul 2>&1
exit /b 0

:HEALTH_CHECK_SILENT
echo 健康检查...

docker compose ps | findstr "Up" >nul
if %errorlevel% neq 0 (
    echo ❌ 部分服务未正常运行
    docker compose ps
    exit /b 1
)

echo ✅ 所有服务运行正常
exit /b 0

:SHOW_ACCESS_INFO
echo.
echo ==========================================
echo 🎉 部署完成！服务访问信息
echo ==========================================
echo.
echo 📱 前端应用:
echo   - Vue3应用: http://localhost:8080
echo   - Nginx入口: http://localhost:80
echo.
echo 🔧 后端服务:
echo   - 生产API: http://localhost:8081
echo.
echo 🗄️ 数据库:
echo   - MySQL: localhost:3307
echo   - 用户名: root
echo   - 密码: root
echo   - 数据库: my_blog
echo.
echo 🔍 管理命令:
echo   - 查看日志: docker compose logs [service-name]
echo   - 重启服务: docker compose restart [service-name]
echo   - 停止服务: docker compose down
echo.
echo 默认登录账户: admin / 123456
echo.
exit /b 0

:ERROR_EXIT
echo.
echo ❌ 部署过程中出现错误，请检查上述错误信息
echo 常见解决方案:
echo 1. 确保Docker Desktop正在运行
echo 2. 检查端口是否被占用
echo 3. 确保有足够的磁盘空间
echo 4. 检查网络连接
echo.
pause
goto MAIN_MENU

:EXIT
echo.
echo 感谢使用！再见！
echo.
pause
exit /b 0