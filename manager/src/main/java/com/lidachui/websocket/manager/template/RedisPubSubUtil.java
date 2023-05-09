package com.lidachui.websocket.manager.template;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RedisPubSubTemplate
 *
 * @Author lihuijie
 * @Description: Redis 订阅发布工具类
 * @SINCE 2023/4/17 23:59
 */
@Component
public class RedisPubSubUtil implements InitializingBean {

    private static RedisPubSubUtil redisPubSubUtil;

    private final RedisTemplate<String, Object> redisTemplate;

    private final RedisMessageListenerContainer container;

    private final Map<MessageListener, List<String>> messageListenerMap = new ConcurrentHashMap<>();

    @Autowired
    public RedisPubSubUtil(RedisTemplate<String, Object> redisTemplate, RedisMessageListenerContainer container) {
        this.redisTemplate = redisTemplate;
        this.container = container;
    }

    /**
     * 发布消息
     *
     * @param channel 频道
     * @param message 消息内容
     */
    public static void publish(String channel, Object message) {
        redisPubSubUtil.redisTemplate.convertAndSend(channel, message);
    }

    /**
     * 订阅频道
     *
     * @param listener 监听器
     * @param channels 频道列表
     */
    public static void subscribe(MessageListener listener, String... channels) {
        if (!redisPubSubUtil.container.isRunning()) {
            redisPubSubUtil.container.start();
        }
        List<PatternTopic> list = new ArrayList<>();
        for (String channel : channels) {
            PatternTopic patternTopic = new PatternTopic(channel);
            list.add(patternTopic);
        }
        PatternTopic[] topics = list.toArray(new PatternTopic[0]);
        redisPubSubUtil.container.addMessageListener(listener, new ArrayList<>(Arrays.asList(topics)));
        redisPubSubUtil.messageListenerMap.computeIfAbsent(listener, k -> new ArrayList<>()).addAll(Arrays.asList(channels));
    }

    /**
     * 取消订阅频道
     *
     * @param listener 监听器
     */
    public static void unsubscribe(MessageListener listener) {
        if (redisPubSubUtil.messageListenerMap.containsKey(listener)) {
            redisPubSubUtil.container.removeMessageListener(listener);
            redisPubSubUtil.messageListenerMap.remove(listener);
        }
        if (redisPubSubUtil.messageListenerMap.isEmpty() && redisPubSubUtil.container.isRunning()) {
            redisPubSubUtil.container.stop();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        redisPubSubUtil = this;
    }
}



