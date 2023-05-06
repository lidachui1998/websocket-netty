package com.lidachui.websocket.service.impl;

import com.lidachui.websocket.dal.mapper.BroadcastMessageMapper;
import com.lidachui.websocket.dal.model.BroadcastMessage;
import com.lidachui.websocket.service.BroadcastMessageService;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * BroadcastMessageServiceImpl
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/18 23:52
 */
@Service
public class BroadcastMessageServiceImpl implements BroadcastMessageService {

    @Resource
    private BroadcastMessageMapper broadcastMessageMapper;

    /**
     * 保存广播消息
     */
    @Override
    public int save(BroadcastMessage broadcastMessage) {
        return broadcastMessageMapper.insertSelective(broadcastMessage);
    }

    /**
     * 保存广播消息集合
     */
    @Override
    public void saveList(List<BroadcastMessage> broadcastMessageList) {
        broadcastMessageMapper.insertList(broadcastMessageList);
    }
}
