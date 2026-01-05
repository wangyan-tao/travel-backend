package com.qingchun.travelloan.service;

import com.qingchun.travelloan.dto.StatisticsDTO;
import com.qingchun.travelloan.dto.StatisticsFilterDTO;
import com.qingchun.travelloan.mapper.StatisticsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsService {
    
    @Autowired
    private StatisticsMapper statisticsMapper;
    
    /**
     * 获取完整统计数据（支持筛选）
     */
    public StatisticsDTO.FullStatistics getFullStatistics(StatisticsFilterDTO filter) {
        if (filter == null) {
            filter = new StatisticsFilterDTO();
        }
        filter.setDefaults();
        
        StatisticsDTO.FullStatistics statistics = new StatisticsDTO.FullStatistics();
        
        // 概览数据
        StatisticsDTO.Overview overview = new StatisticsDTO.Overview();
        overview.setTotalUsers(statisticsMapper.getTotalUsers(filter));
        overview.setTotalApplications(statisticsMapper.getTotalApplications(filter));
        overview.setPendingApplications(statisticsMapper.getPendingApplications(filter));
        overview.setTotalLoanAmount(statisticsMapper.getTotalLoanAmount(filter));
        statistics.setOverview(overview);
        
        // 用户增长数据
        List<StatisticsDTO.UserGrowth> userGrowth = statisticsMapper.getUserGrowthData(filter);
        statistics.setUserGrowth(userGrowth);
        
        // 贷款状态分布
        List<StatisticsDTO.LoanStatus> loanStatus = statisticsMapper.getLoanStatusDistribution(filter);
        statistics.setLoanStatus(loanStatus);
        
        // 贷款产品分布
        List<StatisticsDTO.LoanProduct> loanProducts = statisticsMapper.getLoanProductDistribution(filter);
        statistics.setLoanProducts(loanProducts);
        
        // 还款情况
        List<StatisticsDTO.RepaymentStatus> repaymentStatus = statisticsMapper.getRepaymentStatusData(filter);
        statistics.setRepaymentStatus(repaymentStatus);
        
        return statistics;
    }
    
    /**
     * 获取概览统计（支持筛选）
     */
    public StatisticsDTO.Overview getOverview(StatisticsFilterDTO filter) {
        if (filter == null) {
            filter = new StatisticsFilterDTO();
        }
        filter.setDefaults();
        
        StatisticsDTO.Overview overview = new StatisticsDTO.Overview();
        overview.setTotalUsers(statisticsMapper.getTotalUsers(filter));
        overview.setTotalApplications(statisticsMapper.getTotalApplications(filter));
        overview.setPendingApplications(statisticsMapper.getPendingApplications(filter));
        overview.setTotalLoanAmount(statisticsMapper.getTotalLoanAmount(filter));
        return overview;
    }
    
    /**
     * 获取用户增长数据（支持筛选）
     */
    public List<StatisticsDTO.UserGrowth> getUserGrowth(StatisticsFilterDTO filter) {
        if (filter == null) {
            filter = new StatisticsFilterDTO();
        }
        filter.setDefaults();
        return statisticsMapper.getUserGrowthData(filter);
    }
    
    /**
     * 获取贷款状态分布（支持筛选）
     */
    public List<StatisticsDTO.LoanStatus> getLoanStatus(StatisticsFilterDTO filter) {
        if (filter == null) {
            filter = new StatisticsFilterDTO();
        }
        filter.setDefaults();
        return statisticsMapper.getLoanStatusDistribution(filter);
    }
    
    /**
     * 获取贷款产品分布（支持筛选）
     */
    public List<StatisticsDTO.LoanProduct> getLoanProducts(StatisticsFilterDTO filter) {
        if (filter == null) {
            filter = new StatisticsFilterDTO();
        }
        filter.setDefaults();
        return statisticsMapper.getLoanProductDistribution(filter);
    }
    
    /**
     * 获取还款情况（支持筛选）
     */
    public List<StatisticsDTO.RepaymentStatus> getRepaymentStatus(StatisticsFilterDTO filter) {
        if (filter == null) {
            filter = new StatisticsFilterDTO();
        }
        filter.setDefaults();
        return statisticsMapper.getRepaymentStatusData(filter);
    }
}
