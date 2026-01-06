package com.qingchun.travelloan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户证书实体类
 *
 * @author Qingchun Team
 */
@Data
@TableName("user_certificate")
public class UserCertificate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 证书ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 证书类型：JOB_PROOF-工作证明, ACADEMIC_HONOR-学业荣誉
     */
    @TableField("certificate_type")
    private String certificateType;

    /**
     * 来源ID（user_job_proof.id 或 academic_honor.id）
     */
    @TableField("source_id")
    private Long sourceId;

    /**
     * 证书名称
     */
    @TableField("certificate_name")
    private String certificateName;

    /**
     * 证书文件URL
     */
    @TableField("certificate_url")
    private String certificateUrl;

    /**
     * 证书描述
     */
    @TableField("description")
    private String description;

    /**
     * 审批状态：PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝
     */
    @TableField("status")
    private String status;

    /**
     * 审批人ID
     */
    @TableField("approver_id")
    private Long approverId;

    /**
     * 审批意见
     */
    @TableField("approval_opinion")
    private String approvalOpinion;

    /**
     * 审批时间
     */
    @TableField("approved_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvedAt;

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

