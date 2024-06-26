package com.lidachui.websocket.service.listener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

/**
 * TestMqListener
 *
 * @author: lihuijie
 * @date: 2024/6/26 13:59
 * @version: 1.0
 */
@Component
public class TestMqListener implements MessageListener {

  @Override
  public void onMessage(Message message) {
    System.out.println("收到消息：" + new String(message.getBody()));
  }
}
