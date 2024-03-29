package com.lidachui.websocket.api.request;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.*;

/**
 * WebsocketMessage
 *
 * @Author lihuijie
 * @Description: 消息实体
 * @SINCE 2023/4/10 23:04
 */
@Data
@Accessors(chain = true)
@Table(name = "message")
public class WebSocketMessageRequest<T> {

    /**
     * UUID，消息id
     */
    private String messageId;

    /**
     * 消息类型
     */
    @NotBlank(message = "")
    private String type;

    /**
     * 消息头部信息，使用 JSON 格式存储
     */
    @Column(name = "header")
    private String header;

    /**
     * 消息主体内容，使用 JSON 格式存储
     */
    @Column(name = "content")
    private String content;

    /**
     * 消息发送者
     */
    private String sender;

    /**
     * 消息接收者
     */
    private String receiver;

    /**
     * 消息时间，存储日期时间类型
     */
    private Date sendTime;

    /**
     * 接收者列表
     */
    private List<String> receiverList;
}

