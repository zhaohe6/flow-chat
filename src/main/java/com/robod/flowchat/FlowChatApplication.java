package com.robod.flowchat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.robod.flowchat.mapper") // 扫描 Mapper 接口
public class FlowChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlowChatApplication.class, args);
    }

}
