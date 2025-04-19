package com.kanyu.chat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.kanyu.chat.mapper")
@SpringBootApplication
@EnableScheduling // 启用定时任务支持
public class ChatServeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatServeApplication.class, args);
    }

}
