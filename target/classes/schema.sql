-- 青春旅贷数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS travel_loan DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE travel_loan;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    `phone` VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色（USER/ADMIN）',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0禁用 1正常）',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (`username`),
    INDEX idx_phone (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `user_identity` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `id_card` VARCHAR(18) NOT NULL COMMENT '身份证号',
    `student_id` VARCHAR(50) NOT NULL COMMENT '学号',
    `university` VARCHAR(100) NOT NULL COMMENT '所在大学',
    `major` VARCHAR(100) NOT NULL COMMENT '专业',
    `grade` VARCHAR(20) COMMENT '年级：大一/大二/大三/大四',
    `id_card_front_url` MEDIUMTEXT COMMENT '身份证正面照片Base64',
    `id_card_back_url` MEDIUMTEXT COMMENT '身份证反面照片Base64',
    `student_card_url` MEDIUMTEXT COMMENT '学生证照片Base64',
    `verification_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '核验状态：PENDING-待核验, VERIFIED-已核验, FAILED-核验失败',
    `verified_at` DATETIME COMMENT '核验时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (`user_id`),
    INDEX idx_id_card (`id_card`),
    INDEX idx_student_id (`student_id`),
    INDEX idx_university (`university`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户实名信息表';

-- 担保人信息表
CREATE TABLE IF NOT EXISTS `guarantor` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `name` VARCHAR(50) NOT NULL COMMENT '担保人姓名',
    `id_card_no` VARCHAR(18) NOT NULL COMMENT '担保人身份证号',
    `id_card_front_url` VARCHAR(500) COMMENT '身份证正面照片URL',
    `id_card_back_url` VARCHAR(500) COMMENT '身份证反面照片URL',
    `relationship` VARCHAR(20) NOT NULL COMMENT '与申请人关系',
    `phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
    `work_unit` VARCHAR(200) COMMENT '工作单位',
    `position` VARCHAR(100) COMMENT '职务',
    `agreement_signed` TINYINT NOT NULL DEFAULT 0 COMMENT '是否签署知情同意书（0否 1是）',
    `agreement_signed_at` DATETIME COMMENT '签署时间',
    `agreement_url` VARCHAR(500) COMMENT '知情同意书URL',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (`user_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='担保人信息表';

-- 位置信息表
CREATE TABLE IF NOT EXISTS `user_location` ( 
     `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID', 
     `user_id` BIGINT NOT NULL UNIQUE COMMENT '用户ID', 
     `current_city` VARCHAR(50) COMMENT '当前所在城市', 
     `school_city` VARCHAR(50) COMMENT '学校所在城市', 
     `latitude` DECIMAL(10, 7) COMMENT '纬度', 
     `longitude` DECIMAL(10, 7) COMMENT '经度', 
     `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', 
     `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', 
     INDEX idx_user_id (`user_id`), 
     INDEX idx_current_city (`current_city`), 
     INDEX idx_school_city (`school_city`), 
     FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE 
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='位置信息表';

-- 贷款产品表
CREATE TABLE IF NOT EXISTS `loan_product` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '产品ID',
    `product_name` VARCHAR(100) NOT NULL COMMENT '产品名称',
    `product_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '产品编码',
    `category` VARCHAR(50) NOT NULL COMMENT '产品分类（短途游贷/毕业旅行贷/研学游贷/跨省游贷）',
    `min_amount` DECIMAL(10, 2) NOT NULL COMMENT '最低额度',
    `max_amount` DECIMAL(10, 2) NOT NULL COMMENT '最高额度',
    `interest_rate` DECIMAL(5, 4) NOT NULL COMMENT '年化利率',
    `interest_type` VARCHAR(50) NOT NULL COMMENT '利率类型（低息免息/固定利率/分期低手续费）',
    `min_period` INT NOT NULL COMMENT '最短分期期限（月）',
    `max_period` INT NOT NULL COMMENT '最长分期期限（月）',
    `apply_condition` TEXT COMMENT '申请条件',
    `approval_time` VARCHAR(50) COMMENT '审批时效',
    `arrival_time` VARCHAR(50) COMMENT '到账时间',
    `penalty_description` TEXT COMMENT '违约金说明',
    `student_discount` TEXT COMMENT '学生专属优惠',
    `institution_name` VARCHAR(200) NOT NULL COMMENT '持牌机构名称',
    `institution_license` VARCHAR(100) COMMENT '机构牌照号',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0下架 1上架）',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (`category`),
    INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='贷款产品表';

-- 贷款申请表
CREATE TABLE IF NOT EXISTS `loan_application` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
    `application_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '申请编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '产品ID',
    `apply_amount` DECIMAL(10, 2) NOT NULL COMMENT '申请金额',
    `apply_period` INT NOT NULL COMMENT '申请期限（月）',
    `loan_purpose` VARCHAR(500) COMMENT '贷款用途',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '申请状态（PENDING待审核/APPROVED已批准/REJECTED已拒绝/CANCELLED已取消）',
    `approved_amount` DECIMAL(10, 2) COMMENT '批准金额',
    `approved_period` INT COMMENT '批准期限（月）',
    `approval_opinion` TEXT COMMENT '审批意见',
    `approved_by` BIGINT COMMENT '审批人ID',
    `approved_at` DATETIME COMMENT '审批时间',
    `disbursed_at` DATETIME COMMENT '放款时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (`user_id`),
    INDEX idx_product_id (`product_id`),
    INDEX idx_status (`status`),
    INDEX idx_application_no (`application_no`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`product_id`) REFERENCES `loan_product`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='贷款申请表';

-- ============================================ 
 -- 7. 还款计划表 
 -- ============================================ 
 CREATE TABLE IF NOT EXISTS `repayment_plan` ( 
     `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '计划ID', 
     `application_id` BIGINT NOT NULL COMMENT '申请ID', 
     `period_number` INT NOT NULL COMMENT '期数', 
     `due_date` DATE NOT NULL COMMENT '应还日期', 
     `principal_amount` DECIMAL(10, 2) NOT NULL COMMENT '本金金额', 
     `interest_amount` DECIMAL(10, 2) NOT NULL COMMENT '利息金额', 
     `total_amount` DECIMAL(10, 2) NOT NULL COMMENT '应还总额', 
     `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待还款, PAID-已还款, OVERDUE-已逾期', 
     `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', 
     `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', 
     INDEX idx_application_id (`application_id`), 
     INDEX idx_due_date (`due_date`), 
     INDEX idx_status (`status`), 
     FOREIGN KEY (`application_id`) REFERENCES `loan_application`(`id`) ON DELETE CASCADE 
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='还款计划表'; 
 
 -- ============================================ 
 -- 8. 还款记录表 
 -- ============================================ 
 CREATE TABLE IF NOT EXISTS `repayment_record` ( 
     `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID', 
     `plan_id` BIGINT NOT NULL COMMENT '计划ID', 
     `application_id` BIGINT NOT NULL COMMENT '申请ID', 
     `user_id` BIGINT NOT NULL COMMENT '用户ID', 
     `payment_amount` DECIMAL(10, 2) NOT NULL COMMENT '还款金额', 
     `payment_time` DATETIME NOT NULL COMMENT '还款时间', 
     `payment_method` VARCHAR(50) COMMENT '支付方式', 
     `transaction_id` VARCHAR(100) COMMENT '交易流水号', 
     `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', 
     `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', 
     INDEX idx_plan_id (`plan_id`), 
     INDEX idx_application_id (`application_id`), 
     INDEX idx_user_id (`user_id`), 
     INDEX idx_payment_time (`payment_time`), 
     FOREIGN KEY (`plan_id`) REFERENCES `repayment_plan`(`id`), 
     FOREIGN KEY (`application_id`) REFERENCES `loan_application`(`id`), 
     FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) 
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='还款记录表'; 
 
 -- ============================================ 
 -- 9. 兼职商铺表 
 -- ============================================ 
 CREATE TABLE IF NOT EXISTS `part_time_job` ( 
     `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商铺ID', 
     `job_title` VARCHAR(100) NOT NULL COMMENT '岗位名称', 
     `company_name` VARCHAR(200) NOT NULL COMMENT '商铺/公司名称', 
     `city` VARCHAR(50) NOT NULL COMMENT '所在城市', 
     `district` VARCHAR(50) COMMENT '所在区域', 
     `address` VARCHAR(200) COMMENT '详细地址', 
     `salary_range` VARCHAR(50) COMMENT '薪资范围', 
     `job_type` VARCHAR(50) NOT NULL COMMENT '岗位类型', 
     `contact_phone` VARCHAR(20) COMMENT '联系电话', 
     `description` TEXT COMMENT '岗位描述', 
     `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-上架, INACTIVE-下架', 
     `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', 
     `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', 
     INDEX idx_city (`city`), 
     INDEX idx_job_type (`job_type`), 
     INDEX idx_status (`status`) 
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='兼职商铺表'; 
 
 -- ============================================ 
 -- 10. 用户兼职证明表 
 -- ============================================ 
 CREATE TABLE IF NOT EXISTS `user_job_proof` ( 
     `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '证明ID', 
     `user_id` BIGINT NOT NULL COMMENT '用户ID', 
     `job_id` BIGINT COMMENT '关联兼职ID', 
     `proof_type` VARCHAR(50) NOT NULL COMMENT '证明类型：工作证明/工资流水/劳动合同', 
     `proof_url` VARCHAR(500) COMMENT '证明材料URL', 
     `monthly_income` DECIMAL(10, 2) COMMENT '月收入', 
     `start_date` DATE NOT NULL COMMENT '开始日期', 
     `end_date` DATE COMMENT '结束日期', 
     `verification_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '核验状态：PENDING-待核验, VERIFIED-已核验, FAILED-核验失败', 
     `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', 
     `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', 
     INDEX idx_user_id (`user_id`), 
     INDEX idx_job_id (`job_id`), 
     FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE, 
     FOREIGN KEY (`job_id`) REFERENCES `part_time_job`(`id`) 
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户兼职证明表'; 
 
 -- ============================================ 
 -- 11. 学业荣誉证明表 
 -- ============================================ 
 CREATE TABLE IF NOT EXISTS `academic_honor` ( 
     `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '证明ID', 
     `user_id` BIGINT NOT NULL COMMENT '用户ID', 
     `honor_type` VARCHAR(50) NOT NULL COMMENT '证明类型：奖学金/竞赛获奖/优秀学生/科研成果/社会实践', 
     `honor_name` VARCHAR(200) NOT NULL COMMENT '荣誉名称', 
     `award_level` VARCHAR(50) COMMENT '获奖级别：国家级/省级/校级/院级', 
     `award_date` DATE COMMENT '获奖日期', 
     `certificate_url` VARCHAR(500) COMMENT '证书URL', 
     `issuing_organization` VARCHAR(200) COMMENT '颁发机构', 
     `verification_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '核验状态：PENDING-待核验, VERIFIED-已核验, FAILED-核验失败', 
     `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', 
     `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', 
     INDEX idx_user_id (`user_id`), 
     INDEX idx_honor_type (`honor_type`), 
     FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE 
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学业荣誉证明表';

-- ============================================ 
 -- 12. 通知消息表 
 -- ============================================ 
 CREATE TABLE IF NOT EXISTS `notification` ( 
     `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID', 
     `user_id` BIGINT NOT NULL COMMENT '用户ID', 
     `title` VARCHAR(200) NOT NULL COMMENT '消息标题', 
     `content` TEXT NOT NULL COMMENT '消息内容', 
     `type` VARCHAR(50) NOT NULL COMMENT '消息类型', 
     `is_read` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已读', 
     `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', 
     `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', 
     INDEX idx_user_id (`user_id`), 
     INDEX idx_type (`type`), 
     INDEX idx_is_read (`is_read`), 
     INDEX idx_created_at (`created_at`), 
     FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE 
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知消息表';

-- 插入默认管理员账号（密码：admin123）
INSERT INTO `user` (`username`, `password`, `phone`, `email`, `role`) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138000', 'admin@qingchun.com', 'ADMIN');
