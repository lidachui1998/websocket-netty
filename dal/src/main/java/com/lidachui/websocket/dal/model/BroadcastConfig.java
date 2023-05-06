package com.lidachui.websocket.dal.model;

import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.*;

/**
 * BroadcastConfig
 *
 * @Author lihuijie
 * @Description: 广播配置实体
 * @SINCE 2023/4/19 11:09
 */
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "broadcast_config")
public class BroadcastConfig extends BaseEntity {

    /**
     * 频道
     */
    private String channel;

    /**
     * 服务
     */
    private String server;

    /**
     * 参数
     */
    private String parameter;

    /**
     * 调用地址（不使用redis、mq等中间件使用http调用）
     */
    private String address;

    /**
     * 适配策略
     */
    private String policy;

    /**
     * 是否启用
     */
    private String isEnable;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createTime;
}
