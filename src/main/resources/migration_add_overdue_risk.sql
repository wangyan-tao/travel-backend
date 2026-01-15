-- 贷后风险用户表
CREATE TABLE IF NOT EXISTS `overdue_risk_user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `application_id` BIGINT NOT NULL COMMENT '申请ID',
    `product_id` BIGINT NOT NULL COMMENT '产品ID',
    `risk_level` VARCHAR(20) NOT NULL COMMENT '风险等级：低风险/中风险/高风险/严重风险',
    `overdue_amount` DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '逾期金额',
    `overdue_days` INT NOT NULL DEFAULT 0 COMMENT '逾期天数',
    `last_repayment_date` DATE COMMENT '最后还款日期',
    `total_overdue_amount` DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '累计逾期金额',
    `overdue_count` INT NOT NULL DEFAULT 0 COMMENT '逾期次数',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-有效, RESOLVED-已解决',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (`user_id`),
    INDEX idx_application_id (`application_id`),
    INDEX idx_product_id (`product_id`),
    INDEX idx_risk_level (`risk_level`),
    INDEX idx_overdue_days (`overdue_days`),
    INDEX idx_status (`status`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`application_id`) REFERENCES `loan_application`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`product_id`) REFERENCES `loan_product`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='贷后风险用户表';

-- 插入测试数据（基于Excel表格结构）
-- 注意：这些数据需要先有对应的用户、产品和申请记录
-- 假设已有用户ID 1001-1010，产品ID 1-3，申请ID 1-10

-- 插入贷后风险用户数据
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
(1010, 10, 3, '中风险', 35000.00, 28, '2024-01-28', 35000.00, 1, 'ACTIVE');



