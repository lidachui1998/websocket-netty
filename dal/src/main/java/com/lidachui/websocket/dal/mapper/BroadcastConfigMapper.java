package com.lidachui.websocket.dal.mapper;

import com.lidachui.websocket.dal.model.BroadcastConfig;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.*;

/**
 * BroadcastConfigMapper
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/19 11:29
 */
public interface BroadcastConfigMapper extends Mapper<BroadcastConfig> {

    /**
     * 查询多个通用查询
     *
     * @param broadcastConfig
     * @return broadcastConfigs
     */
    List<BroadcastConfig> listConfig(@Param("entity") BroadcastConfig broadcastConfig);

    /**
     * 查询单个通用查询
     *
     * @param broadcastConfig
     * @return broadcastConfig
     */
    BroadcastConfig getConfig(@Param("entity") BroadcastConfig broadcastConfig);

}
