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
 * 担保人信息实体类
 *
 * @author Qingchun Team
 */
@Data
@TableName("guarantor")
public class Guarantor implements Serializable {

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
     * 担保人姓名
     */
    @TableField("name")
    private String name;

    /**
     * 担保人身份证号
     */
    @TableField("id_card")
    private String idCard;

    /**
     * 与申请人关系
     */
    @TableField("relationship")
    private String relationship;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 工作单位
     */
    @TableField("work_unit")
    private String workUnit;

    /**
     * 身份证正面照片URL
     */
    @TableField("id_card_front_url")
    private String idCardFrontUrl;

    /**
     * 身份证反面照片URL
     */
    @TableField("id_card_back_url")
    private String idCardBackUrl;

    /**
     * 是否签署知情同意书
     */
    @TableField("agreement_signed")
    private Boolean agreementSigned;

    /**
     * 签署时间
     */
    @TableField("agreement_signed_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime agreementSignedAt;

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
