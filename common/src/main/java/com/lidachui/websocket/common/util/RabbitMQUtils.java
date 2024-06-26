package com.lidachui.websocket.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ工具类 提供消息的发送和动态添加消息监听器的功能。
 *
 * @author: lihuijie
 * @date: 2024/6/26 13:34
 * @version: 1.1
 */
@Component
public class RabbitMQUtils implements DisposableBean, InitializingBean {

  private static RabbitMQUtils rabbitMQUtils;
  private static final Logger log = LoggerFactory.getLogger(RabbitMQUtils.class);

  private final RabbitTemplate rabbitTemplate;
  private final ConnectionFactory connectionFactory;

  /**
   * 构造函数，自动注入RabbitTemplate和ConnectionFactory。
   *
   * @param rabbitTemplate    RabbitTemplate实例
   * @param connectionFactory ConnectionFactory实例
   */
  @Autowired
  public RabbitMQUtils(RabbitTemplate rabbitTemplate, ConnectionFactory connectionFactory) {
    this.rabbitTemplate = rabbitTemplate;
    this.connectionFactory = connectionFactory;
  }

  /**
   * 发送消息到指定的Exchange和RoutingKey。
   *
   * @param exchange   交换机名称
   * @param routingKey 路由键
   * @param message    消息内容
   */
  public static void sendMessage(String exchange, String routingKey, Object message) {
    try {
      rabbitMQUtils.rabbitTemplate.convertAndSend(exchange, routingKey, message);
      log.info("消息已发送至交换机: {}, 路由键: {}, 消息内容: {}", exchange, routingKey, message);
    } catch (Exception e) {
      log.error("发送消息失败，交换机: {}, 路由键: {}, 消息内容: {}", exchange, routingKey, message,
          e);
    }
  }

  /**
   * 动态添加消息监听器。 允许在运行时为指定队列添加消息监听器。
   *
   * @param queueName       队列名称
   * @param messageListener 消息监听器实例
   */
  public static void addMessageListener(String queueName, MessageListener messageListener) {
    try {
      SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
      container.setConnectionFactory(rabbitMQUtils.connectionFactory);
      container.setQueueNames(queueName);
      container.setMessageListener(messageListener);
      container.start();
      log.info("为队列 {} 添加了消息监听器", queueName);
    } catch (Exception e) {
      log.error("为队列 {} 添加消息监听器失败", queueName, e);
    }
  }

  /**
   * 增加一个新的Exchange。
   *
   * @param exchangeName Exchange名称
   * @param exchangeType Exchange类型
   */
  public static void addExchange(String exchangeName, String exchangeType) {
    try {
      Exchange exchange;
      if ("direct".equalsIgnoreCase(exchangeType)) {
        exchange = new DirectExchange(exchangeName);
      } else if ("fanout".equalsIgnoreCase(exchangeType)) {
        exchange = new FanoutExchange(exchangeName);
      } else if ("topic".equalsIgnoreCase(exchangeType)) {
        exchange = new TopicExchange(exchangeName);
      } else {
        log.error("不支持的Exchange类型: {}", exchangeType);
        return;
      }

      // 声明Exchange
      rabbitMQUtils.rabbitTemplate.execute(channel -> {
        channel.exchangeDeclare(exchangeName, exchangeType, true);
        return null;
      });

      log.info("Exchange {} 添加成功", exchangeName);
    } catch (Exception e) {
      log.error("添加Exchange失败: {}", exchangeName, e);
    }
  }

  /**
   * 增加一个新的Queue。
   *
   * @param queueName Queue名称
   */
  public static void addQueue(String queueName) {
    try {
      // 声明Queue
      rabbitMQUtils.rabbitTemplate.execute(channel -> {
        channel.queueDeclare(queueName, true, false, false, null);
        return null;
      });

      log.info("Queue {} 添加成功", queueName);
    } catch (Exception e) {
      log.error("添加Queue失败: {}", queueName, e);
    }
  }

  /**
   * 将指定的Queue绑定到指定的Exchange，并指定Routing Key。
   *
   * @param queueName    队列名称
   * @param exchangeName Exchange名称
   * @param routingKey   路由键
   */
  public static void bindQueueToExchange(String queueName, String exchangeName, String routingKey) {
    try {
      // 绑定Queue到Exchange
      rabbitMQUtils.rabbitTemplate.execute(channel -> {
        channel.queueBind(queueName, exchangeName, routingKey);
        return null;
      });

      log.info("Queue {} 绑定到 Exchange {} 成功，Routing Key: {}", queueName, exchangeName, routingKey);
    } catch (Exception e) {
      log.error("绑定Queue到Exchange失败: Queue {}, Exchange {}, Routing Key: {}", queueName, exchangeName, routingKey, e);
    }
  }

  /**
   * 优雅地关闭资源。 实现DisposableBean接口，确保在Spring容器关闭时正确地释放资源。
   */
  @Override
  public void destroy() throws Exception {
    log.info("正在关闭RabbitMQ监听器容器...");
    // 实现具体的关闭逻辑
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    rabbitMQUtils = this;
  }
}
