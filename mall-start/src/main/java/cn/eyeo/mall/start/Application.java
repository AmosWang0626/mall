package cn.eyeo.mall.start;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot Starter
 *
 * @author AmosWang
 */
@EnableScheduling
@MapperScan("cn.eyeo.mall.gateway.impl.*.database.mapper")
@SpringBootApplication(scanBasePackages = {"cn.eyeo.mall", "com.alibaba.cola"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
