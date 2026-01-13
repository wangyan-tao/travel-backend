package com.qingchun.travelloan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingchun.travelloan.entity.LoanProduct;
import com.qingchun.travelloan.entity.OverdueRiskUser;
import com.qingchun.travelloan.entity.User;
import com.qingchun.travelloan.mapper.LoanProductMapper;
import com.qingchun.travelloan.mapper.OverdueRiskUserMapper;
import com.qingchun.travelloan.mapper.UserMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员预期管理服务类
 *
 * @author Qingchun Team
 */
@Service
public class AdminOverdueService {

    @Autowired
    private OverdueRiskUserMapper overdueRiskUserMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoanProductMapper loanProductMapper;

    /**
     * 逾期用户DTO
     */
    @Data
    public static class OverdueUserDTO {
        private Long userId;
        private String username;
        private String phone;
        private String riskLevel;
        private BigDecimal overdueAmount;
        private Integer overdueDays;
        private String productName;
        private LocalDate lastRepaymentDate;
    }

    /**
     * 逾期统计数据DTO
     */
    @Data
    public static class OverdueStatisticsDTO {
        private Integer totalRiskUsers;
        private BigDecimal totalOverdueAmount;
        private Integer averageOverdueDays;
        private Integer highRiskUsers;
        private List<RiskLevelDistribution> riskLevelDistribution;
        private List<OverdueTrend> overdueTrend;
        private List<OverdueDaysDistribution> overdueDaysDistribution;
    }

    @Data
    public static class RiskLevelDistribution {
        private String name;
        private Integer value;
        private BigDecimal amount;
    }

    @Data
    public static class OverdueTrend {
        private String month;
        private Integer overdueCount;
        private BigDecimal overdueAmount;
    }

    @Data
    public static class OverdueDaysDistribution {
        private String range;
        private Integer count;
    }


    /**
     * 获取逾期用户列表
     */
    public List<OverdueUserDTO> getOverdueUsers(String keyword, String riskLevel) {
        QueryWrapper<OverdueRiskUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "ACTIVE");

        // 风险等级过滤
        if (riskLevel != null && !riskLevel.trim().isEmpty() && !"ALL".equals(riskLevel)) {
            queryWrapper.eq("risk_level", riskLevel);
        }

        List<OverdueRiskUser> riskUsers = overdueRiskUserMapper.selectList(queryWrapper);

        // 转换为DTO
        List<OverdueUserDTO> result = new ArrayList<>();
        for (OverdueRiskUser riskUser : riskUsers) {
            User user = userMapper.selectById(riskUser.getUserId());
            LoanProduct product = loanProductMapper.selectById(riskUser.getProductId());

            // 关键词过滤
            if (keyword != null && !keyword.trim().isEmpty()) {
                String keywordLower = keyword.toLowerCase();
                if (user != null && 
                    !user.getUsername().toLowerCase().contains(keywordLower) &&
                    !user.getPhone().contains(keyword)) {
                    continue;
                }
            }

            if (user != null && product != null) {
                OverdueUserDTO dto = new OverdueUserDTO();
                dto.setUserId(riskUser.getUserId());
                dto.setUsername(user.getUsername());
                dto.setPhone(user.getPhone());
                dto.setRiskLevel(riskUser.getRiskLevel());
                dto.setOverdueAmount(riskUser.getOverdueAmount());
                dto.setOverdueDays(riskUser.getOverdueDays());
                dto.setProductName(product.getProductName());
                dto.setLastRepaymentDate(riskUser.getLastRepaymentDate());
                result.add(dto);
            }
        }

        return result;
    }

    /**
     * 获取统计数据
     */
    public OverdueStatisticsDTO getStatistics() {
        OverdueStatisticsDTO statistics = new OverdueStatisticsDTO();

        // 查询所有有效的风险用户
        QueryWrapper<OverdueRiskUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "ACTIVE");
        List<OverdueRiskUser> riskUsers = overdueRiskUserMapper.selectList(queryWrapper);

        // 计算统计数据
        statistics.setTotalRiskUsers(riskUsers.size());
        
        BigDecimal totalAmount = riskUsers.stream()
                .map(OverdueRiskUser::getOverdueAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.setTotalOverdueAmount(totalAmount);

        if (!riskUsers.isEmpty()) {
            double avgDays = riskUsers.stream()
                    .mapToInt(OverdueRiskUser::getOverdueDays)
                    .average()
                    .orElse(0.0);
            statistics.setAverageOverdueDays((int) Math.round(avgDays));
        } else {
            statistics.setAverageOverdueDays(0);
        }

        statistics.setHighRiskUsers(
                (int) riskUsers.stream()
                        .filter(user -> "高风险".equals(user.getRiskLevel()) || "严重风险".equals(user.getRiskLevel()))
                        .count()
        );

        // 风险等级分布
        Map<String, List<OverdueRiskUser>> groupedByRisk = riskUsers.stream()
                .collect(Collectors.groupingBy(OverdueRiskUser::getRiskLevel));

        List<RiskLevelDistribution> riskDistribution = new ArrayList<>();
        String[] riskLevels = {"低风险", "中风险", "高风险", "严重风险"};
        for (String level : riskLevels) {
            List<OverdueRiskUser> users = groupedByRisk.getOrDefault(level, new ArrayList<>());
            BigDecimal amount = users.stream()
                    .map(OverdueRiskUser::getOverdueAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            riskDistribution.add(createRiskDistribution(level, users.size(), amount));
        }
        statistics.setRiskLevelDistribution(riskDistribution);

        // 逾期趋势：按last_repayment_date字段的月份分组统计
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        // 按最后还款日的月份分组，统计每个月的逾期用户数和逾期金额
        Map<String, List<OverdueRiskUser>> groupedByMonth = riskUsers.stream()
                .filter(user -> user.getLastRepaymentDate() != null)
                .collect(Collectors.groupingBy(
                        user -> user.getLastRepaymentDate().format(monthFormatter)
                ));
        
        // 转换为趋势数据并按月份排序
        List<OverdueTrend> trend = groupedByMonth.entrySet().stream()
                .map(entry -> {
                    String month = entry.getKey();
                    List<OverdueRiskUser> monthUsers = entry.getValue();
                    int count = monthUsers.size();
                    BigDecimal amount = monthUsers.stream()
                            .map(OverdueRiskUser::getOverdueAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return createTrend(month, count, amount);
                })
                .sorted(Comparator.comparing(OverdueTrend::getMonth))  // 按月份排序
                .collect(Collectors.toList());
        
        statistics.setOverdueTrend(trend);

        // 逾期天数分布
        long count1_30 = riskUsers.stream().filter(u -> u.getOverdueDays() >= 1 && u.getOverdueDays() <= 30).count();
        long count31_60 = riskUsers.stream().filter(u -> u.getOverdueDays() >= 31 && u.getOverdueDays() <= 60).count();
        long count61_90 = riskUsers.stream().filter(u -> u.getOverdueDays() >= 61 && u.getOverdueDays() <= 90).count();
        long count91_120 = riskUsers.stream().filter(u -> u.getOverdueDays() >= 91 && u.getOverdueDays() <= 120).count();
        long count120plus = riskUsers.stream().filter(u -> u.getOverdueDays() > 120).count();

        List<OverdueDaysDistribution> daysDistribution = new ArrayList<>();
        daysDistribution.add(createDaysDistribution("1-30天", (int) count1_30));
        daysDistribution.add(createDaysDistribution("31-60天", (int) count31_60));
        daysDistribution.add(createDaysDistribution("61-90天", (int) count61_90));
        daysDistribution.add(createDaysDistribution("91-120天", (int) count91_120));
        daysDistribution.add(createDaysDistribution("120天以上", (int) count120plus));
        statistics.setOverdueDaysDistribution(daysDistribution);

        return statistics;
    }

    private RiskLevelDistribution createRiskDistribution(String name, int value, BigDecimal amount) {
        RiskLevelDistribution dist = new RiskLevelDistribution();
        dist.setName(name);
        dist.setValue(value);
        dist.setAmount(amount);
        return dist;
    }

    private OverdueTrend createTrend(String month, int count, BigDecimal amount) {
        OverdueTrend trend = new OverdueTrend();
        trend.setMonth(month);
        trend.setOverdueCount(count);
        trend.setOverdueAmount(amount);
        return trend;
    }

    private OverdueDaysDistribution createDaysDistribution(String range, int count) {
        OverdueDaysDistribution dist = new OverdueDaysDistribution();
        dist.setRange(range);
        dist.setCount(count);
        return dist;
    }
}

