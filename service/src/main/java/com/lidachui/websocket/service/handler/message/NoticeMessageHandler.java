package com.lidachui.websocket.service.handler.message;

import com.lidachui.websocket.common.constants.MessageType;
import com.lidachui.websocket.dal.model.WebSocketMessage;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

/**
 * NoticeMessageHandler
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/17 22:24
 */
@Service
public class NoticeMessageHandler implements MessageHandler {
    /**
     * 获取该处理器能够处理的消息类型
     */
    @Override
    public String getMessageType() {
        return MessageType.NOTICE;
    }

    /**
     * 处理消息
     */
    @Override
    public void handleMessage(Channel channel, WebSocketMessage message) {
        System.out.println("通知");
    }
}
