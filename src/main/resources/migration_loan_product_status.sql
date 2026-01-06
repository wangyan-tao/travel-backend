-- 贷款产品status字段类型迁移脚本
-- 将status字段从TINYINT改为VARCHAR，以支持ACTIVE/INACTIVE状态值
-- 执行此脚本前请先备份数据库

USE travel_loan;

-- 1. 修改status字段类型为VARCHAR
ALTER TABLE `loan_product`
MODIFY COLUMN `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-上架, INACTIVE-下架';

-- 2. 更新现有数据：将1转换为ACTIVE，0转换为INACTIVE
UPDATE `loan_product` SET `status` = 'ACTIVE' WHERE `status` = '1' OR `status` = 1;
UPDATE `loan_product` SET `status` = 'INACTIVE' WHERE `status` = '0' OR `status` = 0;

-- 3. 修改category字段名（如果数据库中使用的是product_type，需要先重命名）
-- 注意：如果数据库中已经是category字段，可以跳过此步骤
-- ALTER TABLE `loan_product` CHANGE COLUMN `product_type` `category` VARCHAR(50) NOT NULL COMMENT '产品分类';

