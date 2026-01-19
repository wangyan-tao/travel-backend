-- 移除学生证、学号、专业、年级字段的数据库迁移脚本
-- 执行此脚本前请先备份数据库
-- 此脚本将 student_id、major 字段改为允许 NULL，以适应新的业务需求

USE travel_loan;

-- 1. 修改 student_id 字段，允许 NULL
ALTER TABLE `user_identity` 
MODIFY COLUMN `student_id` VARCHAR(50) NULL COMMENT '学号（已废弃）';

-- 2. 修改 major 字段，允许 NULL
ALTER TABLE `user_identity` 
MODIFY COLUMN `major` VARCHAR(100) NULL COMMENT '专业（已废弃）';

-- 3. grade 字段已经允许 NULL，无需修改

-- 4. student_card_url 字段已经允许 NULL，无需修改

-- 5. 更新表注释
ALTER TABLE `user_identity` COMMENT = '用户实名信息表（已移除学生证、学号、专业、年级字段）';
