package com.lidachui.websocket.service;

import com.lidachui.websocket.api.request.WebSocketMessageRequest;

/**
 * MessageSendService
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/11 9:26
 */
public interface MessageSendService {
    /**
     * 发送消息
     * @param request 请求
     */
    void sendMessage(WebSocketMessageRequest request);

    /**
     * 去除
     *
     * @param userId 用户id
     */
    void remove(String userId);

}
