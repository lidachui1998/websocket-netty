package com.lidachui.websocket.service;

import com.lidachui.websocket.dal.model.BroadcastMessage;

import java.util.*;

/**
 * BroadcastMessageService
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/18 23:51
 */
public interface BroadcastMessageService {

    /**
     * 保存广播消息
     * @param broadcastMessage
     * @return
     */
    int save(BroadcastMessage broadcastMessage);

    /**
     * 保存广播消息集合
     *
     * @param broadcastMessageList
     */
    void saveList(List<BroadcastMessage> broadcastMessageList);
}
