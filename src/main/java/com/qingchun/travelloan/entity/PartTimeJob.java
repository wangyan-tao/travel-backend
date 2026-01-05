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
 * 兼职商铺实体类
 *
 * @author Qingchun Team
 */
@Data
@TableName("part_time_job")
public class PartTimeJob implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商铺ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 岗位名称
     */
    @TableField("job_title")
    private String jobTitle;

    /**
     * 商铺/公司名称
     */
    @TableField("company_name")
    private String companyName;

    /**
     * 所在城市
     */
    @TableField("city")
    private String city;

    /**
     * 所在区域
     */
    @TableField("district")
    private String district;

    /**
     * 详细地址
     */
    @TableField("address")
    private String address;

    /**
     * 薪资范围
     */
    @TableField("salary_range")
    private String salaryRange;

    /**
     * 岗位类型
     */
    @TableField("job_type")
    private String jobType;

    /**
     * 联系电话
     */
    @TableField("contact_phone")
    private String contactPhone;

    /**
     * 岗位描述
     */
    @TableField("description")
    private String description;

    /**
     * 状态：ACTIVE-上架, INACTIVE-下架
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
