package com.aircookies.smtworkordermanagement;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.aircookies.smtworkordermanagement.mapper")
@EnableScheduling
public class SmtWorkorderManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmtWorkorderManagementApplication.class, args);
    }

}
