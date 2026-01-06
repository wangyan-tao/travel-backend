package com.qingchun.travelloan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingchun.travelloan.dto.EvaluationRequest;
import com.qingchun.travelloan.entity.EvaluationQuestionnaire;
import com.qingchun.travelloan.entity.UserIdentity;
import com.qingchun.travelloan.exception.BusinessException;
import com.qingchun.travelloan.mapper.EvaluationQuestionnaireMapper;
import com.qingchun.travelloan.mapper.UserIdentityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 测评服务类
 * 
 * @author Qingchun Team
 */
@Service
public class EvaluationService {

    @Autowired
    private EvaluationQuestionnaireMapper evaluationQuestionnaireMapper;

    @Autowired
    private UserIdentityMapper userIdentityMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public EvaluationQuestionnaire submitEvaluation(EvaluationRequest request, Long userId) {
        if (request == null) {
            throw new BusinessException("测评数据不能为空");
        }

        // 计算总分
        int totalScore = calculateScore(request);
        
        // 确定等级（R1-R5）
        String level = determineLevel(totalScore);

        // 保存或更新测评问卷
        EvaluationQuestionnaire evaluation = evaluationQuestionnaireMapper.selectOne(
                new QueryWrapper<EvaluationQuestionnaire>().eq("user_id", userId)
        );

        if (evaluation == null) {
            evaluation = new EvaluationQuestionnaire();
            evaluation.setUserId(userId);
        }

        evaluation.setMonthlyIncome(request.getMonthlyIncome());
        evaluation.setRepaymentCapability(request.getRepaymentCapability());
        evaluation.setCreditRecord(request.getCreditRecord());
        evaluation.setTravelBudget(request.getTravelBudget());
        evaluation.setRepaymentPreference(request.getRepaymentPreference());
        evaluation.setRiskTolerance(request.getRiskTolerance());
        evaluation.setTotalScore(totalScore);
        evaluation.setEvaluationLevel(level);

        // 保存详细答案
        try {
            if (request.getAnswers() != null) {
                evaluation.setAnswers(objectMapper.writeValueAsString(request.getAnswers()));
            }
        } catch (Exception e) {
            throw new BusinessException("保存测评答案失败");
        }

        if (evaluation.getId() == null) {
            evaluationQuestionnaireMapper.insert(evaluation);
        } else {
            evaluationQuestionnaireMapper.updateById(evaluation);
        }

        // 更新user_identity表的测评信息
        UserIdentity identity = userIdentityMapper.selectOne(
                new QueryWrapper<UserIdentity>().eq("user_id", userId)
        );

        if (identity != null) {
            identity.setEvaluationCompleted(true);
            identity.setEvaluationScore(totalScore);
            identity.setEvaluationLevel(level);
            identity.setEvaluationCompletedAt(LocalDateTime.now());
            userIdentityMapper.updateById(identity);
        }

        return evaluation;
    }

    public EvaluationQuestionnaire getEvaluation(Long userId) {
        return evaluationQuestionnaireMapper.selectOne(
                new QueryWrapper<EvaluationQuestionnaire>().eq("user_id", userId)
        );
    }

    public boolean hasCompletedEvaluation(Long userId) {
        UserIdentity identity = userIdentityMapper.selectOne(
                new QueryWrapper<UserIdentity>().eq("user_id", userId)
        );
        return identity != null && Boolean.TRUE.equals(identity.getEvaluationCompleted());
    }

    /**
     * 计算测评总分
     */
    private int calculateScore(EvaluationRequest request) {
        int score = 0;
        
        // 月收入评分 (0-20分)
        score += getIncomeScore(request.getMonthlyIncome());
        
        // 还款能力评分 (0-20分)
        score += getRepaymentCapabilityScore(request.getRepaymentCapability());
        
        // 信用记录评分 (0-20分)
        score += getCreditRecordScore(request.getCreditRecord());
        
        // 旅游预算评分 (0-15分)
        score += getTravelBudgetScore(request.getTravelBudget());
        
        // 还款期限偏好评分 (0-15分)
        score += getRepaymentPreferenceScore(request.getRepaymentPreference());
        
        // 风险承受能力评分 (0-10分)
        score += getRiskToleranceScore(request.getRiskTolerance());
        
        return score;
    }

    private int getIncomeScore(String income) {
        if (income == null) return 0;
        switch (income) {
            case "1000以下": return 5;
            case "1000-2000": return 10;
            case "2000-3000": return 15;
            case "3000以上": return 20;
            default: return 0;
        }
    }

    private int getRepaymentCapabilityScore(String capability) {
        if (capability == null) return 0;
        switch (capability) {
            case "完全无法还款": return 0;
            case "还款困难": return 5;
            case "可以还款": return 15;
            case "轻松还款": return 20;
            default: return 0;
        }
    }

    private int getCreditRecordScore(String record) {
        if (record == null) return 0;
        switch (record) {
            case "有逾期记录": return 0;
            case "信用一般": return 10;
            case "信用良好": return 15;
            case "信用优秀": return 20;
            default: return 0;
        }
    }

    private int getTravelBudgetScore(String budget) {
        if (budget == null) return 0;
        switch (budget) {
            case "1000以下": return 5;
            case "1000-2000": return 10;
            case "2000-3000": return 12;
            case "3000以上": return 15;
            default: return 0;
        }
    }

    private int getRepaymentPreferenceScore(String preference) {
        if (preference == null) return 0;
        switch (preference) {
            case "1-2个月": return 5;
            case "3-6个月": return 10;
            case "6-12个月": return 12;
            case "12个月以上": return 15;
            default: return 0;
        }
    }

    private int getRiskToleranceScore(String tolerance) {
        if (tolerance == null) return 0;
        switch (tolerance) {
            case "保守型": return 3;
            case "稳健型": return 6;
            case "积极型": return 8;
            case "激进型": return 10;
            default: return 0;
        }
    }

    /**
     * 确定测评等级
     * R1 低风险：85-100分
     * R2 中低风险：70-84分
     * R3 中风险：55-69分
     * R4 中高风险：40-54分
     * R5 高风险：0-39分
     */
    private String determineLevel(int score) {
        if (score >= 85) {
            return "R1";
        } else if (score >= 70) {
            return "R2";
        } else if (score >= 55) {
            return "R3";
        } else if (score >= 40) {
            return "R4";
        } else {
            return "R5";
        }
    }
}

