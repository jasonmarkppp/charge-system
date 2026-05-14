package com.experiment.report;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 中小学实验报告评价系统 启动类
 */
@SpringBootApplication
@MapperScan("com.experiment.report.mapper")
public class ExperimentReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExperimentReportApplication.class, args);
        System.out.println("========================================");
        System.out.println("  中小学实验报告评价系统启动成功！");
        System.out.println("  接口地址: http://localhost:8088/api");
        System.out.println("  文档地址: http://localhost:8088/api/doc.html");
        System.out.println("========================================");
    }
}
