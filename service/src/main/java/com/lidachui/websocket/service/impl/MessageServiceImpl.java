package com.lidachui.websocket.service.impl;

import com.lidachui.websocket.dal.mapper.MessageMapper;
import com.lidachui.websocket.dal.model.WebSocketMessage;
import com.lidachui.websocket.service.MessageService;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * MessageServiceImpl
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/18 23:52
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageMapper messageMapper;

    /**
     * 保存消息
     *
     * @param message 消息
     */
    @Override
    public void save(WebSocketMessage message) {
        messageMapper.insertSelective(message);
    }
}
