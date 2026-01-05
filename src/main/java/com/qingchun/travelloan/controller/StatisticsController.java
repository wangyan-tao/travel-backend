package com.qingchun.travelloan.controller;

import com.qingchun.travelloan.dto.StatisticsDTO;
import com.qingchun.travelloan.dto.StatisticsFilterDTO;
import com.qingchun.travelloan.service.StatisticsService;
import com.qingchun.travelloan.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/statistics")
@Tag(name = "统计数据", description = "数据统计相关接口")
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    @GetMapping("/full")
    @Operation(summary = "获取完整统计数据", description = "获取所有统计数据，支持日期范围和用户类型筛选")
    public Result<StatisticsDTO.FullStatistics> getFullStatistics(
            @Parameter(description = "开始日期 (格式: yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期 (格式: yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "用户类型 (ALL-全部, STUDENT-学生, GRADUATE-毕业生)")
            @RequestParam(required = false, defaultValue = "ALL") String userType
    ) {
        StatisticsFilterDTO filter = new StatisticsFilterDTO();
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);
        filter.setUserType(userType);
        
        StatisticsDTO.FullStatistics statistics = statisticsService.getFullStatistics(filter);
        return Result.success(statistics);
    }
    
    @GetMapping("/overview")
    @Operation(summary = "获取概览统计", description = "获取总用户数、总申请数、待审批数、总贷款额，支持筛选")
    public Result<StatisticsDTO.Overview> getOverview(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "ALL") String userType
    ) {
        StatisticsFilterDTO filter = new StatisticsFilterDTO();
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);
        filter.setUserType(userType);
        
        StatisticsDTO.Overview overview = statisticsService.getOverview(filter);
        return Result.success(overview);
    }
    
    @GetMapping("/user-growth")
    @Operation(summary = "获取用户增长数据", description = "获取用户增长趋势，支持筛选")
    public Result<List<StatisticsDTO.UserGrowth>> getUserGrowth(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "ALL") String userType
    ) {
        StatisticsFilterDTO filter = new StatisticsFilterDTO();
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);
        filter.setUserType(userType);
        
        List<StatisticsDTO.UserGrowth> data = statisticsService.getUserGrowth(filter);
        return Result.success(data);
    }
    
    @GetMapping("/loan-status")
    @Operation(summary = "获取贷款状态分布", description = "获取贷款申请的状态分布，支持筛选")
    public Result<List<StatisticsDTO.LoanStatus>> getLoanStatus(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "ALL") String userType
    ) {
        StatisticsFilterDTO filter = new StatisticsFilterDTO();
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);
        filter.setUserType(userType);
        
        List<StatisticsDTO.LoanStatus> data = statisticsService.getLoanStatus(filter);
        return Result.success(data);
    }
    
    @GetMapping("/loan-products")
    @Operation(summary = "获取贷款产品分布", description = "获取各贷款产品的申请数量分布，支持筛选")
    public Result<List<StatisticsDTO.LoanProduct>> getLoanProducts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "ALL") String userType
    ) {
        StatisticsFilterDTO filter = new StatisticsFilterDTO();
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);
        filter.setUserType(userType);
        
        List<StatisticsDTO.LoanProduct> data = statisticsService.getLoanProducts(filter);
        return Result.success(data);
    }
    
    @GetMapping("/repayment-status")
    @Operation(summary = "获取还款情况", description = "获取按时还款和逾期还款数据，支持筛选")
    public Result<List<StatisticsDTO.RepaymentStatus>> getRepaymentStatus(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "ALL") String userType
    ) {
        StatisticsFilterDTO filter = new StatisticsFilterDTO();
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);
        filter.setUserType(userType);
        
        List<StatisticsDTO.RepaymentStatus> data = statisticsService.getRepaymentStatus(filter);
        return Result.success(data);
    }
}
