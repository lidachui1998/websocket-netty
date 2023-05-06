package com.lidachui.websocket.service.impl;

import com.lidachui.websocket.dal.mapper.BroadcastConfigMapper;
import com.lidachui.websocket.dal.model.BroadcastConfig;
import com.lidachui.websocket.service.BroadcastConfigService;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * BroadcastConfigServiceImpl
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/19 11:33
 */
@Service
public class BroadcastConfigServiceImpl implements BroadcastConfigService {
    @Resource
    private BroadcastConfigMapper broadcastConfigMapper;


    /**
     * 查询多个通用查询
     */
    @Override
    public List<BroadcastConfig> listConfig(BroadcastConfig broadcastConfig) {
        return broadcastConfigMapper.listConfig(broadcastConfig);
    }

    /**
     * 查询单个通用查询
     */
    @Override
    public BroadcastConfig getConfig(BroadcastConfig broadcastConfig) {
        return broadcastConfigMapper.getConfig(broadcastConfig);
    }
}
