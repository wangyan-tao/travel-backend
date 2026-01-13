package com.qingchun.travelloan.controller;

import com.qingchun.travelloan.service.AdminOverdueService;
import com.qingchun.travelloan.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员预期管理控制器
 *
 * @author Qingchun Team
 */
@Tag(name = "管理员-预期管理")
@RestController
@RequestMapping("/admin/overdue")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOverdueController {

    @Autowired
    private AdminOverdueService adminOverdueService;

    @Operation(summary = "获取逾期用户列表")
    @GetMapping("/users")
    public Result<List<AdminOverdueService.OverdueUserDTO>> getOverdueUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String riskLevel) {
        List<AdminOverdueService.OverdueUserDTO> users = adminOverdueService.getOverdueUsers(keyword, riskLevel);
        return Result.success(users);
    }

    @Operation(summary = "获取逾期统计数据")
    @GetMapping("/statistics")
    public Result<AdminOverdueService.OverdueStatisticsDTO> getStatistics() {
        AdminOverdueService.OverdueStatisticsDTO statistics = adminOverdueService.getStatistics();
        return Result.success(statistics);
    }
}

