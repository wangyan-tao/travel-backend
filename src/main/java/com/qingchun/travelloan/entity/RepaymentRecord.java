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
 * 还款记录实体类
 *
 * @author Qingchun Team
 */
@Data
@TableName("repayment_record")
public class RepaymentRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 计划ID
     */
    @TableField("plan_id")
    private Long planId;

    /**
     * 申请ID
     */
    @TableField("application_id")
    private Long applicationId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 还款金额
     */
    @TableField("payment_amount")
    private BigDecimal paymentAmount;

    /**
     * 还款时间
     */
    @TableField("payment_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentTime;

    /**
     * 支付方式
     */
    @TableField("payment_method")
    private String paymentMethod;

    /**
     * 交易流水号
     */
    @TableField("transaction_id")
    private String transactionId;

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
