package com.lidachui.websocket.service.listener;

import com.lidachui.websocket.common.util.RedisPubSubUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * TestChannelListener
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/18 21:35
 */
@Component
@Slf4j
public class TestChannelListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String msg = new String(message.getBody());
        log.info("Receive message from channel " + channel + ": " + msg);
        // 在这里进行消息处理
        String result = "Hello, " + msg + "!";
        // 发送处理结果
        RedisPubSubUtil.publish(channel + "-response", result);
    }

}
