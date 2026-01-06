package com.qingchun.travelloan.controller;

import com.qingchun.travelloan.service.AdminLoanApplicationService;
import com.qingchun.travelloan.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 管理员贷款申请管理控制器
 *
 * @author Qingchun Team
 */
@Tag(name = "管理员-贷款申请管理")
@RestController
@RequestMapping("/admin/applications")
@PreAuthorize("hasRole('ADMIN')")
public class AdminLoanApplicationController {

    @Autowired
    private AdminLoanApplicationService adminLoanApplicationService;

    @Operation(summary = "获取申请列表")
    @GetMapping
    public Result<com.qingchun.travelloan.vo.PageResult<AdminLoanApplicationService.LoanApplicationDTO>> getApplications(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        com.qingchun.travelloan.vo.PageResult<AdminLoanApplicationService.LoanApplicationDTO> pageResult = 
                adminLoanApplicationService.getAllApplications(keyword, status, page, size);
        return Result.success(pageResult);
    }
    
    @Operation(summary = "获取申请ID列表（用于快速切换）")
    @GetMapping("/ids")
    public Result<List<Long>> getApplicationIds(
            @RequestParam(required = false) String status) {
        List<Long> ids = adminLoanApplicationService.getAllApplicationIds(status);
        return Result.success(ids);
    }

    @Operation(summary = "获取申请详情")
    @GetMapping("/{id}")
    public Result<AdminLoanApplicationService.LoanApplicationDTO> getApplication(@PathVariable Long id) {
        AdminLoanApplicationService.LoanApplicationDTO application = 
                adminLoanApplicationService.getApplicationById(id);
        return Result.success(application);
    }

    @Operation(summary = "审批申请")
    @PostMapping("/{id}/approve")
    public Result<Void> approveApplication(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        Long approverId = (Long) authentication.getPrincipal();
        BigDecimal approvedAmount = request.get("approvedAmount") != null 
                ? new BigDecimal(request.get("approvedAmount").toString()) 
                : null;
        Integer approvedPeriod = request.get("approvedPeriod") != null 
                ? Integer.valueOf(request.get("approvedPeriod").toString()) 
                : null;
        String approvalOpinion = request.get("approvalOpinion") != null 
                ? request.get("approvalOpinion").toString() 
                : null;
        
        adminLoanApplicationService.approveApplication(id, approverId, approvedAmount, approvedPeriod, approvalOpinion);
        return Result.success("申请已批准");
    }

    @Operation(summary = "拒绝申请")
    @PostMapping("/{id}/reject")
    public Result<Void> rejectApplication(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        Long approverId = (Long) authentication.getPrincipal();
        String rejectReason = request.get("rejectReason");
        
        adminLoanApplicationService.rejectApplication(id, approverId, rejectReason);
        return Result.success("申请已拒绝");
    }
}

