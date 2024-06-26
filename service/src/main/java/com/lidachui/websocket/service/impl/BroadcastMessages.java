package com.lidachui.websocket.service.impl;


import cn.hutool.core.text.CharSequenceUtil;
import com.lidachui.websocket.common.constants.MessageBroadcastPolicyType;
import com.lidachui.websocket.common.util.AsyncTaskUtil;
import com.lidachui.websocket.common.util.SpringUtil;
import com.lidachui.websocket.dal.model.BroadcastMessage;
import com.lidachui.websocket.dal.model.WebSocketMessage;
import com.lidachui.websocket.service.policy.AbstractMessageBroadcastPolicy;
import com.lidachui.websocket.service.policy.MessageBroadCastFactory;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;


/**
 * 广播消息 BroadcastMessages
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/19 15:10
 * @date 2023/06/19
 */
@Component
public class BroadcastMessages implements InitializingBean {

  public static void broadcast(WebSocketMessage message, String channel, String server,
      String title) {
    String property = SpringUtil.getProperty("broadcast.message.policy");
    List<String> policyList = CharSequenceUtil.split(property, ',');

    MessageBroadCastFactory messageBroadCastFactory = SpringUtil.getBean(
        MessageBroadCastFactory.class);
    for (String policy : policyList) {
      AsyncTaskUtil.execute(() -> {
        try {
          MessageBroadcastPolicyType messageBroadcastPolicyType = MessageBroadcastPolicyType.valueOf(
              policy.toUpperCase());
          AbstractMessageBroadcastPolicy broadcastPolicy = Objects.requireNonNull(
              messageBroadCastFactory).createPolicy(
              messageBroadcastPolicyType);
          BroadcastMessage broadcastMessage = BroadcastMessage.builder()
              .channel(channel)
              .title(title)
              .server(server)
              .content(message)
              .createTime(new Date())
              .createUser(message.getSender())
              .build();
          broadcastPolicy.broadcastMessage(broadcastMessage);
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    }
  }

  @Override
  public void afterPropertiesSet() {
    // TODO document why this method is empty
  }
}
