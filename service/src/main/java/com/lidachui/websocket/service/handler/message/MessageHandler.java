package com.lidachui.websocket.service.handler.message;

import com.lidachui.websocket.dal.model.WebSocketMessage;
import io.netty.channel.Channel;


/**
 * MessageHandler
 *
 * @Author lihuijie
 * @Description: 消息处理接口
 * @SINCE 2023/4/16 23:20
 */
public interface MessageHandler {
    /**
     * 获取该处理器能够处理的消息类型
     *
     * @return 消息处理器类型
     */
    String getMessageType();

    /**
     * 处理消息
     *
     * @param channel 通道
     * @param message 消息
     */
    void handleMessage(Channel channel, WebSocketMessage message);

}
