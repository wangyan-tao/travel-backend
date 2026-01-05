package com.qingchun.travelloan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学业荣誉证明实体类
 *
 * @author Qingchun Team
 */
@Data
@TableName("academic_honor")
public class AcademicHonor implements Serializable {

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
     * 证明类型：奖学金/竞赛获奖/优秀学生/科研成果/社会实践
     */
    @TableField("honor_type")
    private String honorType;

    /**
     * 荣誉名称
     */
    @TableField("honor_name")
    private String honorName;

    /**
     * 获奖级别：国家级/省级/校级/院级
     */
    @TableField("award_level")
    private String awardLevel;

    /**
     * 获奖日期
     */
    @TableField("award_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate awardDate;

    /**
     * 证书URL
     */
    @TableField("certificate_url")
    private String certificateUrl;

    /**
     * 颁发机构
     */
    @TableField("issuing_organization")
    private String issuingOrganization;

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
