package com.lidachui.websocket.dal.mapper;


import com.lidachui.websocket.dal.model.BroadcastMessage;
import tk.mybatis.mapper.common.Mapper;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.special.InsertListMapper;

/**
 * BroadcastMessageDOMapper
 *
 * @author lihuijie
 * @since 2023-04-18 23:45:55
 */
@Repository
public interface BroadcastMessageMapper extends Mapper<BroadcastMessage>, InsertListMapper<BroadcastMessage> {
}
