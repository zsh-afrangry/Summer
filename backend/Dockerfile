# 第一阶段：使用Maven构建应用
FROM maven:3-eclipse-temurin-17 as build-stage

# 设置工作目录
WORKDIR /app

# 复制pom.xml和源代码
COPY pom.xml ./
COPY src ./src

# 编译应用，跳过测试以加快构建速度
RUN mvn clean package -DskipTests

# 第二阶段：运行应用
FROM openjdk:17-slim

# 设置工作目录
WORKDIR /app

# 从构建阶段复制编译好的JAR文件
COPY --from=build-stage /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# 设置 JVM 内存参数和优化参数
ENV JAVA_OPTS="-Xmx384m -Xms128m -XX:MaxMetaspaceSize=128m -XX:+UseG1GC -XX:+UseContainerSupport"

# 生产环境变量 - 关闭详细日志
ENV SHOW_SQL=false
ENV FORMAT_SQL=false
ENV LOG_LEVEL=INFO
ENV SQL_LOG_LEVEL=WARN

# 暴露端口
EXPOSE 8080

# 启动应用
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]