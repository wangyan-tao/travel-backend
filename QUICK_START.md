# 青春旅贷 Java 后端快速启动指南

## 前置要求

- Java 8 或更高版本
- Maven 3.6+
- MySQL 5.7+ 或 8.0+
- （可选）Postman 或 curl 用于API测试

## 快速启动步骤

### 1. 创建数据库

```bash
# 登录MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE qingchun_travel_loan CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 使用数据库
USE qingchun_travel_loan;

# 执行初始化脚本
SOURCE /home/ubuntu/qingchun_travel_loan_backend/src/main/resources/schema.sql;
```

### 2. 配置数据库连接

编辑 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/qingchun_travel_loan?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root          # 修改为你的数据库用户名
    password: your-password # 修改为你的数据库密码
```

### 3. 启动后端服务

```bash
# 进入项目目录
cd /home/ubuntu/qingchun_travel_loan_backend

# 方式一：使用Maven直接运行
mvn spring-boot:run

# 方式二：打包后运行
mvn clean package
java -jar target/qingchun-travel-loan-1.0.0.jar
```

启动成功后，你会看到：

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::               (v2.7.18)

2024-01-02 08:00:00.000  INFO 12345 --- [main] c.q.t.TravelLoanApplication : Started TravelLoanApplication in 3.456 seconds
```

服务运行在: `http://localhost:8080`

### 4. 验证服务是否启动

```bash
# 测试健康检查接口
curl http://localhost:8080/actuator/health

# 预期响应
{"status":"UP"}
```

### 5. 注册第一个用户

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "phone": "13800138000"
  }'
```

成功响应：

```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "userId": 1,
    "username": "testuser",
    "email": "test@example.com",
    "role": "USER",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### 6. 提升用户为管理员

```bash
# 登录MySQL
mysql -u root -p qingchun_travel_loan

# 将用户提升为管理员
UPDATE user SET role = 'ADMIN' WHERE username = 'testuser';

# 验证
SELECT id, username, email, role FROM user;
```

### 7. 登录获取Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

成功响应：

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": 1,
    "username": "testuser",
    "role": "ADMIN",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 604800000
  }
}
```

### 8. 使用Token访问管理后台API

```bash
# 将上一步获取的token替换到这里
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# 访问统计数据API
curl -X GET "http://localhost:8080/api/statistics/full" \
  -H "Authorization: Bearer $TOKEN"
```

### 9. 访问Swagger API文档

在浏览器中打开: `http://localhost:8080/swagger-ui.html`

可以在线测试所有API接口。

## 前端连接后端

### 修改前端配置

编辑 `client/src/lib/axios.ts`，确认后端地址正确：

```typescript
const axios = axiosLib.create({
  baseURL: 'http://localhost:8080',  // Java后端地址
  timeout: 10000,
});
```

### 启动前端

```bash
cd /home/ubuntu/qingchun_travel_loan
pnpm dev
```

前端运行在: `http://localhost:3000`

### 测试前后端联调

1. 访问前端: `http://localhost:3000`
2. 点击"注册"按钮注册账号
3. 登录后访问管理后台: `http://localhost:3000/admin/dashboard`
4. 如果看到统计数据，说明前后端联调成功！

## 常见问题

### Q1: 启动失败，提示端口被占用

```bash
# 查看8080端口占用情况
lsof -i:8080

# 杀死占用进程
kill -9 <PID>

# 或者修改端口
# 在 application.yml 中添加：
server:
  port: 8081
```

### Q2: 数据库连接失败

检查以下几点：
1. MySQL服务是否启动: `systemctl status mysql`
2. 数据库是否创建: `SHOW DATABASES;`
3. 用户名密码是否正确
4. 防火墙是否允许3306端口

### Q3: 前端无法连接后端

检查：
1. 后端是否启动: `curl http://localhost:8080/actuator/health`
2. CORS配置是否正确（已在CorsConfig.java中配置）
3. 前端axios baseURL是否正确

### Q4: Token验证失败

确认：
1. Token是否正确复制（包含完整的JWT字符串）
2. Token是否过期（默认7天有效期）
3. Authorization header格式: `Bearer <token>`

## 开发工具推荐

### 1. API测试工具

- **Postman**: 图形化界面，功能强大
- **Insomnia**: 轻量级，界面简洁
- **curl**: 命令行工具，适合脚本化

### 2. 数据库管理工具

- **MySQL Workbench**: 官方工具
- **DBeaver**: 开源免费，支持多种数据库
- **Navicat**: 商业软件，功能丰富

### 3. Java开发工具

- **IntelliJ IDEA**: 推荐，功能强大
- **Eclipse**: 开源免费
- **VS Code**: 轻量级，需要安装Java插件

## 生产环境部署

### 1. 修改配置

```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:mysql://your-prod-db:3306/qingchun_travel_loan
    username: prod_user
    password: strong_password

jwt:
  secret: your-very-strong-secret-key-for-production
  expiration: 604800000

server:
  port: 8080
```

### 2. 打包部署

```bash
# 打包
mvn clean package -Dmaven.test.skip=true

# 运行
java -jar target/qingchun-travel-loan-1.0.0.jar --spring.profiles.active=prod
```

### 3. 使用Docker部署（可选）

```dockerfile
FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/qingchun-travel-loan-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

```bash
# 构建镜像
docker build -t qingchun-travel-loan:1.0.0 .

# 运行容器
docker run -d -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/qingchun_travel_loan \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=password \
  qingchun-travel-loan:1.0.0
```

## 下一步

- 阅读 [JWT认证API文档](./JWT_AUTH_API.md) 了解详细的API接口
- 阅读 [API集成文档](../qingchun_travel_loan/API_INTEGRATION.md) 了解前后端集成
- 访问 Swagger 文档进行API测试
- 开始开发业务功能

## 技术支持

如有问题，请查看：
- 项目README
- API文档
- 或提交Issue
