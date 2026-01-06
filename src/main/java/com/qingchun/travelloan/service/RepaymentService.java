package com.qingchun.travelloan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingchun.travelloan.entity.LoanApplication;
import com.qingchun.travelloan.entity.RepaymentPlan;
import com.qingchun.travelloan.entity.RepaymentRecord;
import com.qingchun.travelloan.exception.BusinessException;
import com.qingchun.travelloan.mapper.LoanApplicationMapper;
import com.qingchun.travelloan.mapper.RepaymentPlanMapper;
import com.qingchun.travelloan.mapper.RepaymentRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 还款管理服务类
 *
 * @author Qingchun Team
 */
@Service
public class RepaymentService {

    @Autowired
    private RepaymentPlanMapper repaymentPlanMapper;

    @Autowired
    private RepaymentRecordMapper repaymentRecordMapper;

    @Autowired
    private LoanApplicationMapper loanApplicationMapper;

    /**
     * 获取用户的还款概览
     */
    public RepaymentOverviewDTO getRepaymentOverview(Long userId) {
        // 获取用户所有已批准的贷款申请
        QueryWrapper<LoanApplication> applicationWrapper = new QueryWrapper<>();
        applicationWrapper.eq("user_id", userId)
                .eq("status", "APPROVED")
                .isNotNull("loan_amount");
        List<LoanApplication> applications = loanApplicationMapper.selectList(applicationWrapper);

        RepaymentOverviewDTO overview = new RepaymentOverviewDTO();
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal paidAmount = BigDecimal.ZERO;
        BigDecimal remainingAmount = BigDecimal.ZERO;
        int totalPeriods = 0;
        int paidPeriods = 0;
        RepaymentPlan nextPlan = null;
        LocalDate nextDueDate = null;

        for (LoanApplication application : applications) {
            // 获取该申请的所有还款计划
            QueryWrapper<RepaymentPlan> planWrapper = new QueryWrapper<>();
            planWrapper.eq("application_id", application.getId())
                    .orderByAsc("period_number");
            List<RepaymentPlan> plans = repaymentPlanMapper.selectList(planWrapper);

            if (plans.isEmpty()) {
                continue;
            }

            totalPeriods += plans.size();

            for (RepaymentPlan plan : plans) {
                totalAmount = totalAmount.add(plan.getTotalAmount());
                if ("PAID".equals(plan.getStatus())) {
                    paidAmount = paidAmount.add(plan.getTotalAmount());
                    paidPeriods++;
                } else {
                    remainingAmount = remainingAmount.add(plan.getTotalAmount());
                    // 找到最近的待还款计划
                    if (nextPlan == null || plan.getDueDate().isBefore(nextDueDate)) {
                        if ("PENDING".equals(plan.getStatus()) || "OVERDUE".equals(plan.getStatus())) {
                            nextPlan = plan;
                            nextDueDate = plan.getDueDate();
                        }
                    }
                }
            }
        }

        overview.setTotalAmount(totalAmount);
        overview.setPaidAmount(paidAmount);
        overview.setRemainingAmount(remainingAmount);
        overview.setTotalPeriods(totalPeriods);
        overview.setPaidPeriods(paidPeriods);
        if (nextPlan != null) {
            overview.setNextPaymentDate(nextPlan.getDueDate());
            overview.setNextPaymentAmount(nextPlan.getTotalAmount());
        }

        return overview;
    }

    /**
     * 获取用户的所有还款计划
     */
    public List<RepaymentPlanDTO> getUserRepaymentPlans(Long userId) {
        // 获取用户所有已批准的贷款申请
        QueryWrapper<LoanApplication> applicationWrapper = new QueryWrapper<>();
        applicationWrapper.eq("user_id", userId)
                .eq("status", "APPROVED");
        List<LoanApplication> applications = loanApplicationMapper.selectList(applicationWrapper);

        return applications.stream()
                .flatMap(app -> {
                    QueryWrapper<RepaymentPlan> planWrapper = new QueryWrapper<>();
                    planWrapper.eq("application_id", app.getId())
                            .orderByAsc("period_number");
                    List<RepaymentPlan> plans = repaymentPlanMapper.selectList(planWrapper);
                    return plans.stream().map(plan -> {
                        RepaymentPlanDTO dto = new RepaymentPlanDTO();
                        dto.setPlan(plan);
                        dto.setApplicationId(app.getId());
                        dto.setLoanAmount(app.getLoanAmount() != null ? app.getLoanAmount() : app.getApplyAmount());
                        return dto;
                    });
                })
                .sorted((a, b) -> {
                    // 按应还日期排序
                    if (a.getPlan().getDueDate().equals(b.getPlan().getDueDate())) {
                        return a.getPlan().getPeriodNumber().compareTo(b.getPlan().getPeriodNumber());
                    }
                    return a.getPlan().getDueDate().compareTo(b.getPlan().getDueDate());
                })
                .collect(Collectors.toList());
    }

    /**
     * 根据申请ID获取还款计划
     */
    public List<RepaymentPlan> getRepaymentPlansByApplicationId(Long applicationId, Long userId) {
        // 验证申请是否属于该用户
        LoanApplication application = loanApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new BusinessException("贷款申请不存在");
        }
        if (!application.getUserId().equals(userId)) {
            throw new BusinessException("无权访问该贷款申请的还款计划");
        }

        QueryWrapper<RepaymentPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("application_id", applicationId)
                .orderByAsc("period_number");
        return repaymentPlanMapper.selectList(wrapper);
    }

    /**
     * 获取用户的还款记录
     */
    public List<RepaymentRecord> getUserRepaymentRecords(Long userId) {
        QueryWrapper<RepaymentRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .orderByDesc("payment_time");
        return repaymentRecordMapper.selectList(wrapper);
    }

    /**
     * 还款概览DTO
     */
    public static class RepaymentOverviewDTO {
        private BigDecimal totalAmount = BigDecimal.ZERO;
        private BigDecimal paidAmount = BigDecimal.ZERO;
        private BigDecimal remainingAmount = BigDecimal.ZERO;
        private Integer totalPeriods = 0;
        private Integer paidPeriods = 0;
        private LocalDate nextPaymentDate;
        private BigDecimal nextPaymentAmount;

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public BigDecimal getPaidAmount() {
            return paidAmount;
        }

        public void setPaidAmount(BigDecimal paidAmount) {
            this.paidAmount = paidAmount;
        }

        public BigDecimal getRemainingAmount() {
            return remainingAmount;
        }

        public void setRemainingAmount(BigDecimal remainingAmount) {
            this.remainingAmount = remainingAmount;
        }

        public Integer getTotalPeriods() {
            return totalPeriods;
        }

        public void setTotalPeriods(Integer totalPeriods) {
            this.totalPeriods = totalPeriods;
        }

        public Integer getPaidPeriods() {
            return paidPeriods;
        }

        public void setPaidPeriods(Integer paidPeriods) {
            this.paidPeriods = paidPeriods;
        }

        public LocalDate getNextPaymentDate() {
            return nextPaymentDate;
        }

        public void setNextPaymentDate(LocalDate nextPaymentDate) {
            this.nextPaymentDate = nextPaymentDate;
        }

        public BigDecimal getNextPaymentAmount() {
            return nextPaymentAmount;
        }

        public void setNextPaymentAmount(BigDecimal nextPaymentAmount) {
            this.nextPaymentAmount = nextPaymentAmount;
        }
    }

    /**
     * 还款计划DTO（包含申请信息）
     */
    public static class RepaymentPlanDTO {
        private RepaymentPlan plan;
        private Long applicationId;
        private BigDecimal loanAmount;

        public RepaymentPlan getPlan() {
            return plan;
        }

        public void setPlan(RepaymentPlan plan) {
            this.plan = plan;
        }

        public Long getApplicationId() {
            return applicationId;
        }

        public void setApplicationId(Long applicationId) {
            this.applicationId = applicationId;
        }

        public BigDecimal getLoanAmount() {
            return loanAmount;
        }

        public void setLoanAmount(BigDecimal loanAmount) {
            this.loanAmount = loanAmount;
        }
    }
}

