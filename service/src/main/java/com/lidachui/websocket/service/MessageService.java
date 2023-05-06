package com.lidachui.websocket.service;

import com.lidachui.websocket.dal.model.WebSocketMessage;

/**
 * WebSocketMessageService
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/18 23:50
 */
public interface MessageService {

    /**
     * 保存消息
     * @param message 消息
     */
    void save(WebSocketMessage message);
}
