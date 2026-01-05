package com.qingchun.travelloan.entity;

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
public class LoanApplication implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String applicationNo;
    private Long userId;
    private Long productId;
    private BigDecimal applyAmount;
    private Integer applyPeriod;
    private String loanPurpose;
    private String status;
    private BigDecimal approvedAmount;
    private Integer approvedPeriod;
    private String approvalOpinion;
    private Long approvedBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime disbursedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
