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
 * 测评问卷实体类
 * 
 * @author Qingchun Team
 */
@Data
@TableName("evaluation_questionnaire")
public class EvaluationQuestionnaire implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("monthly_income")
    private String monthlyIncome;
    
    @TableField("repayment_capability")
    private String repaymentCapability;
    
    @TableField("credit_record")
    private String creditRecord;
    
    @TableField("travel_budget")
    private String travelBudget;
    
    @TableField("repayment_preference")
    private String repaymentPreference;
    
    @TableField("risk_tolerance")
    private String riskTolerance;
    
    @TableField("total_score")
    private Integer totalScore;
    
    @TableField("evaluation_level")
    private String evaluationLevel;
    
    @TableField("answers")
    private String answers;
    
    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

