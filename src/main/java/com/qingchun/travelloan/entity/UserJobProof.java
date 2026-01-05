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
 * 用户兼职证明实体类
 *
 * @author Qingchun Team
 */
@Data
@TableName("user_job_proof")
public class UserJobProof implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 证明ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 关联兼职ID
     */
    @TableField("job_id")
    private Long jobId;

    /**
     * 证明类型：工作证明/工资流水/劳动合同
     */
    @TableField("proof_type")
    private String proofType;

    /**
     * 证明材料URL
     */
    @TableField("proof_url")
    private String proofUrl;

    /**
     * 月收入
     */
    @TableField("monthly_income")
    private BigDecimal monthlyIncome;

    /**
     * 开始日期
     */
    @TableField("start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @TableField("end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    /**
     * 核验状态：PENDING-待核验, VERIFIED-已核验, FAILED-核验失败
     */
    @TableField("verification_status")
    private String verificationStatus;

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
