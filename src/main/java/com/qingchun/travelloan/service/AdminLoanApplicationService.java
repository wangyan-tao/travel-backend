package com.qingchun.travelloan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingchun.travelloan.entity.LoanApplication;
import com.qingchun.travelloan.entity.LoanProduct;
import com.qingchun.travelloan.entity.User;
import com.qingchun.travelloan.exception.BusinessException;
import com.qingchun.travelloan.mapper.LoanApplicationMapper;
import com.qingchun.travelloan.mapper.LoanProductMapper;
import com.qingchun.travelloan.mapper.UserMapper;
import com.qingchun.travelloan.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员贷款申请管理服务类
 *
 * @author Qingchun Team
 */
@Service
public class AdminLoanApplicationService {

    @Autowired
    private LoanApplicationMapper loanApplicationMapper;

    @Autowired
    private LoanProductMapper loanProductMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取所有申请列表（分页）
     */
    public PageResult<LoanApplicationDTO> getAllApplications(String keyword, String status, Integer page, Integer size) {
        QueryWrapper<LoanApplication> wrapper = new QueryWrapper<>();
        
        // 按申请时间倒序排列
        wrapper.orderByDesc("apply_time");
        
        if (status != null && !status.trim().isEmpty() && !"ALL".equals(status)) {
            wrapper.eq("status", status);
        }
        
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
     * 获取所有申请ID列表（用于快速切换）
     */
    public List<Long> getAllApplicationIds(String status) {
        QueryWrapper<LoanApplication> wrapper = new QueryWrapper<>();
        wrapper.select("id");
        wrapper.orderByDesc("apply_time");
        
        if (status != null && !status.trim().isEmpty() && !"ALL".equals(status)) {
            wrapper.eq("status", status);
        }
        
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
     * 审批申请
     */
    @Transactional(rollbackFor = Exception.class)
    public void approveApplication(Long applicationId, Long approverId, BigDecimal approvedAmount, Integer approvedPeriod, String approvalOpinion) {
        LoanApplication application = loanApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }
        if (!"PENDING".equals(application.getStatus())) {
            throw new BusinessException("只能审批待审核状态的申请");
        }

        application.setStatus("APPROVED");
        application.setApproverId(approverId);
        application.setApproveTime(LocalDateTime.now());
        application.setLoanAmount(approvedAmount != null ? approvedAmount : application.getApplyAmount());
        application.setUpdatedAt(LocalDateTime.now());
        
        loanApplicationMapper.updateById(application);
    }

    /**
     * 拒绝申请
     */
    @Transactional(rollbackFor = Exception.class)
    public void rejectApplication(Long applicationId, Long approverId, String rejectReason) {
        LoanApplication application = loanApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }
        if (!"PENDING".equals(application.getStatus())) {
            throw new BusinessException("只能拒绝待审核状态的申请");
        }

        application.setStatus("REJECTED");
        application.setApproverId(approverId);
        application.setApproveTime(LocalDateTime.now());
        application.setRejectReason(rejectReason);
        application.setUpdatedAt(LocalDateTime.now());
        
        loanApplicationMapper.updateById(application);
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

