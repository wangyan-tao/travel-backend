-- 添加省份字段到user_location表
-- 如果字段已存在，此语句会失败，可以忽略

ALTER TABLE `user_location` 
ADD COLUMN `current_province` VARCHAR(50) COMMENT '当前所在省份' AFTER `user_id`;

-- 添加索引（可选）
ALTER TABLE `user_location` 
ADD INDEX `idx_current_province` (`current_province`);

