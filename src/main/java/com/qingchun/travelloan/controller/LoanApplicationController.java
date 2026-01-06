package com.qingchun.travelloan.controller;

import com.qingchun.travelloan.entity.LoanApplication;
import com.qingchun.travelloan.service.LoanApplicationService;
import com.qingchun.travelloan.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 贷款申请控制器
 *
 * @author Qingchun Team
 */
@Tag(name = "贷款申请管理")
@RestController
@RequestMapping("/loan")
public class LoanApplicationController {

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Operation(summary = "获取我的申请列表")
    @GetMapping("/my-applications")
    public Result<List<LoanApplicationService.LoanApplicationDTO>> getMyApplications(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<LoanApplicationService.LoanApplicationDTO> applications = loanApplicationService.getUserApplications(userId);
        return Result.success(applications);
    }

    @Operation(summary = "获取申请详情")
    @GetMapping("/applications/{id}")
    public Result<LoanApplicationService.LoanApplicationDTO> getApplication(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        LoanApplicationService.LoanApplicationDTO application = loanApplicationService.getApplicationById(id, userId);
        return Result.success(application);
    }

    @Operation(summary = "取消申请")
    @PutMapping("/applications/{id}/cancel")
    public Result<Void> cancelApplication(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        loanApplicationService.cancelApplication(id, userId);
        return Result.success("申请已取消");
    }

    @Operation(summary = "创建贷款申请")
    @PostMapping("/applications")
    public Result<LoanApplicationService.LoanApplicationDTO> createApplication(
            @RequestBody CreateApplicationRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        LoanApplication application = loanApplicationService.createApplication(
            userId,
            request.getProductId(),
            request.getApplyAmount(),
            request.getApplyTerm(),
            request.getPurpose()
        );
        
        LoanApplicationService.LoanApplicationDTO dto = loanApplicationService.getApplicationById(application.getId(), userId);
        return Result.success(dto);
    }

    /**
     * 创建申请请求DTO
     */
    public static class CreateApplicationRequest {
        private Long productId;
        private java.math.BigDecimal applyAmount;
        private Integer applyTerm;
        private String purpose;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public java.math.BigDecimal getApplyAmount() {
            return applyAmount;
        }

        public void setApplyAmount(java.math.BigDecimal applyAmount) {
            this.applyAmount = applyAmount;
        }

        public Integer getApplyTerm() {
            return applyTerm;
        }

        public void setApplyTerm(Integer applyTerm) {
            this.applyTerm = applyTerm;
        }

        public String getPurpose() {
            return purpose;
        }

        public void setPurpose(String purpose) {
            this.purpose = purpose;
        }
    }
}

