package com.qingchun.travelloan.mapper;

import com.qingchun.travelloan.dto.StatisticsDTO;
import com.qingchun.travelloan.dto.StatisticsFilterDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface StatisticsMapper {
    
    // 获取总用户数（支持筛选）
    Long getTotalUsers(@Param("filter") StatisticsFilterDTO filter);
    
    // 获取总申请数（支持筛选）
    Long getTotalApplications(@Param("filter") StatisticsFilterDTO filter);
    
    // 获取待审批数（支持筛选）
    Long getPendingApplications(@Param("filter") StatisticsFilterDTO filter);
    
    // 获取总贷款金额（支持筛选）
    String getTotalLoanAmount(@Param("filter") StatisticsFilterDTO filter);
    
    // 获取用户增长数据（支持筛选）
    List<StatisticsDTO.UserGrowth> getUserGrowthData(@Param("filter") StatisticsFilterDTO filter);
    
    // 获取贷款状态分布（支持筛选）
    List<StatisticsDTO.LoanStatus> getLoanStatusDistribution(@Param("filter") StatisticsFilterDTO filter);
    
    // 获取贷款产品分布（支持筛选）
    List<StatisticsDTO.LoanProduct> getLoanProductDistribution(@Param("filter") StatisticsFilterDTO filter);
    
    // 获取还款情况（支持筛选）
    List<StatisticsDTO.RepaymentStatus> getRepaymentStatusData(@Param("filter") StatisticsFilterDTO filter);
}
