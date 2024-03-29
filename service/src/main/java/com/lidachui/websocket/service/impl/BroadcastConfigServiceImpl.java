package com.lidachui.websocket.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.lidachui.websocket.common.annotation.ReadBroadcastConfig;
import com.lidachui.websocket.common.constants.CommonConstants;
import com.lidachui.websocket.common.constants.MessageBroadcastPolicyType;
import com.lidachui.websocket.common.util.SpringUtil;
import com.lidachui.websocket.dal.mapper.BroadcastConfigMapper;
import com.lidachui.websocket.dal.model.BroadcastConfig;
import com.lidachui.websocket.service.BroadcastConfigService;
import com.lidachui.websocket.service.policy.AbstractMessageBroadcastPolicy;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

/**
 * BroadcastConfigServiceImpl
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/19 11:33
 */
@Service
@Slf4j
public class BroadcastConfigServiceImpl implements BroadcastConfigService {
    @Resource
    private BroadcastConfigMapper broadcastConfigMapper;


    /**
     * 查询多个通用查询
     */
    @Override
    public List<BroadcastConfig> listConfig(BroadcastConfig broadcastConfig) {
        return broadcastConfigMapper.listConfig(broadcastConfig);
    }

    /**
     * 查询单个通用查询
     */
    @Override
    public BroadcastConfig getConfig(BroadcastConfig broadcastConfig) {
        return broadcastConfigMapper.getConfig(broadcastConfig);
    }

    /**
     * 刷新配置
     */
    @Override
    public void refreshConfig() {
        Map<String, AbstractMessageBroadcastPolicy> messageBroadcastPolicyMap = SpringUtil.getBeansOfType(AbstractMessageBroadcastPolicy.class);
        for (Map.Entry<String, AbstractMessageBroadcastPolicy> entry : messageBroadcastPolicyMap.entrySet()) {
            AbstractMessageBroadcastPolicy policy = entry.getValue();
            Class<?> clazz = policy.getClass();
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (declaredField.isAnnotationPresent(ReadBroadcastConfig.class)) {
                    ReadBroadcastConfig annotation = declaredField.getAnnotation(ReadBroadcastConfig.class);
                    MessageBroadcastPolicyType policyType = annotation.policy();
                    BroadcastConfig policyQuery = BroadcastConfig.builder()
                            .policy(policyType.name().toLowerCase())
                            .isEnable(CommonConstants.TRUE).build();
                    List<BroadcastConfig> configs = listConfig(policyQuery);
                    if (CollUtil.isNotEmpty(configs)) {
                        declaredField.setAccessible(true);
                        try {
                            declaredField.set(policy, configs);
                        } catch (Exception e) {
                            log.error(e.getMessage());
                        }
                    }
                }
            }
        }
    }


}
