package com.lidachui.websocket.service.config;


import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;


import java.util.concurrent.ConcurrentHashMap;

/**
 * WebsocketConfig
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/17 0:00
 */
public class WebsocketConfig {

    /**
     * 定义一个channel组，管理所有的channel * GlobalEventExecutor.INSTANCE 是全局的事件执行器，是一个单例
     */
    private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 存放用户与Chanel的对应信息，用于给指定用户发送消息
     */
    private static final ConcurrentHashMap<String, Channel> USER_CHANNEL_MAP = new ConcurrentHashMap<>();

    private WebsocketConfig() {
    }

    /**
     * 获取channel组 * @return
     */
    public static ChannelGroup getChannelGroup() {
        return CHANNEL_GROUP;
    }

    /**
     * 获取用户channel map * @return
     */
    public static ConcurrentHashMap<String, Channel> getUserChannelMap() {
        return USER_CHANNEL_MAP;
    }
}
