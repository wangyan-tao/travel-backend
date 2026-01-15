-- 初始化贷后风险用户数据
-- 注意：执行此脚本前，请确保已有对应的用户、产品和申请记录
-- 如果不存在，此脚本会创建测试数据

USE travel_loan;

-- 1. 创建测试用户（如果不存在）
INSERT INTO `user` (`id`, `username`, `password`, `phone`, `email`, `role`, `status`) VALUES
(1001, '张三', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138001', 'zhangsan@test.com', 'USER', 1),
(1002, '李四', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138002', 'lisi@test.com', 'USER', 1),
(1003, '王五', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138003', 'wangwu@test.com', 'USER', 1),
(1004, '赵六', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138004', 'zhaoliu@test.com', 'USER', 1),
(1005, '钱七', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138005', 'qianqi@test.com', 'USER', 1),
(1006, '孙八', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138006', 'sunba@test.com', 'USER', 1),
(1007, '周九', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138007', 'zhoujiu@test.com', 'USER', 1),
(1008, '吴十', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138008', 'wushi@test.com', 'USER', 1),
(1009, '郑十一', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138009', 'zhengshiyi@test.com', 'USER', 1),
(1010, '王十二', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138010', 'wangshier@test.com', 'USER', 1)
ON DUPLICATE KEY UPDATE `username`=`username`;

-- 2. 创建测试产品（如果不存在，假设产品ID 1-3已存在）
-- 如果产品不存在，请先创建产品

-- 3. 创建测试申请（如果不存在）
INSERT INTO `loan_application` (`id`, `user_id`, `product_id`, `apply_amount`, `apply_term`, `purpose`, `status`, `apply_time`, `loan_amount`, `loan_time`) VALUES
(1, 1001, 1, 50000.00, 12, '旅游贷款', 'DISBURSED', '2023-10-01 10:00:00', 50000.00, '2023-10-05 10:00:00'),
(2, 1002, 2, 120000.00, 24, '旅游贷款', 'DISBURSED', '2023-08-01 10:00:00', 120000.00, '2023-08-05 10:00:00'),
(3, 1003, 1, 30000.00, 12, '旅游贷款', 'DISBURSED', '2023-12-01 10:00:00', 30000.00, '2023-12-05 10:00:00'),
(4, 1004, 3, 15000.00, 6, '旅游贷款', 'DISBURSED', '2024-01-01 10:00:00', 15000.00, '2024-01-05 10:00:00'),
(5, 1005, 2, 80000.00, 18, '旅游贷款', 'DISBURSED', '2023-11-01 10:00:00', 80000.00, '2023-11-05 10:00:00'),
(6, 1006, 1, 150000.00, 24, '旅游贷款', 'DISBURSED', '2023-07-01 10:00:00', 150000.00, '2023-07-05 10:00:00'),
(7, 1007, 3, 40000.00, 12, '旅游贷款', 'DISBURSED', '2023-12-15 10:00:00', 40000.00, '2023-12-20 10:00:00'),
(8, 1008, 1, 20000.00, 6, '旅游贷款', 'DISBURSED', '2024-01-15 10:00:00', 20000.00, '2024-01-20 10:00:00'),
(9, 1009, 2, 90000.00, 18, '旅游贷款', 'DISBURSED', '2023-09-01 10:00:00', 90000.00, '2023-09-05 10:00:00'),
(10, 1010, 3, 35000.00, 12, '旅游贷款', 'DISBURSED', '2023-12-20 10:00:00', 35000.00, '2023-12-25 10:00:00')
ON DUPLICATE KEY UPDATE `user_id`=`user_id`;

-- 4. 插入贷后风险用户数据
INSERT INTO `overdue_risk_user` (`user_id`, `application_id`, `product_id`, `risk_level`, `overdue_amount`, `overdue_days`, `last_repayment_date`, `total_overdue_amount`, `overdue_count`, `status`) VALUES
(1001, 1, 1, '高风险', 50000.00, 45, '2024-01-15', 50000.00, 1, 'ACTIVE'),
(1002, 2, 2, '严重风险', 120000.00, 120, '2023-11-20', 120000.00, 2, 'ACTIVE'),
(1003, 3, 1, '中风险', 30000.00, 25, '2024-02-01', 30000.00, 1, 'ACTIVE'),
(1004, 4, 3, '低风险', 15000.00, 10, '2024-02-15', 15000.00, 1, 'ACTIVE'),
(1005, 5, 2, '高风险', 80000.00, 60, '2024-01-10', 80000.00, 1, 'ACTIVE'),
(1006, 6, 1, '严重风险', 150000.00, 150, '2023-10-05', 150000.00, 3, 'ACTIVE'),
(1007, 7, 3, '中风险', 40000.00, 30, '2024-01-25', 40000.00, 1, 'ACTIVE'),
(1008, 8, 1, '低风险', 20000.00, 8, '2024-02-18', 20000.00, 1, 'ACTIVE'),
(1009, 9, 2, '高风险', 90000.00, 75, '2023-12-20', 90000.00, 2, 'ACTIVE'),
(1010, 10, 3, '中风险', 35000.00, 28, '2024-01-28', 35000.00, 1, 'ACTIVE')
ON DUPLICATE KEY UPDATE `overdue_amount`=VALUES(`overdue_amount`), `overdue_days`=VALUES(`overdue_days`);



