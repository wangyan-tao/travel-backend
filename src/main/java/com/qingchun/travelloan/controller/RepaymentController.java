package com.qingchun.travelloan.controller;

import com.qingchun.travelloan.entity.RepaymentPlan;
import com.qingchun.travelloan.entity.RepaymentRecord;
import com.qingchun.travelloan.service.RepaymentService;
import com.qingchun.travelloan.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 还款管理控制器
 *
 * @author Qingchun Team
 */
@Tag(name = "还款管理")
@RestController
@RequestMapping("/repayment")
public class RepaymentController {

    @Autowired
    private RepaymentService repaymentService;

    @Operation(summary = "获取还款概览")
    @GetMapping("/overview")
    public Result<RepaymentService.RepaymentOverviewDTO> getRepaymentOverview(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        RepaymentService.RepaymentOverviewDTO overview = repaymentService.getRepaymentOverview(userId);
        return Result.success(overview);
    }

    @Operation(summary = "获取用户所有还款计划")
    @GetMapping("/plans")
    public Result<List<RepaymentService.RepaymentPlanDTO>> getUserRepaymentPlans(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<RepaymentService.RepaymentPlanDTO> plans = repaymentService.getUserRepaymentPlans(userId);
        return Result.success(plans);
    }

    @Operation(summary = "根据申请ID获取还款计划")
    @GetMapping("/plans/application/{applicationId}")
    public Result<List<RepaymentPlan>> getRepaymentPlansByApplicationId(
            @PathVariable Long applicationId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<RepaymentPlan> plans = repaymentService.getRepaymentPlansByApplicationId(applicationId, userId);
        return Result.success(plans);
    }

    @Operation(summary = "获取还款记录")
    @GetMapping("/records")
    public Result<List<RepaymentRecord>> getRepaymentRecords(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<RepaymentRecord> records = repaymentService.getUserRepaymentRecords(userId);
        return Result.success(records);
    }
}

