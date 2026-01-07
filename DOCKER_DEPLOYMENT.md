# Docker 部署文档

本文档介绍如何使用 Docker 和 Docker Compose 将青春旅贷项目（前后端）部署到阿里云服务器。

## 目录结构

```
qingchun_travel_loan_package/
├── backend/                    # 后端项目
│   ├── Dockerfile             # 后端Docker镜像文件
│   ├── docker-compose.yml     # Docker Compose配置文件
│   └── ...
├── qingchun_travel_loan/       # 前端项目
│   ├── client/                # 前端源码
│   └── Dockerfile             # 前端Docker镜像文件
└── docker-compose.yml          # 主Docker Compose文件（可选）
```

## 前置要求

1. **服务器环境**
   - 阿里云ECS服务器（推荐2核4G以上配置）
   - 操作系统：Ubuntu 20.04+ / CentOS 7+
   - 已安装 Docker（版本20.10+）
   - 已安装 Docker Compose（版本2.0+）

2. **网络配置**
   - 开放端口：80（HTTP）、443（HTTPS）、9000（后端API，可选）
   - 配置安全组规则

3. **数据库**
   - MySQL 8.0+（可在服务器上安装或使用RDS）

## 一、服务器环境准备

### 1.1 安装 Docker

```bash
# Ubuntu/Debian
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo systemctl start docker
sudo systemctl enable docker

# 验证安装
docker --version
docker-compose --version
```

### 1.2 安装 Docker Compose

```bash
# 下载最新版本
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# 添加执行权限
sudo chmod +x /usr/local/bin/docker-compose

# 验证安装
docker-compose --version
```

### 1.3 安装 MySQL（如果使用本地数据库）

```bash
# 使用Docker运行MySQL
docker run -d \
  --name mysql \
  --restart=always \
  -e MYSQL_ROOT_PASSWORD=your_root_password \
  -e MYSQL_DATABASE=travel_loan \
  -p 3306:3306 \
  -v /data/mysql:/var/lib/mysql \
  mysql:8.0

# 或者使用Docker Compose（见下文）
```

## 二、项目文件准备

### 2.1 上传项目到服务器

```bash
# 方式1：使用Git
git clone <your-repo-url> /opt/qingchun_travel_loan
cd /opt/qingchun_travel_loan

# 方式2：使用SCP上传
scp -r qingchun_travel_loan_package root@your-server-ip:/opt/
```

### 2.2 配置环境变量

创建 `.env` 文件（在项目根目录）：

```bash
# 数据库配置
MYSQL_HOST=mysql
MYSQL_PORT=3306
MYSQL_DATABASE=travel_loan
MYSQL_USER=root
MYSQL_PASSWORD=your_mysql_password

# 后端配置
BACKEND_PORT=9000
JWT_SECRET=your-jwt-secret-key-2024

# 前端配置
VITE_API_BASE_URL=http://your-server-ip:9000/api

# 文件上传路径
UPLOAD_PATH=/app/uploads
```

## 三、Docker 镜像构建

### 3.1 后端镜像构建

后端使用 Spring Boot，需要：
- Java 8
- Maven
- 构建JAR包
- 运行JAR包

### 3.2 前端镜像构建

前端使用 Vite + React，需要：
- Node.js 18+
- 构建静态文件
- Nginx 提供静态文件服务

## 四、部署步骤

### 4.1 使用 Docker Compose 一键部署（推荐）

```bash
# 进入项目目录
cd /opt/qingchun_travel_loan_package

# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down

# 重启服务
docker-compose restart
```

### 4.2 手动部署步骤

#### 步骤1：构建后端镜像

```bash
cd backend
docker build -t qingchun-backend:latest .
```

#### 步骤2：构建前端镜像

```bash
cd ../qingchun_travel_loan
docker build -t qingchun-frontend:latest .
```

#### 步骤3：启动MySQL

```bash
docker run -d \
  --name mysql \
  --restart=always \
  -e MYSQL_ROOT_PASSWORD=your_password \
  -e MYSQL_DATABASE=travel_loan \
  -p 3306:3306 \
  -v mysql_data:/var/lib/mysql \
  mysql:8.0
```

#### 步骤4：初始化数据库

```bash
# 等待MySQL启动
sleep 10

# 导入数据库结构
docker exec -i mysql mysql -uroot -pyour_password travel_loan < backend/src/main/resources/schema.sql
```

#### 步骤5：启动后端服务

```bash
docker run -d \
  --name qingchun-backend \
  --restart=always \
  --link mysql:mysql \
  -p 9000:9000 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/travel_loan \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  -v /data/uploads:/app/uploads \
  qingchun-backend:latest
```

#### 步骤6：启动前端服务

```bash
docker run -d \
  --name qingchun-frontend \
  --restart=always \
  -p 80:80 \
  -e VITE_API_BASE_URL=http://your-server-ip:9000/api \
  qingchun-frontend:latest
```

## 五、访问应用

- **前端应用**：http://your-server-ip
- **后端API**：http://your-server-ip:9000/api
- **Swagger文档**：http://your-server-ip:9000/api/swagger-ui.html

## 六、常用操作

### 6.1 查看日志

```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend
docker-compose logs -f frontend

# 查看容器日志
docker logs qingchun-backend
docker logs qingchun-frontend
```

### 6.2 更新应用

```bash
# 停止服务
docker-compose down

# 重新构建镜像
docker-compose build --no-cache

# 启动服务
docker-compose up -d
```

### 6.3 备份数据库

```bash
# 备份
docker exec mysql mysqldump -uroot -pyour_password travel_loan > backup_$(date +%Y%m%d).sql

# 恢复
docker exec -i mysql mysql -uroot -pyour_password travel_loan < backup_20240101.sql
```

### 6.4 查看容器状态

```bash
docker-compose ps
docker ps -a
```

## 七、故障排查

### 7.1 后端无法连接数据库

```bash
# 检查MySQL容器是否运行
docker ps | grep mysql

# 检查网络连接
docker exec qingchun-backend ping mysql

# 查看后端日志
docker logs qingchun-backend
```

### 7.2 前端无法访问后端API

- 检查后端服务是否正常运行
- 检查防火墙和安全组配置
- 检查前端环境变量中的API地址配置

### 7.3 文件上传失败

```bash
# 检查上传目录权限
docker exec qingchun-backend ls -la /app/uploads

# 创建上传目录
docker exec qingchun-backend mkdir -p /app/uploads
docker exec qingchun-backend chmod 777 /app/uploads
```

## 八、性能优化

### 8.1 使用Nginx反向代理

配置Nginx反向代理，统一端口访问：

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端
    location / {
        proxy_pass http://localhost:80;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # 后端API
    location /api {
        proxy_pass http://localhost:9000/api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 8.2 配置HTTPS

使用Let's Encrypt免费SSL证书：

```bash
# 安装Certbot
sudo apt-get install certbot python3-certbot-nginx

# 获取证书
sudo certbot --nginx -d your-domain.com
```

## 九、监控和维护

### 9.1 设置自动重启

Docker Compose配置中已包含 `restart: always`，容器会自动重启。

### 9.2 日志轮转

配置Docker日志驱动，限制日志大小：

```yaml
logging:
  driver: "json-file"
  options:
    max-size: "10m"
    max-file: "3"
```

### 9.3 资源限制

在docker-compose.yml中设置资源限制：

```yaml
deploy:
  resources:
    limits:
      cpus: '1'
      memory: 1G
    reservations:
      cpus: '0.5'
      memory: 512M
```

## 十、安全建议

1. **修改默认密码**：修改MySQL root密码和JWT密钥
2. **使用环境变量**：敏感信息不要硬编码在配置文件中
3. **限制端口访问**：只开放必要的端口
4. **定期更新**：保持Docker镜像和系统更新
5. **备份数据**：定期备份数据库和重要文件

## 十一、联系支持

如遇到问题，请查看：
- 项目README文档
- Docker日志输出
- 服务器系统日志

---

**最后更新**：2024年

