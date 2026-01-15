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

    @Operation(summary = "按时间维度查询还款记录")
    @GetMapping("/records/time-range")
    public Result<List<RepaymentRecord>> getRepaymentRecordsByTimeRange(
            @RequestParam(defaultValue = "ALL") String timeRange,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<RepaymentRecord> records = repaymentService.getUserRepaymentRecordsByTimeRange(userId, timeRange);
        return Result.success(records);
    }

    @Operation(summary = "按产品分组的还款详情")
    @GetMapping("/product-details")
    public Result<List<RepaymentService.ProductRepaymentDetailDTO>> getProductRepaymentDetails(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<RepaymentService.ProductRepaymentDetailDTO> details = repaymentService.getProductRepaymentDetails(userId);
        return Result.success(details);
    }

    @Operation(summary = "提前还款测算")
    @PostMapping("/prepay/calculate")
    public Result<RepaymentService.PrepayCalculationDTO> calculatePrepayment(
            @RequestParam Long applicationId,
            @RequestParam java.math.BigDecimal prepayAmount,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        RepaymentService.PrepayCalculationDTO result = repaymentService.calculatePrepayment(applicationId, prepayAmount, userId);
        return Result.success(result);
    }

    @Operation(summary = "执行还款")
    @PostMapping("/pay")
    public Result<RepaymentRecord> executeRepayment(
            @RequestParam Long planId,
            @RequestParam String paymentMethod,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        // 验证支付方式
        if (!"ALIPAY".equals(paymentMethod) && !"WECHAT".equals(paymentMethod)) {
            return Result.error("支付方式无效，仅支持支付宝(ALIPAY)和微信(WECHAT)");
        }
        RepaymentRecord record = repaymentService.executeRepayment(planId, paymentMethod, userId);
        return Result.success(record);
    }
}

