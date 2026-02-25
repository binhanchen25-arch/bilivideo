package com.imooc;

import com.bilibili.service.WebSocketService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author: Dk.L
 * @date: 2023/3/18
 * @Description: 启动类
 */
@SpringBootApplication(scanBasePackages = {"com.imooc.bilibili.api", "com.bilibili.service"})
@MapperScan(basePackages = "com.bilibili.dao")
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
public class ImoocBilibiliApp {
    public static void main(String[] args) {
        ApplicationContext app = SpringApplication.run(ImoocBilibiliApp.class, args);
        WebSocketService.setApplicationContext(app);
    }
}
