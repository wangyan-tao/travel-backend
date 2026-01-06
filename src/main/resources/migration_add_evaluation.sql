-- 测评功能数据库迁移脚本
-- 执行此脚本前请先备份数据库

USE travel_loan;

-- 1. 为user_identity表添加测评相关字段
ALTER TABLE `user_identity`
ADD COLUMN `evaluation_completed` TINYINT NOT NULL DEFAULT 0 COMMENT '是否完成测评（0否 1是）' AFTER `verified_at`,
ADD COLUMN `evaluation_score` INT COMMENT '测评总分' AFTER `evaluation_completed`,
ADD COLUMN `evaluation_level` VARCHAR(20) COMMENT '测评等级：R1/R2/R3/R4/R5' AFTER `evaluation_score`,
ADD COLUMN `evaluation_completed_at` DATETIME COMMENT '测评完成时间' AFTER `evaluation_level`,
ADD INDEX `idx_evaluation_completed` (`evaluation_completed`);

-- 2. 创建测评问卷表
CREATE TABLE IF NOT EXISTS `evaluation_questionnaire` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    `monthly_income` VARCHAR(50) COMMENT '月收入范围',
    `repayment_capability` VARCHAR(50) COMMENT '还款能力评估',
    `credit_record` VARCHAR(50) COMMENT '信用记录',
    `travel_budget` VARCHAR(50) COMMENT '旅游预算',
    `repayment_preference` VARCHAR(50) COMMENT '还款期限偏好',
    `risk_tolerance` VARCHAR(50) COMMENT '风险承受能力',
    `total_score` INT NOT NULL COMMENT '测评总分',
    `evaluation_level` VARCHAR(20) NOT NULL COMMENT '测评等级：R1/R2/R3/R4/R5',
    `answers` JSON COMMENT '详细答案（JSON格式）',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_evaluation_level` (`evaluation_level`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测评问卷表';

