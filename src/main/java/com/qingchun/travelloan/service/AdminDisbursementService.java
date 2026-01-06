package com.qingchun.travelloan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingchun.travelloan.entity.LoanApplication;
import com.qingchun.travelloan.entity.LoanProduct;
import com.qingchun.travelloan.entity.RepaymentPlan;
import com.qingchun.travelloan.entity.User;
import com.qingchun.travelloan.exception.BusinessException;
import com.qingchun.travelloan.mapper.LoanApplicationMapper;
import com.qingchun.travelloan.mapper.LoanProductMapper;
import com.qingchun.travelloan.mapper.RepaymentPlanMapper;
import com.qingchun.travelloan.mapper.UserMapper;
import com.qingchun.travelloan.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员放款管理服务类
 *
 * @author Qingchun Team
 */
@Service
public class AdminDisbursementService {

    @Autowired
    private LoanApplicationMapper loanApplicationMapper;

    @Autowired
    private LoanProductMapper loanProductMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RepaymentPlanMapper repaymentPlanMapper;

    /**
     * 获取已批准申请列表（分页）
     */
    public PageResult<LoanApplicationDTO> getApprovedApplications(String keyword, String status, Integer page, Integer size) {
        QueryWrapper<LoanApplication> wrapper = new QueryWrapper<>();
        
        // 只查询已批准状态的申请
        wrapper.eq("status", "APPROVED");
        
        // 按申请时间倒序排列
        wrapper.orderByDesc("approve_time");
        
        // 获取总数
        long total = loanApplicationMapper.selectCount(wrapper);
        
        // 分页处理
        int currentPage = (page == null || page < 1) ? 1 : page;
        int pageSize = (size == null || size < 1) ? 10 : size;
        int offset = (currentPage - 1) * pageSize;
        wrapper.last("LIMIT " + offset + "," + pageSize);
        
        List<LoanApplication> applications = loanApplicationMapper.selectList(wrapper);
        
        List<LoanApplicationDTO> records = applications.stream().map(app -> {
            LoanApplicationDTO dto = new LoanApplicationDTO();
            dto.setApplication(app);
            
            // 获取产品信息
            LoanProduct product = loanProductMapper.selectById(app.getProductId());
            if (product != null && product.getProductName() != null) {
                dto.setProductName(product.getProductName());
            } else {
                dto.setProductName("未知产品");
            }
            
            // 获取用户信息
            User user = userMapper.selectById(app.getUserId());
            if (user != null) {
                dto.setUsername(user.getUsername());
            } else {
                dto.setUsername("未知用户");
            }
            
            return dto;
        }).collect(Collectors.toList());
        
        return PageResult.of(records, total, currentPage, pageSize);
    }
    
    /**
     * 获取已批准申请ID列表（用于快速切换）
     */
    public List<Long> getApprovedApplicationIds() {
        QueryWrapper<LoanApplication> wrapper = new QueryWrapper<>();
        wrapper.select("id");
        wrapper.eq("status", "APPROVED");
        wrapper.orderByDesc("approve_time");
        
        List<LoanApplication> applications = loanApplicationMapper.selectList(wrapper);
        return applications.stream()
                .map(LoanApplication::getId)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取申请详情
     */
    public LoanApplicationDTO getApplicationById(Long applicationId) {
        LoanApplication application = loanApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }
        if (!"APPROVED".equals(application.getStatus())) {
            throw new BusinessException("只能查看已批准状态的申请");
        }

        LoanApplicationDTO dto = new LoanApplicationDTO();
        dto.setApplication(application);
        
        // 获取产品信息
        LoanProduct product = loanProductMapper.selectById(application.getProductId());
        if (product != null && product.getProductName() != null) {
            dto.setProductName(product.getProductName());
        } else {
            dto.setProductName("未知产品");
        }
        
        // 获取用户信息
        User user = userMapper.selectById(application.getUserId());
        if (user != null) {
            dto.setUsername(user.getUsername());
        } else {
            dto.setUsername("未知用户");
        }
        
        return dto;
    }

    /**
     * 同意放款
     * 1. 将申请状态改为DISBURSED
     * 2. 设置放款时间
     * 3. 计算并生成还款计划
     */
    @Transactional(rollbackFor = Exception.class)
    public void approveDisbursement(Long applicationId, Long approverId) {
        LoanApplication application = loanApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }
        if (!"APPROVED".equals(application.getStatus())) {
            throw new BusinessException("只能放款已批准状态的申请");
        }

        // 检查是否已有还款计划
        QueryWrapper<RepaymentPlan> planWrapper = new QueryWrapper<>();
        planWrapper.eq("application_id", applicationId);
        long existingPlans = repaymentPlanMapper.selectCount(planWrapper);
        if (existingPlans > 0) {
            throw new BusinessException("该申请已存在还款计划，不能重复放款");
        }

        // 获取产品信息
        LoanProduct product = loanProductMapper.selectById(application.getProductId());
        if (product == null) {
            throw new BusinessException("产品不存在");
        }

        // 更新申请状态
        application.setStatus("DISBURSED");
        application.setLoanTime(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());
        loanApplicationMapper.updateById(application);

        // 计算并生成还款计划
        generateRepaymentPlan(application, product);
    }

    /**
     * 驳回放款
     */
    @Transactional(rollbackFor = Exception.class)
    public void rejectDisbursement(Long applicationId, Long approverId, String rejectReason) {
        LoanApplication application = loanApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }
        if (!"APPROVED".equals(application.getStatus())) {
            throw new BusinessException("只能驳回已批准状态的申请");
        }

        // 将状态改回PENDING，并设置拒绝原因
        application.setStatus("PENDING");
        application.setRejectReason(rejectReason);
        application.setUpdatedAt(LocalDateTime.now());
        loanApplicationMapper.updateById(application);
    }

    /**
     * 生成还款计划（等额本息方式）
     */
    private void generateRepaymentPlan(LoanApplication application, LoanProduct product) {
        BigDecimal loanAmount = application.getLoanAmount() != null 
                ? application.getLoanAmount() 
                : application.getApplyAmount();
        int term = application.getApplyTerm();
        BigDecimal annualRate = product.getInterestRate();
        
        if (annualRate == null || annualRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("产品利率无效");
        }

        // 月利率 = 年利率 / 12
        BigDecimal monthlyRate = annualRate.divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP)
                .divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);

        // 等额本息计算：每月还款额 = 本金 * [月利率 * (1+月利率)^期数] / [(1+月利率)^期数 - 1]
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal power = onePlusRate.pow(term);
        BigDecimal numerator = monthlyRate.multiply(power);
        BigDecimal denominator = power.subtract(BigDecimal.ONE);
        BigDecimal monthlyPayment = loanAmount.multiply(numerator)
                .divide(denominator, 2, RoundingMode.HALF_UP);

        // 计算每期还款计划
        BigDecimal remainingPrincipal = loanAmount;
        LocalDate startDate = LocalDate.now();
        
        List<RepaymentPlan> plans = new ArrayList<>();
        BigDecimal totalPrincipal = BigDecimal.ZERO;
        
        for (int i = 1; i <= term; i++) {
            RepaymentPlan plan = new RepaymentPlan();
            plan.setApplicationId(application.getId());
            plan.setPeriodNumber(i);
            plan.setDueDate(startDate.plusMonths(i));
            
            // 计算当期利息
            BigDecimal interest = remainingPrincipal.multiply(monthlyRate)
                    .setScale(2, RoundingMode.HALF_UP);
            
            // 计算当期本金
            BigDecimal principal;
            if (i == term) {
                // 最后一期，本金 = 剩余本金
                principal = remainingPrincipal;
            } else {
                principal = monthlyPayment.subtract(interest);
            }
            
            plan.setPrincipalAmount(principal);
            plan.setInterestAmount(interest);
            plan.setTotalAmount(monthlyPayment);
            plan.setStatus("PENDING");
            plan.setCreatedAt(LocalDateTime.now());
            plan.setUpdatedAt(LocalDateTime.now());
            
            plans.add(plan);
            
            totalPrincipal = totalPrincipal.add(principal);
            remainingPrincipal = remainingPrincipal.subtract(principal);
        }
        
        // 调整最后一期的金额，确保本金总额等于贷款金额
        if (plans.size() > 0) {
            RepaymentPlan lastPlan = plans.get(plans.size() - 1);
            BigDecimal adjustment = loanAmount.subtract(totalPrincipal);
            lastPlan.setPrincipalAmount(lastPlan.getPrincipalAmount().add(adjustment));
            // 重新计算最后一期的总金额 = 本金 + 利息
            lastPlan.setTotalAmount(lastPlan.getPrincipalAmount().add(lastPlan.getInterestAmount()));
        }
        
        // 批量插入还款计划
        for (RepaymentPlan plan : plans) {
            repaymentPlanMapper.insert(plan);
        }
    }

    /**
     * 申请DTO（包含产品名称和用户名）
     */
    public static class LoanApplicationDTO {
        private LoanApplication application;
        private String productName;
        private String username;

        public LoanApplication getApplication() {
            return application;
        }

        public void setApplication(LoanApplication application) {
            this.application = application;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}

