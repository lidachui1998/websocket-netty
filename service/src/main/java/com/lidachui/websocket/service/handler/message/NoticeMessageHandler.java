package com.lidachui.websocket.service.handler.message;

import cn.hutool.core.collection.CollUtil;
import com.lidachui.websocket.common.constants.MessageType;
import com.lidachui.websocket.common.util.JsonUtils;
import com.lidachui.websocket.common.util.ObjectUtil;
import com.lidachui.websocket.dal.model.WebSocketMessage;
import com.lidachui.websocket.service.config.WebsocketConfig;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.lidachui.websocket.common.constants.CommonConstants.FALSE;
import static com.lidachui.websocket.common.constants.CommonConstants.TRUE;

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
        ConcurrentHashMap<Channel, String> userChannelMap = WebsocketConfig.getUserChannelMap();
        List<Channel> receiverChannels = ObjectUtil.getKeysByValue(userChannelMap, message.getReceiver());
        if (CollUtil.isNotEmpty(receiverChannels)){
            for (Channel receiverChannel : receiverChannels) {
                if (receiverChannel.isActive()){
                    String sendMsg = JsonUtils.toJson(message);
                    message.setIsSend(TRUE);
                    receiverChannel.writeAndFlush(new TextWebSocketFrame(sendMsg));
                }
            }
        }else {
            message.setIsSend(FALSE);
        }
    }
}
