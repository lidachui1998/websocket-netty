package com.lidachui.websocket.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.lidachui.websocket.api.request.WebSocketMessageRequest;
import com.lidachui.websocket.common.util.ObjectUtil;
import com.lidachui.websocket.common.util.UUIDGenerator;
import com.lidachui.websocket.dal.model.WebSocketMessage;
import com.lidachui.websocket.manager.client.DiaryClient;
import com.lidachui.websocket.service.MessageSendService;
import com.lidachui.websocket.service.MessageService;
import com.lidachui.websocket.service.config.WebsocketConfig;
import com.lidachui.websocket.service.handler.MyWebSocketHandler;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import javax.annotation.Resource;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MessageSendServiceImpl
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/11 9:26
 */
@Service
public class MessageSendServiceImpl implements MessageSendService {
    @Resource
    private MyWebSocketHandler webSocketHandler;
    @Resource
    private MessageService messageService;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private DiaryClient diaryClient;


    /**
     * 发送消息
     */
    @Override
    public void sendMessage(WebSocketMessageRequest request) {
        if (CollUtil.isNotEmpty(request.getReceiverList())) {
            List<String> receiverList = request.getReceiverList();
            for (String receiverId : receiverList) {
                WebSocketMessage webSocketMessage = modelMapper.map(request, WebSocketMessage.class);
                webSocketMessage.setReceiver(receiverId);
                realSendMessage(webSocketMessage);
            }
        } else {
            WebSocketMessage webSocketMessage = modelMapper.map(request, WebSocketMessage.class);
            realSendMessage(webSocketMessage);
        }
    }

    /**
     * 去除
     *
     * @param userId 用户id
     */
    @Override
    public void remove(String userId) {
        ChannelGroup channelGroup = WebsocketConfig.getChannelGroup();
        ConcurrentHashMap<Channel, String> userChannelMap = WebsocketConfig.getUserChannelMap();
        List<Channel> channelList = ObjectUtil.getKeysByValue(userChannelMap, userId);
        for (Channel channel : channelList) {
            channelGroup.remove(channel);
        }
    }

    /**
     * 真实发送消息
     *
     * @param webSocketMessage web套接字消息
     */
    private void realSendMessage(WebSocketMessage webSocketMessage) {
        webSocketMessage
                .setMessageId(UUIDGenerator.uuid())
                .setSendTime(new Date());
        webSocketHandler.sendMessage(webSocketMessage);
    }

}
