# Mock数据生成说明 - UserId 53

## 概述

本脚本为 userId=53 的用户创建完整的还款管理测试数据，包括：
- 3个贷款申请（不同状态）
- 21个还款计划（12期+6期+3期）
- 7条还款记录

## 数据内容

### 贷款申请

1. **申请1**：毕业旅行贷款
   - 金额：10,000元
   - 期限：12期
   - 状态：DISBURSED（已发放）
   - 放款时间：2个月前

2. **申请2**：短途旅游贷款
   - 金额：5,000元
   - 期限：6期
   - 状态：DISBURSED（已发放）
   - 放款时间：1个月前

3. **申请3**：研学游贷款
   - 金额：3,000元
   - 期限：3期
   - 状态：COMPLETED（已完成）
   - 放款时间：5个月前

### 还款计划

**申请1（12期）**：
- 前3期：已还款（PAID）
- 后9期：待还款（PENDING）
- 每期本金：833.33元
- 每期利息：递减（从100元到8.33元）

**申请2（6期）**：
- 第1期：已还款（PAID）
- 后5期：待还款（PENDING）
- 每期本金：833.33元
- 每期利息：递减（从50元到8.33元）

**申请3（3期）**：
- 全部3期：已还款（PAID）
- 每期本金：1000.00元
- 每期利息：递减（从30元到10元）

### 还款记录

- 申请1：3条记录（对应前3期）
- 申请2：1条记录（对应第1期）
- 申请3：3条记录（对应全部3期）

## 使用方法

### 1. 检查产品ID

脚本默认使用 `product_id = 1`，如果您的数据库中产品ID不同，请先修改脚本中的 `product_id` 值。

```sql
-- 查看可用的产品ID
SELECT id, product_name, product_type FROM loan_product LIMIT 5;
```

### 2. 执行脚本

```bash
# 方式1：使用mysql命令行
mysql -u root -p travel_loan < mock_data_user53.sql

# 方式2：在MySQL客户端中执行
mysql -u root -p
USE travel_loan;
SOURCE /path/to/mock_data_user53.sql;
```

### 3. 验证数据

脚本执行后会自动显示：
- 创建的贷款申请列表
- 还款计划列表
- 还款记录列表
- 统计信息

## 数据统计

执行脚本后，userId=53 的数据统计：

- **申请数量**：3个
- **还款计划总数**：21个
- **已还期数**：7期
- **待还期数**：14期
- **已还金额**：约 6,000+ 元
- **待还金额**：约 12,000+ 元
- **还款记录数**：7条

## 注意事项

1. **产品ID**：确保数据库中存在 `product_id = 1` 的产品，否则会报外键错误
2. **用户ID**：如果userId=53已存在，脚本会更新用户名但不会删除现有数据
3. **时间计算**：还款日期基于当前时间计算，确保时间逻辑正确
4. **金额精度**：由于四舍五入，最后一期的本金可能略有调整以保证总额正确

## 修改产品ID

如果您的产品ID不是1，请修改脚本中的以下部分：

```sql
-- 将 product_id 从 1 改为您的产品ID
INSERT INTO `loan_application` (
    `user_id`, `product_id`, ...  -- 修改这里的 product_id
) VALUES (
    53, 1, ...  -- 修改这里的 1 为您的产品ID
);
```

## 清理数据

如果需要清理这些测试数据，可以执行：

```sql
-- 删除还款记录
DELETE FROM repayment_record WHERE user_id = 53;

-- 删除还款计划（会自动级联删除）
DELETE FROM repayment_plan 
WHERE application_id IN (SELECT id FROM loan_application WHERE user_id = 53);

-- 删除贷款申请
DELETE FROM loan_application WHERE user_id = 53;

-- 可选：删除用户
DELETE FROM user WHERE id = 53;
```

## 测试建议

1. **前端测试**：
   - 登录userId=53的账号
   - 访问还款管理页面
   - 查看还款明细可视化页面
   - 测试提前还款测算功能

2. **API测试**：
   ```bash
   # 获取还款概览
   curl -H "Authorization: Bearer <token>" http://localhost:9000/api/repayment/overview
   
   # 获取产品还款详情
   curl -H "Authorization: Bearer <token>" http://localhost:9000/api/repayment/product-details
   
   # 获取还款计划
   curl -H "Authorization: Bearer <token>" http://localhost:9000/api/repayment/plans
   
   # 获取还款记录
   curl -H "Authorization: Bearer <token>" http://localhost:9000/api/repayment/records
   ```

---

**创建日期**: 2024-01-02  
**版本**: 1.0

