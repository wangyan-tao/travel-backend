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
 * 贷后风险用户实体类
 *
 * @author Qingchun Team
 */
@Data
@TableName("overdue_risk_user")
public class OverdueRiskUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 申请ID
     */
    @TableField("application_id")
    private Long applicationId;

    /**
     * 产品ID
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 风险等级：低风险/中风险/高风险/严重风险
     */
    @TableField("risk_level")
    private String riskLevel;

    /**
     * 逾期金额
     */
    @TableField("overdue_amount")
    private BigDecimal overdueAmount;

    /**
     * 逾期天数
     */
    @TableField("overdue_days")
    private Integer overdueDays;

    /**
     * 最后还款日期
     */
    @TableField("last_repayment_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastRepaymentDate;

    /**
     * 累计逾期金额
     */
    @TableField("total_overdue_amount")
    private BigDecimal totalOverdueAmount;

    /**
     * 逾期次数
     */
    @TableField("overdue_count")
    private Integer overdueCount;

    /**
     * 状态：ACTIVE-有效, RESOLVED-已解决
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

