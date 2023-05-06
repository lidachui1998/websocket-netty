package com.lidachui.websocket.common.constants;

/**
 * MessageType
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/10 23:05
 */
public interface MessageType {
    /**
     * 系统消息
     */
    String SYSTEM = "system";
    /**
     * 系统消息
     */
    String NOTICE = "notice";
    /**
     * 聊天消息
     */
    String CHAT = "chat";
    /**
     * 登录消息
     */
    String LOGIN = "login";
    /**
     * 注销消息
     */
    String LOGOUT = "logout";
    /**
     * 心跳消息
     */
    String HEARTBEAT = "heartbeat";
}
