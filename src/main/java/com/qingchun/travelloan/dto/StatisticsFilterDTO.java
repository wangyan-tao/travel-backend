package com.qingchun.travelloan.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class StatisticsFilterDTO {
    
    /**
     * 开始日期
     */
    private LocalDate startDate;
    
    /**
     * 结束日期
     */
    private LocalDate endDate;
    
    /**
     * 用户类型（可选值：ALL-全部, STUDENT-学生, GRADUATE-毕业生）
     */
    private String userType;
    
    /**
     * 验证并设置默认值
     */
    public void setDefaults() {
        if (startDate == null) {
            // 默认最近一年
            startDate = LocalDate.now().minusYears(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (userType == null || userType.isEmpty()) {
            userType = "ALL";
        }
    }
}
