package com.lidachui.websocket.manager.template;

import javax.annotation.Resource;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RabbitmqUtils
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/19 10:22
 */
@Component
public class RabbitmqUtil {

    @Resource
    private AmqpAdmin amqpAdmin;

    @Resource
    private RabbitTemplate rabbitTemplate;

    private Map<String, SimpleMessageListenerContainer> containers = new ConcurrentHashMap<>();

    /**
     * 发送消息
     *
     * @param exchangeName 交换机名称
     * @param routingKey   路由键
     * @param message      消息内容，可以是任意对象
     */
    public void sendMessage(String exchangeName, String routingKey, Object message) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
    }

    /**
     * 订阅频道，同时启动多个消费者
     *
     * @param queueName           队列名称
     * @param prefetchCount       每个消费者能够处理的最大消息数量，0 表示不限制
     * @param concurrentConsumers 并发消费者数
     * @param listener            消息监听器，实现 MessageListener 接口
     */
    public void subscribe(String queueName, int prefetchCount, int concurrentConsumers, MessageListener listener) {
        if (containers.containsKey(queueName)) {
            // 防止重复订阅
            return;
        }

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setPrefetchCount(prefetchCount);
        factory.setConcurrentConsumers(concurrentConsumers);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());

        SimpleMessageListenerContainer container = factory.createListenerContainer();
        container.setConnectionFactory(rabbitTemplate.getConnectionFactory());
        container.setQueueNames(queueName);
        container.setMessageListener(listener);

        container.start();
        containers.put(queueName, container);
    }

    /**
     * 取消订阅
     *
     * @param queueName 队列名称
     */
    public void unsubscribe(String queueName) {
        SimpleMessageListenerContainer container = containers.remove(queueName);
        if (container != null) {
            container.stop();
        }
    }

}

