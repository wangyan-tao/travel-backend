package com.qingchun.travelloan.dto;

import lombok.Data;
import java.util.List;

@Data
public class StatisticsDTO {
    
    // 概览统计
    @Data
    public static class Overview {
        private Long totalUsers;
        private Long totalApplications;
        private Long pendingApplications;
        private String totalLoanAmount;
    }
    
    // 用户增长数据
    @Data
    public static class UserGrowth {
        private String month;
        private Long users;
    }
    
    // 贷款状态分布
    @Data
    public static class LoanStatus {
        private String name;
        private Long value;
        private String color;
    }
    
    // 贷款产品分布
    @Data
    public static class LoanProduct {
        private String product;
        private Long count;
    }
    
    // 还款情况
    @Data
    public static class RepaymentStatus {
        private String month;
        private Long onTime;
        private Long late;
    }
    
    // 完整统计数据
    @Data
    public static class FullStatistics {
        private Overview overview;
        private List<UserGrowth> userGrowth;
        private List<LoanStatus> loanStatus;
        private List<LoanProduct> loanProducts;
        private List<RepaymentStatus> repaymentStatus;
    }
}
