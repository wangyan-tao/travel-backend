# Java 后端 JWT 认证系统 API 文档

## 概述

本文档详细说明青春旅贷Java后端的JWT认证系统，包括用户注册、登录、权限管理等核心功能。

## 基础信息

- **Base URL**: `http://localhost:8080`
- **认证方式**: JWT (JSON Web Token)
- **Token 传递**: HTTP Header `Authorization: Bearer <token>`
- **Token 有效期**: 7天（可在 `application.yml` 中配置）

## API 接口列表

### 1. 用户注册

**接口地址**: `POST /api/auth/register`

**请求头**:
```
Content-Type: application/json
```

**请求体**:
```json
{
  "username": "zhangsan",
  "password": "password123",
  "email": "zhangsan@example.com",
  "phone": "13800138000"
}
```

**请求参数说明**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名，3-20个字符 |
| password | String | 是 | 密码，6-20个字符 |
| email | String | 是 | 邮箱地址 |
| phone | String | 否 | 手机号码 |

**成功响应** (HTTP 200):
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "userId": 1,
    "username": "zhangsan",
    "email": "zhangsan@example.com",
    "role": "USER",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**失败响应** (HTTP 400):
```json
{
  "code": 400,
  "message": "用户名已存在",
  "data": null
}
```

**错误码说明**:
- `400`: 参数错误或用户名/邮箱已存在
- `500`: 服务器内部错误

---

### 2. 用户登录

**接口地址**: `POST /api/auth/login`

**请求头**:
```
Content-Type: application/json
```

**请求体**:
```json
{
  "username": "zhangsan",
  "password": "password123"
}
```

**请求参数说明**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名或邮箱 |
| password | String | 是 | 密码 |

**成功响应** (HTTP 200):
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": 1,
    "username": "zhangsan",
    "email": "zhangsan@example.com",
    "role": "USER",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 604800000
  }
}
```

**响应字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| userId | Long | 用户ID |
| username | String | 用户名 |
| email | String | 邮箱 |
| role | String | 用户角色（USER/ADMIN） |
| token | String | JWT访问令牌 |
| expiresIn | Long | Token有效期（毫秒） |

**失败响应** (HTTP 401):
```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null
}
```

**错误码说明**:
- `401`: 认证失败（用户名或密码错误）
- `500`: 服务器内部错误

---

### 3. 获取当前用户信息

**接口地址**: `GET /api/auth/me`

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**成功响应** (HTTP 200):
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "zhangsan",
    "email": "zhangsan@example.com",
    "phone": "13800138000",
    "role": "USER",
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

**失败响应** (HTTP 401):
```json
{
  "code": 401,
  "message": "未授权，请先登录",
  "data": null
}
```

---

### 4. 用户登出

**接口地址**: `POST /api/auth/logout`

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**成功响应** (HTTP 200):
```json
{
  "code": 200,
  "message": "登出成功",
  "data": null
}
```

**说明**: 
- JWT是无状态的，登出主要由前端删除Token实现
- 后端可以将Token加入黑名单（需要额外实现）

---

## 权限管理

### 角色说明

系统支持两种角色：

1. **USER（普通用户）**
   - 默认角色
   - 可以访问用户端功能
   - 可以申请贷款、查看还款信息等

2. **ADMIN（管理员）**
   - 管理员角色
   - 可以访问管理后台
   - 可以审批贷款、查看统计数据等

### 如何获取管理员权限

#### 方法一：数据库直接修改（推荐用于开发测试）

1. 先注册一个普通账号
2. 在数据库中执行SQL：

```sql
-- 通过用户名修改
UPDATE user SET role = 'ADMIN' WHERE username = 'zhangsan';

-- 或通过邮箱修改
UPDATE user SET role = 'ADMIN' WHERE email = 'zhangsan@example.com';

-- 或通过用户ID修改
UPDATE user SET role = 'ADMIN' WHERE id = 1;
```

#### 方法二：注册时指定（需要修改代码）

在 `AuthService.java` 的注册方法中添加逻辑：

```java
// 如果是特定邮箱，自动设置为管理员
if ("admin@qingchun.com".equals(request.getEmail())) {
    user.setRole("ADMIN");
} else {
    user.setRole("USER");
}
```

#### 方法三：创建管理员提升接口（需要超级管理员权限）

创建一个只有超级管理员才能访问的接口：

```java
@PostMapping("/promote-admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public Result<?> promoteToAdmin(@RequestParam Long userId) {
    userService.updateUserRole(userId, "ADMIN");
    return Result.success("提升成功");
}
```

---

## 权限验证

### 接口权限注解

在Controller方法上使用注解控制访问权限：

```java
// 需要登录
@PreAuthorize("isAuthenticated()")
public Result<?> userOnlyEndpoint() { }

// 需要管理员权限
@PreAuthorize("hasRole('ADMIN')")
public Result<?> adminOnlyEndpoint() { }

// 需要特定权限
@PreAuthorize("hasAuthority('APPROVE_LOAN')")
public Result<?> approveEndpoint() { }
```

### 前端权限判断

前端根据登录接口返回的 `role` 字段判断用户权限：

```typescript
// 登录后保存用户信息
const loginResponse = await axios.post('/api/auth/login', credentials);
const { token, role } = loginResponse.data.data;

// 保存到localStorage
localStorage.setItem('token', token);
localStorage.setItem('role', role);

// 判断是否为管理员
const isAdmin = role === 'ADMIN';

// 路由守卫
if (isAdmin) {
  // 允许访问管理后台
  router.push('/admin/dashboard');
} else {
  // 跳转到用户端
  router.push('/user/home');
}
```

---

## 使用示例

### 1. 完整的注册登录流程

```bash
# 1. 注册新用户
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "zhangsan",
    "password": "password123",
    "email": "zhangsan@example.com",
    "phone": "13800138000"
  }'

# 响应：
# {
#   "code": 200,
#   "message": "注册成功",
#   "data": {
#     "userId": 1,
#     "username": "zhangsan",
#     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
#   }
# }

# 2. 登录获取Token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "zhangsan",
    "password": "password123"
  }'

# 响应：
# {
#   "code": 200,
#   "message": "登录成功",
#   "data": {
#     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#     "role": "USER"
#   }
# }

# 3. 使用Token访问受保护的接口
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 2. 将用户提升为管理员

```bash
# 1. 登录MySQL数据库
mysql -u root -p qingchun_travel_loan

# 2. 查看用户列表
SELECT id, username, email, role FROM user;

# 3. 将用户提升为管理员
UPDATE user SET role = 'ADMIN' WHERE username = 'zhangsan';

# 4. 验证修改
SELECT id, username, email, role FROM user WHERE username = 'zhangsan';
```

### 3. 前端集成示例

```typescript
// api/auth.ts
import axios from './axios';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  phone?: string;
}

export const authApi = {
  // 注册
  register: async (data: RegisterRequest) => {
    const response = await axios.post('/api/auth/register', data);
    return response.data;
  },

  // 登录
  login: async (data: LoginRequest) => {
    const response = await axios.post('/api/auth/login', data);
    const { token, role } = response.data.data;
    
    // 保存Token到localStorage
    localStorage.setItem('token', token);
    localStorage.setItem('role', role);
    
    // 设置axios默认header
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    
    return response.data;
  },

  // 获取当前用户信息
  getCurrentUser: async () => {
    const response = await axios.get('/api/auth/me');
    return response.data.data;
  },

  // 登出
  logout: async () => {
    await axios.post('/api/auth/logout');
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    delete axios.defaults.headers.common['Authorization'];
  },

  // 检查是否为管理员
  isAdmin: () => {
    return localStorage.getItem('role') === 'ADMIN';
  }
};
```

---

## 安全建议

### 1. 密码安全

- ✅ 已实现：使用BCrypt加密存储密码
- ✅ 已实现：密码最小长度6位
- 建议：添加密码强度验证（大小写+数字+特殊字符）
- 建议：实现密码找回功能

### 2. Token安全

- ✅ 已实现：使用HTTPS传输（生产环境）
- ✅ 已实现：Token设置有效期
- 建议：实现Token刷新机制
- 建议：实现Token黑名单（用于强制登出）

### 3. 接口安全

- ✅ 已实现：CORS跨域配置
- ✅ 已实现：全局异常处理
- 建议：添加请求频率限制（防止暴力破解）
- 建议：添加验证码（防止机器人注册）

### 4. 权限安全

- ✅ 已实现：基于角色的访问控制（RBAC）
- 建议：实现更细粒度的权限控制
- 建议：添加操作日志记录

---

## 常见问题

### Q1: Token过期后如何处理？

**A**: 前端需要捕获401错误，提示用户重新登录：

```typescript
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // Token过期，清除本地存储
      localStorage.clear();
      // 跳转到登录页
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

### Q2: 如何实现"记住我"功能？

**A**: 可以通过延长Token有效期实现：

1. 登录时传递 `rememberMe` 参数
2. 后端根据参数设置不同的Token有效期（如7天 vs 30天）
3. 前端将Token保存到localStorage（而不是sessionStorage）

### Q3: 如何批量创建管理员账号？

**A**: 可以编写SQL脚本批量插入：

```sql
-- 插入管理员账号（密码需要先用BCrypt加密）
INSERT INTO user (username, password, email, role, created_at, updated_at)
VALUES 
  ('admin1', '$2a$10$...', 'admin1@qingchun.com', 'ADMIN', NOW(), NOW()),
  ('admin2', '$2a$10$...', 'admin2@qingchun.com', 'ADMIN', NOW(), NOW());
```

### Q4: Token存储在哪里更安全？

**A**: 
- **localStorage**: 持久化存储，刷新页面不丢失，但容易受XSS攻击
- **sessionStorage**: 关闭标签页后清除，相对安全
- **httpOnly Cookie**: 最安全，但需要后端配合设置

推荐使用 **httpOnly Cookie** + **CSRF Token** 的方式。

---

## 配置说明

### application.yml 配置

```yaml
# JWT配置
jwt:
  secret: your-secret-key-here-change-in-production  # JWT密钥（生产环境必须修改）
  expiration: 604800000  # Token有效期（毫秒），默认7天

# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/qingchun_travel_loan?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your-password
```

### 生产环境注意事项

1. **修改JWT密钥**: 使用强随机密钥，不要使用默认值
2. **启用HTTPS**: 确保Token在传输过程中加密
3. **配置CORS**: 只允许信任的域名访问
4. **数据库安全**: 使用强密码，限制数据库访问IP

---

## 附录：完整的用户表结构

```sql
CREATE TABLE `user` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
  `email` VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
  `phone` VARCHAR(20) COMMENT '手机号',
  `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：USER-普通用户, ADMIN-管理员',
  `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-激活, DISABLED-禁用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_username (username),
  INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

---

## 技术支持

如有问题，请参考：
- Swagger API文档: `http://localhost:8080/swagger-ui.html`
- 项目README: `/qingchun_travel_loan_backend/README.md`
- API集成文档: `/qingchun_travel_loan/API_INTEGRATION.md`
