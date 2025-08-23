#!/bin/bash
# wait-for-mysql.sh - 等待MySQL数据库准备就绪

set -e

host="$1"
port="$2"
shift 2
cmd="$@"

echo "等待 MySQL 数据库在 $host:$port 准备就绪..."

until mysqladmin ping -h"$host" -P"$port" -uroot -proot --silent; do
  echo "MySQL 还未准备就绪，等待 2 秒..."
  sleep 2
done

echo "MySQL 已准备就绪，启动应用..."
exec $cmd