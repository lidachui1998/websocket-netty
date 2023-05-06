package com.lidachui.websocket.service.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPoolConfig
 *
 * @Author lihuijie
 * @Description: 线程池配置
 * @SINCE 2023/4/12 23:33
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Bean(destroyMethod = "shutdown", name = "websocketThreadPool")
    public ThreadPoolExecutor websocketThreadPool() {
        ThreadFactory tf = new ThreadFactoryBuilder().setNameFormat(
                "websocketThreadPool-%d").build();
        return new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors(),
                200,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(10),
                tf,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }


    @Bean(destroyMethod = "shutdown", name = "commonThreadPool")
    public ThreadPoolExecutor commonThreadPool() {
        ThreadFactory tf = new ThreadFactoryBuilder().setNameFormat(
                "CommonThreadPool-%d").build();
        return new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors(),
                10,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                tf,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Bean(destroyMethod = "shutdown", name = "asyncThreadPool")
    public ThreadPoolExecutor asyncTaskThreadPool() {
        ThreadFactory tf = new ThreadFactoryBuilder().setNameFormat(
                "asyncThreadPool-%d").build();
        return new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors(),
                10,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2000),
                tf,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
