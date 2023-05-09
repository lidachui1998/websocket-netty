package com.lidachui.websocket.service.policy;

import cn.hutool.core.collection.CollUtil;
import com.lidachui.websocket.common.annotation.ReadBroadcastConfig;
import com.lidachui.websocket.common.constants.MessageBroadcastPolicyType;
import com.lidachui.websocket.dal.model.BroadcastConfig;
import com.lidachui.websocket.dal.model.BroadcastMessage;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * RabbitMQMessageBroadcastPolicy
 *
 * @Author lihuijie
 * @Description: RabbitMQ消息广播策略
 * @SINCE 2023/4/17 23:00
 */
@Service("rabbitmqMessageBroadcastPolicy")
public class RabbitMqMessageBroadcastPolicy extends AbstractMessageBroadcastPolicy {

    @ReadBroadcastConfig(policy = MessageBroadcastPolicyType.RABBITMQ)
    private final List<BroadcastConfig> configs;

    public RabbitMqMessageBroadcastPolicy(List<BroadcastConfig> configs) {
        this.configs = configs;
    }

    /**
     * 执行具体的广播消息逻辑，子类必须实现
     *
     * @param broadcastMessage 广播消息
     */
    @Override
    protected void doBroadcast(BroadcastMessage broadcastMessage) {
        if (CollUtil.isNotEmpty(configs)){

        }
        // TODO document why this method is empty
    }
}
