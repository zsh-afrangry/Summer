@echo off
chcp 65001 >nul 2>&1
setlocal enabledelayedexpansion

echo ==========================================
echo Vue3 + Spring Boot Docker Deploy Script
echo ==========================================
echo.

:MAIN_MENU
echo ==========================================
echo Please select an option:
echo ==========================================
echo [1] Full Deploy (Recommended for first time)
echo [2] Quick Start (For built projects)
echo [3] Rebuild All Services
echo [4] Check Service Status
echo [5] View Service Logs
echo [6] Stop All Services
echo [7] Clean Docker Resources
echo [8] Health Check
echo [9] Database Management
echo [0] Exit
echo ==========================================
set /p choice="Please enter option [0-9]: "

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
echo Invalid option, please try again
goto MAIN_MENU

:FULL_DEPLOY
echo.
echo ==========================================
echo Starting Full Deployment
echo ==========================================
echo.

echo Step 1: Environment Check...
call :CHECK_ENVIRONMENT
if %errorlevel% neq 0 goto ERROR_EXIT

echo.
echo Step 2: Stop Existing Services...
docker-compose down >nul 2>&1

echo.
echo Step 3: Clean Old Resources...
docker system prune -f >nul 2>&1

echo.
echo Step 4: Build and Start Services...
docker-compose up --build -d
if %errorlevel% neq 0 goto ERROR_EXIT

echo.
echo Step 5: Wait for Services...
echo Waiting for services to start, please wait...
timeout /t 45 /nobreak >nul

echo.
echo Step 6: Health Check...
call :HEALTH_CHECK_SILENT

echo.
echo Step 7: Display Access Info...
call :SHOW_ACCESS_INFO

goto MAIN_MENU

:QUICK_START
echo.
echo ==========================================
echo Quick Start Services
echo ==========================================
echo.

docker-compose up -d
if %errorlevel% neq 0 goto ERROR_EXIT

echo Waiting for services...
timeout /t 30 /nobreak >nul

call :SHOW_ACCESS_INFO
goto MAIN_MENU

:REBUILD_ALL
echo.
echo ==========================================
echo Rebuild All Services
echo ==========================================
echo.

docker-compose down
docker-compose up --build -d
if %errorlevel% neq 0 goto ERROR_EXIT

echo Waiting for services...
timeout /t 45 /nobreak >nul

call :SHOW_ACCESS_INFO
goto MAIN_MENU

:CHECK_STATUS
echo.
echo ==========================================
echo Service Status Check
echo ==========================================
echo.

echo Docker Service Status:
docker-compose ps
echo.

echo Container Resource Usage:
docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}"
echo.

pause
goto MAIN_MENU

:VIEW_LOGS
echo.
echo ==========================================
echo Service Logs
echo ==========================================
echo.
echo [1] All Service Logs
echo [2] Frontend Logs
echo [3] Backend Logs
echo [4] MySQL Logs
echo [5] Nginx Logs
echo [0] Back to Main Menu
echo.
set /p log_choice="Please select [0-5]: "

if "%log_choice%"=="1" docker-compose logs --tail=50
if "%log_choice%"=="2" docker-compose logs frontend --tail=50
if "%log_choice%"=="3" docker-compose logs backend --tail=50
if "%log_choice%"=="4" docker-compose logs mysql --tail=50
if "%log_choice%"=="5" docker-compose logs nginx --tail=50
if "%log_choice%"=="0" goto MAIN_MENU

pause
goto VIEW_LOGS

:STOP_SERVICES
echo.
echo ==========================================
echo Stop All Services
echo ==========================================
echo.

docker-compose down
if %errorlevel% equ 0 (
    echo Services stopped successfully
) else (
    echo Error stopping services
)

pause
goto MAIN_MENU

:CLEANUP_DOCKER
echo.
echo ==========================================
echo Clean Docker Resources
echo ==========================================
echo.

echo Warning: This will clean unused Docker resources
set /p confirm="Continue? [y/N]: "
if /i not "%confirm%"=="y" goto MAIN_MENU

echo Stopping services...
docker-compose down

echo Cleaning unused resources...
docker system prune -f
docker volume prune -f

echo Cleanup completed
pause
goto MAIN_MENU

:HEALTH_CHECK
echo.
echo ==========================================
echo System Health Check
echo ==========================================
echo.

call :HEALTH_CHECK_SILENT

echo.
echo Detailed Connection Test:
echo.

echo Testing frontend service...
curl -s -I http://localhost:80 | findstr "200\|404" >nul && echo Frontend: OK || echo Frontend: ERROR

echo Testing backend API...
curl -s -I http://localhost:8081 | findstr "200\|404" >nul && echo Backend: OK || echo Backend: ERROR

echo Testing database...
docker exec mysql mysqladmin -uroot -proot ping >nul 2>&1 && echo Database: OK || echo Database: ERROR

pause
goto MAIN_MENU

:DATABASE_MENU
echo.
echo ==========================================
echo Database Management
echo ==========================================
echo.
echo [1] Connect to MySQL
echo [2] Database Status
echo [3] Backup Database
echo [4] Restore Database
echo [0] Back to Main Menu
echo.
set /p db_choice="Please select [0-4]: "

if "%db_choice%"=="1" (
    echo Connecting to MySQL... (password: root)
    docker exec -it mysql mysql -uroot -proot my_blog
    goto DATABASE_MENU
)
if "%db_choice%"=="2" (
    echo Database Status:
    docker exec mysql mysqladmin -uroot -proot status
    echo.
    echo Database List:
    docker exec mysql mysql -uroot -proot -e "SHOW DATABASES;"
    pause
    goto DATABASE_MENU
)
if "%db_choice%"=="3" (
    echo Backing up database to backup.sql...
    docker exec mysql mysqldump -uroot -proot my_blog > backup.sql
    echo Backup completed: backup.sql
    pause
    goto DATABASE_MENU
)
if "%db_choice%"=="4" (
    if not exist "backup.sql" (
        echo Backup file backup.sql not found
        pause
        goto DATABASE_MENU
    )
    echo Restoring from backup.sql...
    docker exec -i mysql mysql -uroot -proot my_blog < backup.sql
    echo Restore completed
    pause
    goto DATABASE_MENU
)
if "%db_choice%"=="0" goto MAIN_MENU
goto DATABASE_MENU

:CHECK_ENVIRONMENT
echo Checking Docker environment...
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Docker not installed or not running
    exit /b 1
)

docker-compose version >nul 2>&1
if %errorlevel% neq 0 (
    echo Docker Compose not installed
    exit /b 1
)

echo Environment check passed
exit /b 0

:HEALTH_CHECK_SILENT
echo Health check...

docker-compose ps | findstr "Up" >nul
if %errorlevel% neq 0 (
    echo Some services are not running properly
    docker-compose ps
    exit /b 1
)

echo All services running normally
exit /b 0

:SHOW_ACCESS_INFO
echo.
echo ==========================================
echo Deployment Complete! Access Information
echo ==========================================
echo.
echo Frontend Application:
echo   - Main Entry: http://localhost:80
echo   - Vue3 App: http://localhost:8080
echo.
echo Backend Service:
echo   - Production API: http://localhost:8081
echo.
echo Database:
echo   - MySQL: localhost:3307
echo   - Username: root
echo   - Password: root
echo   - Database: my_blog
echo.
echo Management Commands:
echo   - View logs: docker-compose logs [service-name]
echo   - Restart service: docker-compose restart [service-name]
echo   - Stop services: docker-compose down
echo.
echo Default login: admin / 123456
echo.
exit /b 0

:ERROR_EXIT
echo.
echo Error occurred during deployment
echo Common solutions:
echo 1. Ensure Docker Desktop is running
echo 2. Check if ports are occupied
echo 3. Ensure sufficient disk space
echo 4. Check network connection
echo.
pause
goto MAIN_MENU

:EXIT
echo.
echo Thank you for using! Goodbye!
echo.
pause
exit /b 0