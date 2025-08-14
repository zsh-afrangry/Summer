FROM mysql:5.7

# 将初始化 SQL 脚本打包进镜像
COPY ./init /docker-entrypoint-initdb.d

# 其余环境变量（如 root 密码、库名）保持在 docker compose 中配置

