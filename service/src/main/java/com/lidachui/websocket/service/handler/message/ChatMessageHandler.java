package com.lidachui.websocket.service.handler.message;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.lidachui.websocket.common.constants.CommonConstants;
import com.lidachui.websocket.common.util.JsonUtils;
import com.lidachui.websocket.common.util.ObjectUtil;
import com.lidachui.websocket.dal.model.WebSocketMessage;
import com.lidachui.websocket.service.impl.BroadcastMessages;
import com.lidachui.websocket.service.config.WebsocketConfig;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.lidachui.websocket.common.constants.CommonConstants.*;
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
        ConcurrentHashMap<Channel, String> userChannelMap = WebsocketConfig.getUserChannelMap();
        String userId = null;
        if (!Objects.isNull(channel)) {
            userId = userChannelMap.get(channel);
        }
        String sendMsg = JsonUtils.toJson(message);
        if (StrUtil.isEmpty(userId)) {
            message.setIsSend(FALSE);
            String receiver = message.getReceiver();
            if (StrUtil.isNotEmpty(receiver)) {
                List<Channel> receiverChannels = getReceiverChannels(userChannelMap, receiver);
                if (CollUtil.isNotEmpty(receiverChannels)) {
                    for (Channel receiverChannel : receiverChannels) {
                        if (receiverChannel.isActive()) {
                            receiverChannel.writeAndFlush(new TextWebSocketFrame(sendMsg));
                            message.setIsSend(CommonConstants.TRUE);
                        }
                    }
                } else {
                    message.setIsSend(CommonConstants.FALSE);
                }
                //广播消息出去，其它服务消费
                BroadcastMessages.broadcast(message, NONE_STR, NONE_STR, CHAT);
            }
        }
        if (StrUtil.equals(userId, message.getSender())) {
            String sender = message.getSender();
            if (StrUtil.isNotEmpty(sender)) {
                // 获取接收者用户名
                String receiver = message.getReceiver();
                // 如果接收者不为空，则发送给指定的用户
                if (StrUtil.isNotEmpty(receiver)) {
                    List<Channel> receiverChannels = getReceiverChannels(userChannelMap, receiver);
                    if (CollUtil.isNotEmpty(receiverChannels)) {
                        for (Channel receiverChannel : receiverChannels) {
                            if (receiverChannel.isActive()) {
                                receiverChannel.writeAndFlush(new TextWebSocketFrame(sendMsg));
                                message.setIsSend(CommonConstants.TRUE);
                            }
                        }
                    } else {
                        message.setIsSend(CommonConstants.FALSE);
                    }
                    //广播消息出去，其它服务消费
                    BroadcastMessages.broadcast(message, NONE_STR, NONE_STR, CHAT);
                }
            }
        }
    }

    /**
     * 获取接收器通道
     *
     * @param userChannelMap 用户通道图
     * @param receiver       接受者
     * @return {@code List<Channel>}
     */
    private static List<Channel> getReceiverChannels(ConcurrentHashMap<Channel, String> userChannelMap, String receiver) {
        return ObjectUtil.getKeysByValue(userChannelMap, receiver);
    }
}
