package com.lidachui.websocket.web;


import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;


/**
 * WebSocketApplication
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/10 22:12
 */
@MapperScan(basePackages = "com.lidachui.websocket.dal.mapper")
@ComponentScan("com.lidachui")
@SpringBootApplication
@EnableCaching
@EnableScheduling //第一种方式
public class WebSocketApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(WebSocketApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WebSocketApplication.class);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
