package com.lidachui.websocket.manager.template;

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
public class RedisPubSubUtil {

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
    public void publish(String channel, Object message) {
        redisTemplate.convertAndSend(channel, message);
    }

    /**
     * 订阅频道
     *
     * @param listener 监听器
     * @param channels 频道列表
     */
    public void subscribe(MessageListener listener, String... channels) {
        if (!container.isRunning()) {
            container.start();
        }
        List<PatternTopic> list = new ArrayList<>();
        for (String channel : channels) {
            PatternTopic patternTopic = new PatternTopic(channel);
            list.add(patternTopic);
        }
        PatternTopic[] topics = list.toArray(new PatternTopic[0]);
        container.addMessageListener(listener, Arrays.asList(topics));
        messageListenerMap.computeIfAbsent(listener, k -> new ArrayList<>()).addAll(Arrays.asList(channels));
    }

    /**
     * 取消订阅频道
     *
     * @param listener 监听器
     */
    public void unsubscribe(MessageListener listener) {
        if (messageListenerMap.containsKey(listener)) {
            container.removeMessageListener(listener);
            messageListenerMap.remove(listener);
        }
        if (messageListenerMap.isEmpty() && container.isRunning()) {
            container.stop();
        }
    }
}


