package com.qingchun.travelloan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 青春旅贷应用启动类
 * 
 * @author Qingchun Team
 * @since 2024-01-02
 */
@SpringBootApplication
@MapperScan("com.qingchun.travelloan.mapper")
public class TravelLoanApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelLoanApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("青春旅贷后端服务启动成功！");
        System.out.println("API文档地址: http://localhost:8080/api/swagger-ui.html");
        System.out.println("========================================\n");
    }
}
