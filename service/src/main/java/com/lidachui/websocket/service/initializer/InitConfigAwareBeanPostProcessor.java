package com.lidachui.websocket.service.initializer;

import cn.hutool.core.collection.CollUtil;
import com.lidachui.websocket.common.annotation.ReadBroadcastConfig;
import com.lidachui.websocket.common.constants.CommonConstants;
import com.lidachui.websocket.dal.model.BroadcastConfig;
import com.lidachui.websocket.service.BroadcastConfigService;
import com.lidachui.websocket.service.policy.AbstractMessageBroadcastPolicy;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;


import java.lang.reflect.Field;
import java.util.*;


/**
 * ZhouyuInstantiationAwareBeanPostProcessor
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/23 23:50
 */
@Component
@Slf4j
public class InitConfigAwareBeanPostProcessor implements BeanPostProcessor {

    @Resource
    private BroadcastConfigService broadcastConfigService;


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AbstractMessageBroadcastPolicy) {
            Class<?> clazz = bean.getClass();
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (declaredField.isAnnotationPresent(ReadBroadcastConfig.class)) {
                    ReadBroadcastConfig annotation = declaredField.getAnnotation(ReadBroadcastConfig.class);
                    String policy = annotation.policy();
                    BroadcastConfig policyQuery = BroadcastConfig.builder()
                            .policy(policy.toLowerCase()).isEnable(CommonConstants.TRUE).build();
                    List<BroadcastConfig> configs = broadcastConfigService.listConfig(policyQuery);
                    if (CollUtil.isNotEmpty(configs)) {
                        declaredField.setAccessible(true);
                        try {
                            declaredField.set(bean, configs);
                        } catch (Exception e) {
                            log.error(e.getMessage());
                        }
                    }
                }
            }
        }
        return bean;
    }

}
