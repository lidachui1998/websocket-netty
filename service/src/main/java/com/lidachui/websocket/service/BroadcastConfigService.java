package com.lidachui.websocket.service;


import com.lidachui.websocket.dal.model.BroadcastConfig;

import java.util.*;

/**
 * BroadcastConfigService
 *
 * @author lihuijie
 * @since 2023-04-19 11:30:47
 */
public interface BroadcastConfigService {

    /**
     * 查询多个通用查询
     *
     * @param broadcastConfig
     * @return
     */
    List<BroadcastConfig> listConfig(BroadcastConfig broadcastConfig);

    /**
     * 查询单个通用查询
     *
     * @param broadcastConfig
     * @return
     */
    BroadcastConfig getConfig(BroadcastConfig broadcastConfig);

    /**
     * 刷新配置
     */
    void refreshConfig();
}
