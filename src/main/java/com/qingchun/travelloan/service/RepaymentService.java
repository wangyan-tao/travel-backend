package com.qingchun.travelloan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingchun.travelloan.entity.LoanApplication;
import com.qingchun.travelloan.entity.LoanProduct;
import com.qingchun.travelloan.entity.RepaymentPlan;
import com.qingchun.travelloan.entity.RepaymentRecord;
import com.qingchun.travelloan.exception.BusinessException;
import com.qingchun.travelloan.mapper.LoanApplicationMapper;
import com.qingchun.travelloan.mapper.LoanProductMapper;
import com.qingchun.travelloan.mapper.RepaymentPlanMapper;
import com.qingchun.travelloan.mapper.RepaymentRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 还款管理服务类
 *
 * @author Qingchun Team
 */
@Service
public class RepaymentService {

    private static final Logger logger = LoggerFactory.getLogger(RepaymentService.class);

    @Autowired
    private RepaymentPlanMapper repaymentPlanMapper;

    @Autowired
    private RepaymentRecordMapper repaymentRecordMapper;

    @Autowired
    private LoanApplicationMapper loanApplicationMapper;

    @Autowired
    private LoanProductMapper loanProductMapper;

    /**
     * 获取用户的还款概览
     */
    public RepaymentOverviewDTO getRepaymentOverview(Long userId) {
        // 获取用户所有已批准、已发放或已完成的贷款申请（这些状态可能有还款计划）
        QueryWrapper<LoanApplication> applicationWrapper = new QueryWrapper<>();
        applicationWrapper.eq("user_id", userId)
                .in("status", "APPROVED", "DISBURSED", "COMPLETED");
        List<LoanApplication> applications = loanApplicationMapper.selectList(applicationWrapper);
        
        logger.debug("用户 {} 的贷款申请数量: {}", userId, applications.size());

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

            // 如果没有还款计划，跳过该申请
            if (plans == null || plans.isEmpty()) {
                logger.debug("申请 {} 没有还款计划", application.getId());
                continue;
            }
            
            logger.debug("申请 {} 的还款计划数量: {}", application.getId(), plans.size());

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
        // 获取用户所有已批准、已发放或已完成的贷款申请
        QueryWrapper<LoanApplication> applicationWrapper = new QueryWrapper<>();
        applicationWrapper.eq("user_id", userId)
                .in("status", "APPROVED", "DISBURSED", "COMPLETED");
        List<LoanApplication> applications = loanApplicationMapper.selectList(applicationWrapper);

        return applications.stream()
                .flatMap(app -> {
                    // 获取产品信息
                    LoanProduct product = loanProductMapper.selectById(app.getProductId());
                    String productName = product != null && product.getProductName() != null 
                            ? product.getProductName() 
                            : "未知产品";
                    
                    QueryWrapper<RepaymentPlan> planWrapper = new QueryWrapper<>();
                    planWrapper.eq("application_id", app.getId())
                            .orderByAsc("period_number");
                    List<RepaymentPlan> plans = repaymentPlanMapper.selectList(planWrapper);
                    return plans.stream().map(plan -> {
                        RepaymentPlanDTO dto = new RepaymentPlanDTO();
                        dto.setPlan(plan);
                        dto.setApplicationId(app.getId());
                        dto.setLoanAmount(app.getLoanAmount() != null ? app.getLoanAmount() : app.getApplyAmount());
                        dto.setProductName(productName);
                        return dto;
                    });
                })
                .sorted((a, b) -> {
                    // 先按产品名称排序，然后按应还日期排序
                    int productCompare = a.getProductName().compareTo(b.getProductName());
                    if (productCompare != 0) {
                        return productCompare;
                    }
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
     * 按时间维度查询还款记录
     * @param userId 用户ID
     * @param timeRange 时间范围：3M-近3个月, 6M-近6个月, ALL-全部
     */
    public List<RepaymentRecord> getUserRepaymentRecordsByTimeRange(Long userId, String timeRange) {
        QueryWrapper<RepaymentRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);

        LocalDateTime now = LocalDateTime.now();
        if ("3M".equals(timeRange)) {
            LocalDateTime threeMonthsAgo = now.minusMonths(3);
            wrapper.ge("payment_time", threeMonthsAgo);
        } else if ("6M".equals(timeRange)) {
            LocalDateTime sixMonthsAgo = now.minusMonths(6);
            wrapper.ge("payment_time", sixMonthsAgo);
        }
        // ALL 不添加时间限制

        wrapper.orderByDesc("payment_time");
        return repaymentRecordMapper.selectList(wrapper);
    }

    /**
     * 按产品分组的还款详情
     */
    public List<ProductRepaymentDetailDTO> getProductRepaymentDetails(Long userId) {
        // 获取用户所有已批准、已发放或已完成的贷款申请（这些状态可能有还款计划）
        QueryWrapper<LoanApplication> applicationWrapper = new QueryWrapper<>();
        applicationWrapper.eq("user_id", userId)
                .in("status", "APPROVED", "DISBURSED", "COMPLETED");
        List<LoanApplication> applications = loanApplicationMapper.selectList(applicationWrapper);
        
        logger.debug("用户 {} 的贷款申请数量: {}", userId, applications.size());
        
        // 如果没有申请，直接返回空列表
        if (applications.isEmpty()) {
            logger.debug("用户 {} 没有符合条件的贷款申请", userId);
            return new ArrayList<>();
        }

        Map<Long, ProductRepaymentDetailDTO> productMap = new HashMap<>();

        for (LoanApplication application : applications) {
            Long productId = application.getProductId();
            LoanProduct product = loanProductMapper.selectById(productId);
            if (product == null) {
                continue;
            }

            ProductRepaymentDetailDTO detail = productMap.computeIfAbsent(productId, k -> {
                ProductRepaymentDetailDTO dto = new ProductRepaymentDetailDTO();
                dto.setProductId(productId);
                dto.setProductName(product.getProductName());
                dto.setProductType(product.getProductType());
                dto.setTotalLoanAmount(BigDecimal.ZERO);
                dto.setTotalPeriods(0);
                dto.setPaidPeriods(0);
                dto.setRemainingPeriods(0);
                dto.setTotalPrincipal(BigDecimal.ZERO);
                dto.setTotalInterest(BigDecimal.ZERO);
                dto.setPaidPrincipal(BigDecimal.ZERO);
                dto.setPaidInterest(BigDecimal.ZERO);
                dto.setPaidAmount(BigDecimal.ZERO);
                dto.setRemainingAmount(BigDecimal.ZERO);
                dto.setPlans(new ArrayList<>());
                dto.setStartDate(null);
                dto.setEndDate(null);
                return dto;
            });

            // 获取该申请的所有还款计划
            QueryWrapper<RepaymentPlan> planWrapper = new QueryWrapper<>();
            planWrapper.eq("application_id", application.getId())
                    .orderByAsc("period_number");
            List<RepaymentPlan> plans = repaymentPlanMapper.selectList(planWrapper);

            // 如果没有还款计划，跳过该申请
            if (plans == null || plans.isEmpty()) {
                logger.debug("申请 {} 没有还款计划", application.getId());
                continue;
            }
            
            logger.debug("申请 {} 的还款计划数量: {}", application.getId(), plans.size());

            BigDecimal loanAmount = application.getLoanAmount() != null 
                    ? application.getLoanAmount() 
                    : application.getApplyAmount();
            detail.setTotalLoanAmount(detail.getTotalLoanAmount().add(loanAmount));
            detail.setTotalPeriods(detail.getTotalPeriods() + plans.size());

            LocalDate startDate = plans.get(0).getDueDate();
            LocalDate endDate = plans.get(plans.size() - 1).getDueDate();

            if (detail.getStartDate() == null || startDate.isBefore(detail.getStartDate())) {
                detail.setStartDate(startDate);
            }
            if (detail.getEndDate() == null || endDate.isAfter(detail.getEndDate())) {
                detail.setEndDate(endDate);
            }

            for (RepaymentPlan plan : plans) {
                detail.getPlans().add(plan);
                detail.setTotalPrincipal(detail.getTotalPrincipal().add(plan.getPrincipalAmount()));
                detail.setTotalInterest(detail.getTotalInterest().add(plan.getInterestAmount()));

                if ("PAID".equals(plan.getStatus())) {
                    detail.setPaidPeriods(detail.getPaidPeriods() + 1);
                    detail.setPaidPrincipal(detail.getPaidPrincipal().add(plan.getPrincipalAmount()));
                    detail.setPaidInterest(detail.getPaidInterest().add(plan.getInterestAmount()));
                    detail.setPaidAmount(detail.getPaidAmount().add(plan.getTotalAmount()));
                } else {
                    detail.setRemainingPeriods(detail.getRemainingPeriods() + 1);
                    detail.setRemainingAmount(detail.getRemainingAmount().add(plan.getTotalAmount()));
                }
            }
        }

        return new ArrayList<>(productMap.values());
    }

    /**
     * 提前还款测算
     * @param applicationId 申请ID
     * @param prepayAmount 提前还款金额
     * @param userId 用户ID
     */
    public PrepayCalculationDTO calculatePrepayment(Long applicationId, BigDecimal prepayAmount, Long userId) {
        // 验证申请是否属于该用户
        LoanApplication application = loanApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new BusinessException("贷款申请不存在");
        }
        if (!application.getUserId().equals(userId)) {
            throw new BusinessException("无权访问该贷款申请");
        }

        // 获取该申请的所有还款计划
        QueryWrapper<RepaymentPlan> planWrapper = new QueryWrapper<>();
        planWrapper.eq("application_id", applicationId)
                .orderByAsc("period_number");
        List<RepaymentPlan> plans = repaymentPlanMapper.selectList(planWrapper);

        if (plans.isEmpty()) {
            throw new BusinessException("该申请暂无还款计划");
        }

        PrepayCalculationDTO result = new PrepayCalculationDTO();
        result.setPrepayAmount(prepayAmount);

        BigDecimal remainingPrincipal = BigDecimal.ZERO;
        BigDecimal remainingInterest = BigDecimal.ZERO;
        BigDecimal totalRemainingAmount = BigDecimal.ZERO;
        int remainingPeriods = 0;

        // 计算剩余未还的本金和利息
        for (RepaymentPlan plan : plans) {
            if (!"PAID".equals(plan.getStatus())) {
                remainingPrincipal = remainingPrincipal.add(plan.getPrincipalAmount());
                remainingInterest = remainingInterest.add(plan.getInterestAmount());
                totalRemainingAmount = totalRemainingAmount.add(plan.getTotalAmount());
                remainingPeriods++;
            }
        }

        result.setRemainingPrincipal(remainingPrincipal);
        result.setRemainingInterest(remainingInterest);
        result.setRemainingAmount(totalRemainingAmount);
        result.setRemainingPeriods(remainingPeriods);

        // 如果提前还款金额大于等于剩余金额，则全部还清
        if (prepayAmount.compareTo(totalRemainingAmount) >= 0) {
            result.setSavedInterest(remainingInterest);
            result.setNewRemainingAmount(BigDecimal.ZERO);
            result.setNewRemainingPeriods(0);
            result.setReducedPeriods(remainingPeriods);
        } else {
            // 按比例计算减免的利息
            // 简化计算：假设提前还款金额按比例减少后续期数的利息
            BigDecimal prepayRatio = prepayAmount.divide(totalRemainingAmount, 4, RoundingMode.HALF_UP);
            BigDecimal savedInterest = remainingInterest.multiply(prepayRatio).setScale(2, RoundingMode.HALF_UP);
            
            result.setSavedInterest(savedInterest);
            result.setNewRemainingAmount(totalRemainingAmount.subtract(prepayAmount));
            
            // 估算减少的期数（简化计算）
            int reducedPeriods = (int) Math.ceil(remainingPeriods * prepayRatio.doubleValue());
            result.setReducedPeriods(Math.min(reducedPeriods, remainingPeriods));
            result.setNewRemainingPeriods(remainingPeriods - result.getReducedPeriods());
        }

        return result;
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
        private String productName;

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

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }
    }

    /**
     * 按产品分组的还款详情DTO
     */
    public static class ProductRepaymentDetailDTO {
        private Long productId;
        private String productName;
        private String productType;
        private BigDecimal totalLoanAmount; // 总授信金额
        private Integer totalPeriods; // 分期总期限
        private Integer paidPeriods; // 已还期数
        private Integer remainingPeriods; // 剩余期数
        private BigDecimal totalPrincipal; // 总本金
        private BigDecimal totalInterest; // 总利息
        private BigDecimal paidPrincipal; // 已还本金
        private BigDecimal paidInterest; // 已还利息
        private BigDecimal paidAmount; // 已还金额
        private BigDecimal remainingAmount; // 待还总金额
        private LocalDate startDate; // 还款起始日期
        private LocalDate endDate; // 还款结束日期
        private List<RepaymentPlan> plans; // 还款计划列表

        // Getters and Setters
        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductType() {
            return productType;
        }

        public void setProductType(String productType) {
            this.productType = productType;
        }

        public BigDecimal getTotalLoanAmount() {
            return totalLoanAmount;
        }

        public void setTotalLoanAmount(BigDecimal totalLoanAmount) {
            this.totalLoanAmount = totalLoanAmount;
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

        public Integer getRemainingPeriods() {
            return remainingPeriods;
        }

        public void setRemainingPeriods(Integer remainingPeriods) {
            this.remainingPeriods = remainingPeriods;
        }

        public BigDecimal getTotalPrincipal() {
            return totalPrincipal;
        }

        public void setTotalPrincipal(BigDecimal totalPrincipal) {
            this.totalPrincipal = totalPrincipal;
        }

        public BigDecimal getTotalInterest() {
            return totalInterest;
        }

        public void setTotalInterest(BigDecimal totalInterest) {
            this.totalInterest = totalInterest;
        }

        public BigDecimal getPaidPrincipal() {
            return paidPrincipal;
        }

        public void setPaidPrincipal(BigDecimal paidPrincipal) {
            this.paidPrincipal = paidPrincipal;
        }

        public BigDecimal getPaidInterest() {
            return paidInterest;
        }

        public void setPaidInterest(BigDecimal paidInterest) {
            this.paidInterest = paidInterest;
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

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        public List<RepaymentPlan> getPlans() {
            return plans;
        }

        public void setPlans(List<RepaymentPlan> plans) {
            this.plans = plans;
        }
    }

    /**
     * 提前还款测算DTO
     */
    public static class PrepayCalculationDTO {
        private BigDecimal prepayAmount; // 提前还款金额
        private BigDecimal remainingPrincipal; // 剩余本金
        private BigDecimal remainingInterest; // 剩余利息
        private BigDecimal remainingAmount; // 剩余总金额
        private Integer remainingPeriods; // 剩余期数
        private BigDecimal savedInterest; // 可减免利息
        private BigDecimal newRemainingAmount; // 提前还款后剩余金额
        private Integer newRemainingPeriods; // 提前还款后剩余期数
        private Integer reducedPeriods; // 减少的期数

        // Getters and Setters
        public BigDecimal getPrepayAmount() {
            return prepayAmount;
        }

        public void setPrepayAmount(BigDecimal prepayAmount) {
            this.prepayAmount = prepayAmount;
        }

        public BigDecimal getRemainingPrincipal() {
            return remainingPrincipal;
        }

        public void setRemainingPrincipal(BigDecimal remainingPrincipal) {
            this.remainingPrincipal = remainingPrincipal;
        }

        public BigDecimal getRemainingInterest() {
            return remainingInterest;
        }

        public void setRemainingInterest(BigDecimal remainingInterest) {
            this.remainingInterest = remainingInterest;
        }

        public BigDecimal getRemainingAmount() {
            return remainingAmount;
        }

        public void setRemainingAmount(BigDecimal remainingAmount) {
            this.remainingAmount = remainingAmount;
        }

        public Integer getRemainingPeriods() {
            return remainingPeriods;
        }

        public void setRemainingPeriods(Integer remainingPeriods) {
            this.remainingPeriods = remainingPeriods;
        }

        public BigDecimal getSavedInterest() {
            return savedInterest;
        }

        public void setSavedInterest(BigDecimal savedInterest) {
            this.savedInterest = savedInterest;
        }

        public BigDecimal getNewRemainingAmount() {
            return newRemainingAmount;
        }

        public void setNewRemainingAmount(BigDecimal newRemainingAmount) {
            this.newRemainingAmount = newRemainingAmount;
        }

        public Integer getNewRemainingPeriods() {
            return newRemainingPeriods;
        }

        public void setNewRemainingPeriods(Integer newRemainingPeriods) {
            this.newRemainingPeriods = newRemainingPeriods;
        }

        public Integer getReducedPeriods() {
            return reducedPeriods;
        }

        public void setReducedPeriods(Integer reducedPeriods) {
            this.reducedPeriods = reducedPeriods;
        }
    }
}

