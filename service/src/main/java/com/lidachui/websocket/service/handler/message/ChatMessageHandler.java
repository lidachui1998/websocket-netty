package com.lidachui.websocket.service.handler.message;

import cn.hutool.core.util.StrUtil;
import com.lidachui.websocket.common.constants.CommonConstants;
import com.lidachui.websocket.common.constants.MessageType;
import com.lidachui.websocket.common.util.JsonUtils;
import com.lidachui.websocket.dal.model.WebSocketMessage;
import com.lidachui.websocket.service.BroadcastMessages;
import com.lidachui.websocket.service.config.WebsocketConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.lidachui.websocket.common.constants.CommonConstants.NONE_STR;
import static com.lidachui.websocket.common.constants.MessageType.CHAT;

/**
 * ChatMessageHandler
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/16 23:23
 */
@Service
@Slf4j
public class ChatMessageHandler implements MessageHandler {

    /**
     * 获取该处理器能够处理的消息类型
     *
     * @return 消息处理器类型
     */
    @Override
    public String getMessageType() {
        return CHAT;
    }

    /**
     * 处理消息
     */
    @Override
    public void handleMessage(Channel channel, WebSocketMessage message) {
        ConcurrentHashMap<String, Channel> userChannelMap = WebsocketConfig.getUserChannelMap();
        String userId = userChannelMap.entrySet().stream().filter(entry -> entry.getValue() == channel).map(Map.Entry::getKey).findFirst().orElse(null);
        if (StrUtil.equals(userId, message.getSender())) {
            String sender = message.getSender();
            if (StrUtil.isNotEmpty(sender)) {
                // 获取接收者用户名
                String receiver = message.getReceiver();
                String sendMsg = JsonUtils.toJson(message);
                // 如果接收者不为空，则发送给指定的用户
                if (StrUtil.isNotEmpty(receiver)) {
                    Channel receiverChannel = userChannelMap.get(receiver);
                    if (!Objects.isNull(receiverChannel) && receiverChannel.isActive()) {
                        receiverChannel.writeAndFlush(new TextWebSocketFrame(sendMsg));
                        message.setIsSend(CommonConstants.TRUE);
                    }else {
                        message.setIsSend(CommonConstants.FALSE);
                    }
                    //广播消息出去，其它服务消费
                    BroadcastMessages.broadcast(message, NONE_STR, NONE_STR, CHAT);
                }
            }
        }
    }
}
