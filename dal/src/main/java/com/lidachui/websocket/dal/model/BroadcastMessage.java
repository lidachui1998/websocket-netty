package com.lidachui.websocket.dal.model;

import javax.persistence.Column;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.*;

/**
 * BroadcastMessage
 *
 * @Author lihuijie
 * @Description: 广播消息实体
 * @SINCE 2023/4/18 22:59
 */
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "broadcast_message")
public class BroadcastMessage<T> extends BaseEntity {

    /**
     * 广播渠道代码
     */
    private String channel;

    /**
     * 广播标题
     */
    private String title;

    /**
     * 广播服务名称
     */
    private String server;

    /**
     * 广播内容
     */
    @Column(name = "content")
    private T content;

    /**
     * 发送状态
     */
    private String sendStatus;

    /**
     * 策略
     */
    private String policy;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createTime;
}
