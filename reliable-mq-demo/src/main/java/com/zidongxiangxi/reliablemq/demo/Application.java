package com.zidongxiangxi.reliablemq.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author chenxudong
 */
@SpringBootApplication
@ComponentScan(value = "com.zidongxiangxi")
@EnableAsync
@EnableTransactionManagement
public class Application {

    public static void main(String[] args) {
        new SpringApplication(Application.class).run(args);
    }
}
