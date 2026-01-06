-- ============================================
-- 为 userId=53 创建 Mock 数据
-- ============================================

USE travel_loan;

-- 确保用户存在（如果不存在则创建）
INSERT INTO `user` (`id`, `username`, `password`, `phone`, `email`, `role`, `status`)
VALUES (53, 'testuser53', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800000053', 'test53@example.com', 'USER', 1)
ON DUPLICATE KEY UPDATE `username` = `username`;

-- 假设产品ID为1（如果不存在，请先创建产品）
-- 如果产品ID不同，请修改下面的 product_id 值

-- ============================================
-- 1. 创建贷款申请
-- ============================================

-- 申请1：已发放状态（DISBURSED）- 12期，10000元
INSERT INTO `loan_application` (
    `user_id`, `product_id`, `apply_amount`, `apply_term`, `purpose`, 
    `status`, `apply_time`, `approve_time`, `approver_id`, 
    `loan_amount`, `loan_time`, `created_at`, `updated_at`
) VALUES (
    53, 1, 10000.00, 12, '毕业旅行贷款',
    'DISBURSED', 
    DATE_SUB(NOW(), INTERVAL 3 MONTH), 
    DATE_SUB(NOW(), INTERVAL 2 MONTH),
    1,
    10000.00,
    DATE_SUB(NOW(), INTERVAL 2 MONTH),
    DATE_SUB(NOW(), INTERVAL 3 MONTH),
    NOW()
);

SET @application_id_1 = LAST_INSERT_ID();

-- 申请2：已发放状态（DISBURSED）- 6期，5000元
INSERT INTO `loan_application` (
    `user_id`, `product_id`, `apply_amount`, `apply_term`, `purpose`, 
    `status`, `apply_time`, `approve_time`, `approver_id`, 
    `loan_amount`, `loan_time`, `created_at`, `updated_at`
) VALUES (
    53, 1, 5000.00, 6, '短途旅游贷款',
    'DISBURSED', 
    DATE_SUB(NOW(), INTERVAL 2 MONTH), 
    DATE_SUB(NOW(), INTERVAL 1 MONTH),
    1,
    5000.00,
    DATE_SUB(NOW(), INTERVAL 1 MONTH),
    DATE_SUB(NOW(), INTERVAL 2 MONTH),
    NOW()
);

SET @application_id_2 = LAST_INSERT_ID();

-- 申请3：已完成状态（COMPLETED）- 3期，3000元
INSERT INTO `loan_application` (
    `user_id`, `product_id`, `apply_amount`, `apply_term`, `purpose`, 
    `status`, `apply_time`, `approve_time`, `approver_id`, 
    `loan_amount`, `loan_time`, `created_at`, `updated_at`
) VALUES (
    53, 1, 3000.00, 3, '研学游贷款',
    'COMPLETED', 
    DATE_SUB(NOW(), INTERVAL 6 MONTH), 
    DATE_SUB(NOW(), INTERVAL 5 MONTH),
    1,
    3000.00,
    DATE_SUB(NOW(), INTERVAL 5 MONTH),
    DATE_SUB(NOW(), INTERVAL 6 MONTH),
    NOW()
);

SET @application_id_3 = LAST_INSERT_ID();

-- ============================================
-- 2. 创建还款计划（申请1：12期）
-- ============================================
-- 等额本息计算，年利率12%，月利率1%
-- 每期本金：10000/12 = 833.33
-- 每期利息：剩余本金 * 1%

INSERT INTO `repayment_plan` (`application_id`, `period_number`, `due_date`, `principal_amount`, `interest_amount`, `total_amount`, `status`) VALUES
(@application_id_1, 1, DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 MONTH), INTERVAL 1 MONTH), 833.33, 100.00, 933.33, 'PAID'),
(@application_id_1, 2, DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 MONTH), INTERVAL 2 MONTH), 833.33, 91.67, 925.00, 'PAID'),
(@application_id_1, 3, DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 MONTH), INTERVAL 3 MONTH), 833.33, 83.33, 916.66, 'PAID'),
(@application_id_1, 4, DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 MONTH), INTERVAL 4 MONTH), 833.33, 75.00, 908.33, 'PENDING'),
(@application_id_1, 5, DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 MONTH), INTERVAL 5 MONTH), 833.33, 66.67, 900.00, 'PENDING'),
(@application_id_1, 6, DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 MONTH), INTERVAL 6 MONTH), 833.33, 58.33, 891.66, 'PENDING'),
(@application_id_1, 7, DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 MONTH), INTERVAL 7 MONTH), 833.33, 50.00, 883.33, 'PENDING'),
(@application_id_1, 8, DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 MONTH), INTERVAL 8 MONTH), 833.33, 41.67, 875.00, 'PENDING'),
(@application_id_1, 9, DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 MONTH), INTERVAL 9 MONTH), 833.33, 33.33, 866.66, 'PENDING'),
(@application_id_1, 10, DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 MONTH), INTERVAL 10 MONTH), 833.33, 25.00, 858.33, 'PENDING'),
(@application_id_1, 11, DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 MONTH), INTERVAL 11 MONTH), 833.33, 16.67, 850.00, 'PENDING'),
(@application_id_1, 12, DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 MONTH), INTERVAL 12 MONTH), 833.37, 8.33, 841.70, 'PENDING');

-- ============================================
-- 3. 创建还款计划（申请2：6期）
-- ============================================
-- 每期本金：5000/6 = 833.33

INSERT INTO `repayment_plan` (`application_id`, `period_number`, `due_date`, `principal_amount`, `interest_amount`, `total_amount`, `status`) VALUES
(@application_id_2, 1, DATE_ADD(DATE_SUB(NOW(), INTERVAL 1 MONTH), INTERVAL 1 MONTH), 833.33, 50.00, 883.33, 'PAID'),
(@application_id_2, 2, DATE_ADD(DATE_SUB(NOW(), INTERVAL 1 MONTH), INTERVAL 2 MONTH), 833.33, 41.67, 875.00, 'PENDING'),
(@application_id_2, 3, DATE_ADD(DATE_SUB(NOW(), INTERVAL 1 MONTH), INTERVAL 3 MONTH), 833.33, 33.33, 866.66, 'PENDING'),
(@application_id_2, 4, DATE_ADD(DATE_SUB(NOW(), INTERVAL 1 MONTH), INTERVAL 4 MONTH), 833.33, 25.00, 858.33, 'PENDING'),
(@application_id_2, 5, DATE_ADD(DATE_SUB(NOW(), INTERVAL 1 MONTH), INTERVAL 5 MONTH), 833.33, 16.67, 850.00, 'PENDING'),
(@application_id_2, 6, DATE_ADD(DATE_SUB(NOW(), INTERVAL 1 MONTH), INTERVAL 6 MONTH), 833.35, 8.33, 841.68, 'PENDING');

-- ============================================
-- 4. 创建还款计划（申请3：3期，全部已还）
-- ============================================
-- 每期本金：3000/3 = 1000.00

INSERT INTO `repayment_plan` (`application_id`, `period_number`, `due_date`, `principal_amount`, `interest_amount`, `total_amount`, `status`) VALUES
(@application_id_3, 1, DATE_ADD(DATE_SUB(NOW(), INTERVAL 5 MONTH), INTERVAL 1 MONTH), 1000.00, 30.00, 1030.00, 'PAID'),
(@application_id_3, 2, DATE_ADD(DATE_SUB(NOW(), INTERVAL 5 MONTH), INTERVAL 2 MONTH), 1000.00, 20.00, 1020.00, 'PAID'),
(@application_id_3, 3, DATE_ADD(DATE_SUB(NOW(), INTERVAL 5 MONTH), INTERVAL 3 MONTH), 1000.00, 10.00, 1010.00, 'PAID');

-- ============================================
-- 5. 创建还款记录
-- ============================================

-- 申请1的前3期还款记录
INSERT INTO `repayment_record` (`plan_id`, `application_id`, `user_id`, `payment_amount`, `payment_time`, `payment_method`, `transaction_id`)
SELECT id, @application_id_1, 53, total_amount, DATE_ADD(due_date, INTERVAL -2 DAY), '支付宝', CONCAT('TXN', UNIX_TIMESTAMP(), '_', id)
FROM repayment_plan 
WHERE application_id = @application_id_1 AND status = 'PAID'
ORDER BY period_number;

-- 申请2的第1期还款记录
INSERT INTO `repayment_record` (`plan_id`, `application_id`, `user_id`, `payment_amount`, `payment_time`, `payment_method`, `transaction_id`)
SELECT id, @application_id_2, 53, total_amount, DATE_ADD(due_date, INTERVAL -1 DAY), '微信支付', CONCAT('TXN', UNIX_TIMESTAMP(), '_', id)
FROM repayment_plan 
WHERE application_id = @application_id_2 AND status = 'PAID'
ORDER BY period_number;

-- 申请3的全部3期还款记录
INSERT INTO `repayment_record` (`plan_id`, `application_id`, `user_id`, `payment_amount`, `payment_time`, `payment_method`, `transaction_id`)
SELECT id, @application_id_3, 53, total_amount, DATE_ADD(due_date, INTERVAL -3 DAY), '银行卡', CONCAT('TXN', UNIX_TIMESTAMP(), '_', id)
FROM repayment_plan 
WHERE application_id = @application_id_3 AND status = 'PAID'
ORDER BY period_number;

-- ============================================
-- 查询验证数据
-- ============================================

-- 查看创建的申请
SELECT '贷款申请' AS '数据类型', id, user_id, product_id, apply_amount, apply_term, status, loan_amount, loan_time
FROM loan_application 
WHERE user_id = 53
ORDER BY id;

-- 查看还款计划
SELECT '还款计划' AS '数据类型', rp.id, rp.application_id, rp.period_number, rp.due_date, 
       rp.principal_amount, rp.interest_amount, rp.total_amount, rp.status
FROM repayment_plan rp
INNER JOIN loan_application la ON rp.application_id = la.id
WHERE la.user_id = 53
ORDER BY rp.application_id, rp.period_number;

-- 查看还款记录
SELECT '还款记录' AS '数据类型', rr.id, rr.plan_id, rr.application_id, rr.payment_amount, 
       rr.payment_time, rr.payment_method, rr.transaction_id
FROM repayment_record rr
WHERE rr.user_id = 53
ORDER BY rr.payment_time DESC;

-- 统计信息
SELECT 
    '统计信息' AS '数据类型',
    COUNT(DISTINCT la.id) AS '申请数量',
    COUNT(rp.id) AS '还款计划总数',
    SUM(CASE WHEN rp.status = 'PAID' THEN 1 ELSE 0 END) AS '已还期数',
    SUM(CASE WHEN rp.status = 'PENDING' THEN 1 ELSE 0 END) AS '待还期数',
    SUM(CASE WHEN rp.status = 'PAID' THEN rp.total_amount ELSE 0 END) AS '已还金额',
    SUM(CASE WHEN rp.status = 'PENDING' THEN rp.total_amount ELSE 0 END) AS '待还金额',
    COUNT(rr.id) AS '还款记录数'
FROM loan_application la
LEFT JOIN repayment_plan rp ON la.id = rp.application_id
LEFT JOIN repayment_record rr ON rp.id = rr.plan_id
WHERE la.user_id = 53;
