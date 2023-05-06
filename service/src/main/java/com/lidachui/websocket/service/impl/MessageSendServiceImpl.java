package com.lidachui.websocket.service.impl;

import com.lidachui.websocket.api.request.WebSocketMessageRequest;
import com.lidachui.websocket.common.util.JsonUtils;
import com.lidachui.websocket.common.util.UUIDGenerator;
import com.lidachui.websocket.dal.model.WebSocketMessage;
import com.lidachui.websocket.service.MessageSendService;
import com.lidachui.websocket.service.handler.MyWebSocketHandler;
import javax.annotation.Resource;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;

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
    private ModelMapper modelMapper;

    /**
     * 发送消息
     */
    @Override
    public void sendMessage(WebSocketMessageRequest request) {
        WebSocketMessage webSocketMessage = modelMapper.map(request, WebSocketMessage.class);
        JsonUtils.convertToJsonString(webSocketMessage, WebSocketMessage::getHeader, WebSocketMessage::setHeader);
        JsonUtils.convertToJsonString(webSocketMessage, WebSocketMessage::getContent, WebSocketMessage::setContent);
        webSocketMessage
                .setMessageId(UUIDGenerator.uuid())
                .setSendTime(new Date());
        webSocketHandler.sendMessage(webSocketMessage);
    }

}
