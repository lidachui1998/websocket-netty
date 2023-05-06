package com.lidachui.websocket.dal.mapper;


import com.lidachui.websocket.dal.model.WebSocketMessage;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;


/**
 * MessageDOMapper
 *
 * @author lihuijie
 * @since 2023-04-16 22:54:48
 */
@Repository
public interface MessageMapper extends Mapper<WebSocketMessage> {
}
