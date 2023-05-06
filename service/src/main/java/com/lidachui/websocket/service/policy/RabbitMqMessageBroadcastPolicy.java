package com.lidachui.websocket.service.policy;

import com.lidachui.websocket.dal.model.BroadcastMessage;
import org.springframework.stereotype.Service;




/**
 * RabbitMQMessageBroadcastPolicy
 *
 * @Author lihuijie
 * @Description: RabbitMQ消息广播策略
 * @SINCE 2023/4/17 23:00
 */
@Service("rabbitmqMessageBroadcastPolicy")
public class RabbitMqMessageBroadcastPolicy extends AbstractMessageBroadcastPolicy {

    /**
     * 执行具体的广播消息逻辑，子类必须实现
     *
     * @param broadcastMessage 广播消息
     */
    @Override
    protected void doBroadcast(BroadcastMessage broadcastMessage) {
        // TODO document why this method is empty
    }
}
