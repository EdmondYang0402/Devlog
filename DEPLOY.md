# DevLog Docker Compose 部署

## 服务器要求

- Ubuntu 24.04
- Docker Engine 与 Docker Compose v2
- 建议至少 2 核 CPU、4 GB 内存
- 已拉取或可拉取 `nginx:alpine`、`mysql:8.4`、`redis:7-alpine`、Node、Maven 和 Java 21 镜像

## 首次部署

在项目根目录执行：

```bash
cp .env.example .env
nano .env
```

至少设置以下值：

- `MYSQL_ROOT_PASSWORD`：MySQL root 强密码
- `MYSQL_PASSWORD`：应用数据库用户强密码
- `JWT_SECRET`：至少 32 字节的随机密钥
- `REDIS_PASSWORD`：建议设置；留空表示 Redis 不启用密码
- `APP_UPLOAD_DIR`、`APP_UPLOAD_PUBLIC_URL_PREFIX` 通常保持默认值；图片保存在 Docker `upload_data` 卷

可使用以下命令生成 JWT 密钥：

```bash
openssl rand -base64 48
```

启动全部服务：

```bash
docker compose up -d --build
```

查看状态和日志：

```bash
docker compose ps
docker compose logs -f
docker compose logs -f backend
```

默认访问地址：

```text
http://服务器公网IP:8088
```

如修改了 `.env` 中的 `FRONTEND_PORT`，请使用对应端口。云服务器安全组和 Ubuntu 防火墙只需开放该前端端口；不要向公网开放 `3306`、`6379` 或 `8080`。

## 停止与更新

停止容器但保留 MySQL、Redis 数据：

```bash
docker compose down
```

更新代码并重新构建：

```bash
git pull
docker compose up -d --build
```

不要执行 `docker compose down -v`，除非明确要删除数据库和 Redis 持久化卷。

## 数据库初始化

`database/init/00-core-schema.sql` 与 `database/` 下的模块 SQL 会按 Compose 中指定的顺序挂载到 MySQL 的 `/docker-entrypoint-initdb.d/`。它们只在 `mysql_data` 数据卷首次创建时执行，不会覆盖已有数据。

`database/article_time_columns_migration.sql` 是旧数据库列名迁移脚本，不参与全新数据库初始化；仅在旧库确实存在 `createTime`、`updateTime` 列时手工执行。

## MySQL 备份与恢复

备份到当前目录：

```bash
docker compose exec -T mysql sh -c 'exec mysqldump -uroot -p"$MYSQL_ROOT_PASSWORD" --single-transaction --routines --triggers "$MYSQL_DATABASE"' > devlog-backup.sql
```

恢复前请确认目标数据库，恢复会修改数据库内容：

```bash
docker compose exec -T mysql sh -c 'exec mysql -uroot -p"$MYSQL_ROOT_PASSWORD" "$MYSQL_DATABASE"' < devlog-backup.sql
```

Redis 使用 AOF 并保存在 `redis_data` 卷。可先触发一次持久化：

```bash
docker compose exec redis sh -c 'if [ -n "$REDIS_PASSWORD" ]; then redis-cli -a "$REDIS_PASSWORD" BGREWRITEAOF; else redis-cli BGREWRITEAOF; fi'
```

## 已有数据出现中文乱码

新建数据卷已固定使用 `utf8mb4_unicode_ci`。如果旧数据卷的 API 返回中已经出现 `æ­¤...` 一类乱码，单纯重建容器不会改写已有数据。先按上文命令备份，再执行一次：

```bash
docker compose exec -T mysql sh -c \
  'exec mysql --default-character-set=utf8mb4 -uroot -p"$MYSQL_ROOT_PASSWORD" devlog' \
  < database/repair_utf8_mojibake.sql
```

该脚本只转换符合已确认 mojibake 字节特征的文本，并将现有 `devlog` 表统一为 `utf8mb4_unicode_ci`。执行后重新检查 `/api/site/profile` 和文章内容；不要重复执行。

## 请求链路

浏览器请求 `/api/user/login`，Nginx 的 `/api/` 代理会去掉 `/api` 前缀并向 `backend:8080/user/login` 转发。Vue Router 的非静态资源路径会回退到 `index.html`。

图片和头像写入后端 `/app/uploads`，并由 Docker `upload_data` 卷持久化。浏览器通过 `/uploads/...` 访问，Nginx 将该路径代理到 Spring 静态资源映射。

备份上传文件：

```bash
docker run --rm \
  -v devlog_upload_data:/data:ro \
  -v "$PWD":/backup \
  alpine tar czf /backup/devlog-uploads.tar.gz -C /data .
```
