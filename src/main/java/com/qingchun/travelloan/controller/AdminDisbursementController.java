package com.qingchun.travelloan.controller;

import com.qingchun.travelloan.service.AdminDisbursementService;
import com.qingchun.travelloan.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员放款管理控制器
 *
 * @author Qingchun Team
 */
@Tag(name = "管理员-放款管理")
@RestController
@RequestMapping("/admin/disbursements")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDisbursementController {

    @Autowired
    private AdminDisbursementService adminDisbursementService;

    @Operation(summary = "获取已批准申请列表")
    @GetMapping
    public Result<com.qingchun.travelloan.vo.PageResult<AdminDisbursementService.LoanApplicationDTO>> getApprovedApplications(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        com.qingchun.travelloan.vo.PageResult<AdminDisbursementService.LoanApplicationDTO> pageResult = 
                adminDisbursementService.getApprovedApplications(keyword, status, page, size);
        return Result.success(pageResult);
    }
    
    @Operation(summary = "获取已批准申请ID列表（用于快速切换）")
    @GetMapping("/ids")
    public Result<List<Long>> getApprovedApplicationIds() {
        List<Long> ids = adminDisbursementService.getApprovedApplicationIds();
        return Result.success(ids);
    }

    @Operation(summary = "获取申请详情")
    @GetMapping("/{id}")
    public Result<AdminDisbursementService.LoanApplicationDTO> getApplication(@PathVariable Long id) {
        AdminDisbursementService.LoanApplicationDTO application = 
                adminDisbursementService.getApplicationById(id);
        return Result.success(application);
    }

    @Operation(summary = "同意放款")
    @PostMapping("/{id}/approve")
    public Result<Void> approveDisbursement(
            @PathVariable Long id,
            Authentication authentication) {
        Long approverId = (Long) authentication.getPrincipal();
        adminDisbursementService.approveDisbursement(id, approverId);
        return Result.success("放款成功，还款计划已生成");
    }

    @Operation(summary = "驳回放款")
    @PostMapping("/{id}/reject")
    public Result<Void> rejectDisbursement(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        Long approverId = (Long) authentication.getPrincipal();
        String rejectReason = request.get("rejectReason");
        if (rejectReason == null || rejectReason.trim().isEmpty()) {
            return Result.error("拒绝原因不能为空");
        }
        adminDisbursementService.rejectDisbursement(id, approverId, rejectReason);
        return Result.success("放款已驳回");
    }
}

