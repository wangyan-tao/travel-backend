package com.qingchun.travelloan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 贷款产品实体类
 * 
 * @author Qingchun Team
 */
@Data
@TableName("loan_product")
public class LoanProduct implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("product_name")
    private String productName;

    @TableField("product_code")
    private String productCode;

    @TableField("product_type")
    private String productType;

    @TableField("min_amount")
    private BigDecimal minAmount;

    @TableField("max_amount")
    private BigDecimal maxAmount;

    @TableField("interest_rate")
    private BigDecimal interestRate;

    @TableField("interest_type")
    private String interestType;

    @TableField("min_term")
    private Integer minTerm;

    @TableField("max_term")
    private Integer maxTerm;

    @TableField("institution_name")
    private String institutionName;

    @TableField("application_conditions")
    private String applicationConditions;

    @TableField("approval_time")
    private String approvalTime;

    @TableField("penalty_rate")
    private BigDecimal penaltyRate;

    @TableField("description")
    private String description;

    @TableField("status")
    private String status;
    
    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
