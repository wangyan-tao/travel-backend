package com.qingchun.travelloan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("user_identity")
public class UserIdentity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("real_name")
    private String realName;
    
    @TableField("id_card")
    private String idCard;
    
    @TableField("student_id")
    private String studentId;
    
    @TableField("university")
    private String university;
    
    @TableField("major")
    private String major;
    
    @TableField("grade")
    private String grade;
    
    @TableField("id_card_front_url")
    private String idCardFrontUrl;
    
    @TableField("id_card_back_url")
    private String idCardBackUrl;
    
    @TableField("student_card_url")
    private String studentCardUrl;
    
    @TableField("verification_status")
    private String verificationStatus;
    
    @TableField("verified_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime verifiedAt;
    
    @TableField("evaluation_completed")
    private Boolean evaluationCompleted;
    
    @TableField("evaluation_score")
    private Integer evaluationScore;
    
    @TableField("evaluation_level")
    private String evaluationLevel;
    
    @TableField("evaluation_completed_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime evaluationCompletedAt;
    
    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 城市信息（仅用于接收前端数据，不映射到数据库）
     */
    @TableField(exist = false)
    @JsonProperty("city")
    private String city;
}
