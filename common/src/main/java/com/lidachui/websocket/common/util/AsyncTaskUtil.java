package com.lidachui.websocket.common.util;

import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author lihuijie
 * @date 2023/06/11 15:14
 * @description
 */
@Component
@Slf4j
public class AsyncTaskUtil implements InitializingBean {

    private static AsyncTaskUtil asyncTaskUtil;

    @Resource(name = "asyncThreadPool")
    private Executor asyncExecutor;

    /**
     * 将自身实例化给静态变量 asyncTaskUtil，以便后续调用
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        asyncTaskUtil = this;
    }

    /**
     * 异步执行单个任务
     *
     * @param r 待执行的任务
     */
    public static void execute(Runnable r) {
        asyncTaskUtil.asyncExecutor.execute(r);
    }

    /**
     * 异步执行多个任务
     *
     * @param tasks 待执行的任务数组
     */
    public static void multiExecute(Runnable[] tasks) {
        if (tasks.length == 0) {
            return;
        }
        CompletableFuture<?>[] completableFutures = new CompletableFuture<?>[tasks.length];
        for (int i = 0; i < tasks.length; i++) {
            completableFutures[i] = CompletableFuture.runAsync(tasks[i], asyncTaskUtil.asyncExecutor);
        }
        CompletableFuture.allOf(completableFutures).join();
    }

    /**
     * 异步执行指定的任务并返回执行结果
     *
     * @param task 待执行的任务
     * @return 异步执行的结果
     */
    public static <T> CompletableFuture<T> doTaskAsync(Callable<T> task) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, asyncTaskUtil.asyncExecutor);
    }

}
