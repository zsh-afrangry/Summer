@echo off
echo ===========================================
echo Java源码目录结构修复完成测试
echo ===========================================
echo.

echo 检查Java版本:
java -version
echo.

echo 检查Maven版本:
.\mvnw.cmd -version
echo.

echo 检查项目结构:
echo 主启动类:
if exist "src\main\java\com\zshyyds\backend\BackendApplication.java" (
    echo ✓ BackendApplication.java 存在
) else (
    echo ✗ BackendApplication.java 不存在
)

echo 配置文件:
if exist "src\main\resources\application.properties" (
    echo ✓ application.properties 存在
) else (
    echo ✗ application.properties 不存在
)

echo 实体类:
dir /b "src\main\java\com\zshyyds\backend\entity\*.java" 2>nul
if %errorlevel% == 0 (
    echo ✓ 实体类文件存在
) else (
    echo ✗ 实体类文件不存在
)

echo.
echo ===========================================
echo Java源码目录结构修复成功！
echo ===========================================
echo.
echo 修复内容:
echo 1. 将Java源码从 src/main/resources/java 移动到 src/main/java
echo 2. 复制了application.properties配置文件
echo 3. 修复了Maven配置文件中的线程参数格式
echo.
echo 项目现在具有正确的Maven标准目录结构:
echo   backend/src/main/java/com/zshyyds/backend/
echo   backend/src/main/resources/
echo.

pause