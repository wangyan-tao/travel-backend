package com.qingchun.travelloan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 还款计划实体类
 *
 * @author Qingchun Team
 */
@Data
@TableName("repayment_plan")
public class RepaymentPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 计划ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 申请ID
     */
    @TableField("application_id")
    private Long applicationId;

    /**
     * 期数
     */
    @TableField("period_number")
    private Integer periodNumber;

    /**
     * 应还日期
     */
    @TableField("due_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    /**
     * 本金金额
     */
    @TableField("principal_amount")
    private BigDecimal principalAmount;

    /**
     * 利息金额
     */
    @TableField("interest_amount")
    private BigDecimal interestAmount;

    /**
     * 应还总额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 状态：PENDING-待还款, PAID-已还款, OVERDUE-已逾期
     */
    @TableField("status")
    private String status;

    /**
     * 创建时间
     */
    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
