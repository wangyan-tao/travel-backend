# 贷后风险用户管理功能设置说明

## 概述

此功能用于管理贷后风险用户，包括风险用户列表展示、数据分析和通知发送功能。

## 数据库设置

### 1. 执行数据库迁移

首先执行数据库表创建脚本：

```sql
-- 在 schema.sql 中已包含表结构，如果单独执行，可以使用：
source travel-backend/src/main/resources/migration_add_overdue_risk.sql
```

或者在MySQL客户端执行：
```bash
mysql -u root -p travel_loan < travel-backend/src/main/resources/migration_add_overdue_risk.sql
```

### 2. 初始化测试数据

执行初始化数据脚本（包含测试用户、申请和风险用户数据）：

```bash
mysql -u root -p travel_loan < travel-backend/src/main/resources/init_overdue_risk_data.sql
```

或者在MySQL客户端执行：
```sql
source travel-backend/src/main/resources/init_overdue_risk_data.sql
```

**注意：** 
- 此脚本会创建10个测试用户（ID: 1001-1010）
- 会创建10个测试申请（ID: 1-10）
- 会创建10条风险用户记录
- 如果数据已存在，会使用 `ON DUPLICATE KEY UPDATE` 更新

### 3. 验证数据

查询风险用户数据：
```sql
SELECT * FROM overdue_risk_user;
```

查询统计数据：
```sql
SELECT 
    risk_level,
    COUNT(*) as count,
    SUM(overdue_amount) as total_amount,
    AVG(overdue_days) as avg_days
FROM overdue_risk_user
WHERE status = 'ACTIVE'
GROUP BY risk_level;
```

## 功能说明

### 1. 风险用户列表
- 显示所有有逾期风险的用户
- 支持按用户名、手机号搜索
- 支持按风险等级筛选（低风险/中风险/高风险/严重风险）

### 2. 数据分析
- **统计卡片**：风险用户总数、总逾期金额、平均逾期天数、高风险用户数
- **风险等级分布**：饼图展示各风险等级的用户分布
- **逾期趋势分析**：折线图展示逾期用户数和金额的趋势
- **各风险等级逾期金额对比**：柱状图对比不同风险等级的逾期金额
- **逾期天数分布**：柱状图展示不同逾期天数区间的用户分布

### 3. 通知功能
- 点击"发送通知"按钮可以向指定用户发送通知消息
- 通知类型为"OVERDUE"（逾期提醒）
- 通知会保存到 `notification` 表中

## API接口

### 获取逾期用户列表
```
GET /admin/overdue/users?keyword={keyword}&riskLevel={riskLevel}
```

### 获取统计数据
```
GET /admin/overdue/statistics
```

### 发送通知
```
POST /admin/notifications/send
Body: {
  userId: number,
  title: string,
  content: string,
  type: string
}
```

## 数据表结构

### overdue_risk_user 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键ID |
| user_id | BIGINT | 用户ID |
| application_id | BIGINT | 申请ID |
| product_id | BIGINT | 产品ID |
| risk_level | VARCHAR(20) | 风险等级 |
| overdue_amount | DECIMAL(10,2) | 逾期金额 |
| overdue_days | INT | 逾期天数 |
| last_repayment_date | DATE | 最后还款日期 |
| total_overdue_amount | DECIMAL(10,2) | 累计逾期金额 |
| overdue_count | INT | 逾期次数 |
| status | VARCHAR(20) | 状态（ACTIVE/RESOLVED） |

## 注意事项

1. 确保数据库中已有对应的用户、产品和申请记录
2. 风险等级包括：低风险、中风险、高风险、严重风险
3. 只有状态为 `ACTIVE` 的风险用户会被查询和统计
4. 统计数据会根据数据库中的实际数据动态计算

## 故障排查

如果前端无法获取数据：

1. 检查数据库连接配置
2. 确认表已创建：`SHOW TABLES LIKE 'overdue_risk_user';`
3. 确认有数据：`SELECT COUNT(*) FROM overdue_risk_user;`
4. 检查后端日志是否有错误信息
5. 确认用户有管理员权限（ADMIN角色）



