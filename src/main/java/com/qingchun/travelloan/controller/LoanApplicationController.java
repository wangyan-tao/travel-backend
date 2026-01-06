package com.qingchun.travelloan.controller;

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
}

