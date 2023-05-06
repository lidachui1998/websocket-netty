package com.lidachui.websocket.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * SpringUtil
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/18 21:38
 */
@Component
public class SpringUtil implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(SpringUtil.class);

    private static ApplicationContext context;

    /**
     * 注册 ApplicationContext
     */
    @Override
    public void setApplicationContext(@NonNull ApplicationContext context) throws BeansException {
        SpringUtil.context = context;
    }

    /**
     * 根据 bean 的名称获取 bean 实例
     */
    @Nullable
    public static <T> T getBean(@NonNull String beanName) {
        try {
            return (T) context.getBean(beanName);
        } catch (BeansException e) {
            log.error("Failed to get bean with name [{}]: {}", beanName, e.getMessage());
            return null;
        }
    }

    /**
     * 获取指定类型的 Bean 实例
     */
    @Nullable
    public static <T> T getBean(@NonNull Class<T> clazz) {
        try {
            return context.getBean(clazz);
        } catch (BeansException e) {
            log.error("Failed to get bean with type [{}]: {}", clazz.getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * 获取指定类型和名称的 Bean 实例
     */
    @Nullable
    public static <T> T getBean(@NonNull Class<T> clazz, @NonNull String beanName) {
        try {
            return context.getBean(beanName, clazz);
        } catch (BeansException e) {
            log.error("Failed to get bean with name [{}] and type [{}]: {}", beanName, clazz.getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * 获取指定类型和注解的 Bean 实例
     */
    @Nullable
    public static <T> T getBean(@NonNull Class<T> clazz, @NonNull Class<? extends Annotation> qualifier) {
        try {
            return context.getBean(clazz, qualifier);
        } catch (BeansException e) {
            log.error("Failed to get bean with qualifier [{}] and type [{}]: {}", qualifier.getSimpleName(),
                    clazz.getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * 发布事件
     */
    public static void publishEvent(@NonNull ApplicationEvent event) {
        try {
            context.publishEvent(event);
        } catch (Exception e) {
            log.error("Failed to publish event [{}]: {}", event.getClass().getSimpleName(), e.getMessage());
        }
    }

    /**
     * 获取应用名称
     */
    public static String getServiceId() {
        Properties props = System.getProperties();
        return props.getProperty("spring.application.name", props.getProperty("sun.java.command"));
    }

    /**
     * 获取 properties 中指定 key 的值
     */
    public static String getProperty(@NonNull String key) {
        try {
            return context.getEnvironment().getProperty(key);
        } catch (Exception e) {
            log.error("Failed to get property [{}]: {}", key, e.getMessage());
            return null;
        }
    }

    /**
     * 返回该类型所有的bean
     *
     * @param clazz
     * @return
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return context.getBeansOfType(clazz);
    }
}

