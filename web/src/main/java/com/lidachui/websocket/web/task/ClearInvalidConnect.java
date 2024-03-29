package com.lidachui.websocket.web.task;

import com.lidachui.websocket.common.result.Result;
import com.lidachui.websocket.manager.client.DiaryClient;
import com.lidachui.websocket.service.config.WebsocketConfig;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 清除无效连接
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/9/19 15:21
 * @date 2023/09/19
 */
@Component
@Slf4j
public class ClearInvalidConnect {
    @Resource
    private DiaryClient diaryClient;

    /**
     * 定时清除无效连接的任务
     */
    @Scheduled(fixedDelay = 10000) // 每隔10秒执行一次
    public void clearInvalidConnections() {
        ConcurrentHashMap<Channel, String> userChannelMap = WebsocketConfig.getUserChannelMap();
        ChannelGroup channelGroup = WebsocketConfig.getChannelGroup();
        ConcurrentHashMap.KeySetView<Channel, String> channels = userChannelMap.keySet();
        for (Channel channel : channels) {
            if (!channel.isActive()){
                channel.close();

                channelGroup.remove(channel);
                log.info("close invalid connect :" + channel.remoteAddress() + "; userId:" + userChannelMap.get(channel));
            }
        }
    }

    /**
     * 清除未授权的连接
     */
    @Scheduled(fixedDelay = 180000)
    public void clearUnAuthorized() {
        ConcurrentHashMap<Channel, String> userChannelMap = WebsocketConfig.getUserChannelMap();
        for (Map.Entry<Channel, String> entry : userChannelMap.entrySet()) {
            Channel existChannel = entry.getKey();
        }
    }
}
