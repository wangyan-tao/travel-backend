package com.qingchun.travelloan.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 测评请求DTO
 * 
 * @author Qingchun Team
 */
@Data
public class EvaluationRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String monthlyIncome;
    private String repaymentCapability;
    private String creditRecord;
    private String travelBudget;
    private String repaymentPreference;
    private String riskTolerance;
    private Map<String, Object> answers;
}

