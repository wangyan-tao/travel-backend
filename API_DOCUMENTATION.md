# 青春旅贷后端API文档

## 基础信息

- **Base URL**: `http://localhost:8080/api`
- **认证方式**: JWT Bearer Token
- **请求头**: `Authorization: Bearer {token}`

## API接口列表

### 1. 认证模块

#### 1.1 用户注册
- **接口**: `POST /auth/register`
- **权限**: 公开
- **请求体**:
```json
{
  "username": "string",
  "password": "string",
  "phone": "string",
  "email": "string"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "user": {
      "id": 1,
      "username": "zhangsan",
      "phone": "13800138000",
      "role": "USER"
    }
  }
}
```

#### 1.2 用户登录
- **接口**: `POST /auth/login`
- **权限**: 公开
- **请求体**:
```json
{
  "username": "string",
  "password": "string"
}
```

#### 1.3 获取当前用户信息
- **接口**: `GET /auth/me`
- **权限**: 需要认证

### 2. 实名认证模块

#### 2.1 提交实名信息
- **接口**: `POST /identity/submit`
- **权限**: 需要认证
- **请求体**:
```json
{
  "realName": "张三",
  "idCardNo": "110101199001011234",
  "idCardFrontUrl": "https://...",
  "idCardBackUrl": "https://...",
  "studentId": "2020001",
  "studentCardUrl": "https://...",
  "school": "北京大学",
  "major": "计算机科学与技术",
  "enrollmentDate": "2020-09-01",
  "bankCardNo": "6222021234567890",
  "bankName": "中国工商银行"
}
```

#### 2.2 获取实名信息
- **接口**: `GET /identity/info`
- **权限**: 需要认证

### 3. 担保人模块

#### 3.1 提交担保人信息
- **接口**: `POST /guarantor/submit`
- **权限**: 需要认证
- **请求体**:
```json
{
  "name": "李四",
  "idCardNo": "110101197001011234",
  "idCardFrontUrl": "https://...",
  "idCardBackUrl": "https://...",
  "relationship": "父亲",
  "phone": "13900139000",
  "workUnit": "某某公司",
  "position": "经理"
}
```

#### 3.2 签署知情同意书
- **接口**: `POST /guarantor/sign-agreement`
- **权限**: 需要认证

### 4. 位置信息模块

#### 4.1 保存位置信息
- **接口**: `POST /location/save`
- **权限**: 需要认证
- **请求体**:
```json
{
  "currentCity": "北京市",
  "homeCity": "上海市",
  "schoolCity": "北京市",
  "latitude": 39.9042,
  "longitude": 116.4074,
  "locationAuthorized": true
}
```

### 5. 贷款产品模块

#### 5.1 获取产品列表
- **接口**: `GET /loan-products`
- **权限**: 公开
- **查询参数**:
  - `category`: 产品分类（短途游贷/毕业旅行贷/研学游贷/跨省游贷）
  - `minAmount`: 最低额度
  - `maxAmount`: 最高额度
  - `interestType`: 利率类型

#### 5.2 获取产品详情
- **接口**: `GET /loan-products/{id}`
- **权限**: 公开

### 6. 贷款申请模块

#### 6.1 提交贷款申请
- **接口**: `POST /loan-applications`
- **权限**: 需要认证
- **请求体**:
```json
{
  "productId": 1,
  "applyAmount": 5000.00,
  "applyPeriod": 12,
  "loanPurpose": "毕业旅行"
}
```

#### 6.2 查询申请列表
- **接口**: `GET /loan-applications`
- **权限**: 需要认证

#### 6.3 查询申请详情
- **接口**: `GET /loan-applications/{id}`
- **权限**: 需要认证

### 7. 兼职商铺模块

#### 7.1 获取兼职列表
- **接口**: `GET /part-time-jobs`
- **权限**: 需要认证
- **查询参数**:
  - `city`: 城市
  - `jobType`: 岗位类型

#### 7.2 上传兼职证明
- **接口**: `POST /job-proofs`
- **权限**: 需要认证

### 8. 学业荣誉模块

#### 8.1 上传荣誉证明
- **接口**: `POST /academic-honors`
- **权限**: 需要认证
- **请求体**:
```json
{
  "honorType": "奖学金",
  "honorName": "国家奖学金",
  "issueDate": "2023-10-01",
  "issueOrganization": "教育部",
  "proofUrl": "https://...",
  "description": "2023年度国家奖学金"
}
```

#### 8.2 获取荣誉列表
- **接口**: `GET /academic-honors`
- **权限**: 需要认证

### 9. 还款管理模块

#### 9.1 获取还款计划
- **接口**: `GET /repayment-plans/{applicationId}`
- **权限**: 需要认证

#### 9.2 获取还款记录
- **接口**: `GET /repayment-records`
- **权限**: 需要认证
- **查询参数**:
  - `startDate`: 开始日期
  - `endDate`: 结束日期

#### 9.3 提前还款测算
- **接口**: `POST /repayment-plans/calculate-early-repayment`
- **权限**: 需要认证
- **请求体**:
```json
{
  "applicationId": 1,
  "earlyRepaymentAmount": 3000.00
}
```

### 10. 管理端模块

#### 10.1 用户管理
- **接口**: `GET /admin/users`
- **权限**: 管理员
- **查询参数**:
  - `keyword`: 搜索关键词
  - `verifyStatus`: 核验状态

#### 10.2 贷款审批
- **接口**: `POST /admin/loan-applications/{id}/approve`
- **权限**: 管理员
- **请求体**:
```json
{
  "approved": true,
  "approvedAmount": 5000.00,
  "approvedPeriod": 12,
  "approvalOpinion": "审批通过"
}
```

#### 10.3 数据统计
- **接口**: `GET /admin/statistics`
- **权限**: 管理员
- **响应**:
```json
{
  "userCount": 1000,
  "applicationCount": 500,
  "approvedCount": 400,
  "rejectedCount": 50,
  "totalLoanAmount": 2000000.00,
  "onTimeRepaymentRate": 0.95
}
```

### 11. 通知模块

#### 11.1 获取通知列表
- **接口**: `GET /notifications`
- **权限**: 需要认证

#### 11.2 标记已读
- **接口**: `PUT /notifications/{id}/read`
- **权限**: 需要认证

## 文件上传

### 上传文件
- **接口**: `POST /upload`
- **权限**: 需要认证
- **请求**: `multipart/form-data`
- **响应**:
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "https://storage.example.com/files/xxx.jpg"
  }
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 启动说明

1. 确保MySQL数据库已启动
2. 执行`schema.sql`初始化数据库
3. 运行`mvn spring-boot:run`启动后端服务
4. 访问API文档: http://localhost:8080/api/swagger-ui.html
