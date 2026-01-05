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
 * 贷款申请实体类
 * 
 * @author Qingchun Team
 */
@Data
@TableName("loan_application")
public class LoanApplication implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("product_id")
    private Long productId;
    @TableField("apply_amount")
    private BigDecimal applyAmount;
    @TableField("apply_term")
    private Integer applyTerm;
    @TableField("purpose")
    private String purpose;
    @TableField("status")
    private String status;
    @TableField("apply_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime applyTime;
    @TableField("approve_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approveTime;
    @TableField("reject_reason")
    private String rejectReason;
    @TableField("approver_id")
    private Long approverId;
    @TableField("loan_amount")
    private BigDecimal loanAmount;
    @TableField("loan_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loanTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
