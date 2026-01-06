package com.qingchun.travelloan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingchun.travelloan.entity.LoanApplication;
import com.qingchun.travelloan.entity.LoanProduct;
import com.qingchun.travelloan.exception.BusinessException;
import com.qingchun.travelloan.mapper.LoanApplicationMapper;
import com.qingchun.travelloan.mapper.LoanProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 贷款申请服务类
 *
 * @author Qingchun Team
 */
@Service
public class LoanApplicationService {

    @Autowired
    private LoanApplicationMapper loanApplicationMapper;

    @Autowired
    private LoanProductMapper loanProductMapper;

    /**
     * 获取用户的申请列表
     */
    public List<LoanApplicationDTO> getUserApplications(Long userId) {
        QueryWrapper<LoanApplication> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .orderByDesc("apply_time");
        List<LoanApplication> applications = loanApplicationMapper.selectList(wrapper);

        return applications.stream().map(app -> {
            LoanApplicationDTO dto = new LoanApplicationDTO();
            dto.setApplication(app);
            
            // 获取产品信息
            LoanProduct product = loanProductMapper.selectById(app.getProductId());
            if (product != null && product.getProductName() != null) {
                dto.setProductName(product.getProductName());
            } else {
                dto.setProductName("未知产品");
            }
            
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 根据ID获取申请详情
     */
    public LoanApplicationDTO getApplicationById(Long applicationId, Long userId) {
        LoanApplication application = loanApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }
        if (!application.getUserId().equals(userId)) {
            throw new BusinessException("无权访问该申请");
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
        
        return dto;
    }

    /**
     * 取消申请
     */
    public void cancelApplication(Long applicationId, Long userId) {
        LoanApplication application = loanApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }
        if (!application.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该申请");
        }
        if (!"PENDING".equals(application.getStatus())) {
            throw new BusinessException("只能取消待审核状态的申请");
        }

        application.setStatus("CANCELLED");
        application.setUpdatedAt(LocalDateTime.now());
        loanApplicationMapper.updateById(application);
    }

    /**
     * 申请DTO（包含产品名称）
     */
    public static class LoanApplicationDTO {
        private LoanApplication application;
        private String productName;

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
    }
}

