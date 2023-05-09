package com.lidachui.websocket.service.policy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.lidachui.websocket.common.annotation.ReadBroadcastConfig;
import com.lidachui.websocket.common.constants.CommonConstants;
import com.lidachui.websocket.common.constants.MessageBroadcastPolicyType;
import com.lidachui.websocket.common.util.JsonUtils;
import com.lidachui.websocket.common.util.SpringUtil;
import com.lidachui.websocket.dal.model.BroadcastConfig;
import com.lidachui.websocket.dal.model.BroadcastMessage;
import com.lidachui.websocket.manager.template.RedisPubSubUtil;
import com.lidachui.websocket.service.BroadcastMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.lidachui.websocket.common.constants.CommonConstants.FAIL;
import static com.lidachui.websocket.common.constants.CommonConstants.SUCCESS;

/**
 * RedisMessageBroadcastPolicy
 *
 * @Author lihuijie
 * @Description: redis消息广播策略
 * @SINCE 2023/4/17 22:58
 */
@Service
@Slf4j
public class RedisMessageBroadcastPolicy extends AbstractMessageBroadcastPolicy {

    @ReadBroadcastConfig(policy = MessageBroadcastPolicyType.REDIS)
    private final List<BroadcastConfig> configs;

    public RedisMessageBroadcastPolicy(List<BroadcastConfig> configs) {
        this.configs = configs;
    }

    /**
     * 执行具体的广播消息逻辑，子类必须实现
     *
     * @param broadcastMessage 广播消息
     */
    @Override
    protected void doBroadcast(BroadcastMessage broadcastMessage) {
        if (CollUtil.isEmpty(configs)) {
            return;
        }
        List<BroadcastMessage> broadcastMessages = Lists.newArrayList();
        for (BroadcastConfig config : configs) {
            log.info("渠道: {} 服务: {} 标题:{}", config.getChannel(), config.getServer(), broadcastMessage.getTitle());
            broadcastMessage.setServer(config.getServer());
            try {
                RedisPubSubUtil.publish(config.getChannel(), broadcastMessage);
                broadcastMessage.setSendStatus(SUCCESS);
            } catch (Exception e) {
                broadcastMessage.setSendStatus(FAIL);
            }
            broadcastMessages.add(broadcastMessage);
        }
        saveBroadcastMessage(broadcastMessages);
    }

    /**
     * 广播消息记录入库
     */
    private void saveBroadcastMessage(List<BroadcastMessage> broadcastMessages) {
        if (CollUtil.isNotEmpty(broadcastMessages)) {
            for (BroadcastMessage broadcastMessage : broadcastMessages) {
                broadcastMessage.setCreateUser(StrUtil.isNotEmpty(broadcastMessage.getCreateUser()) ? broadcastMessage.getCreateUser() : CommonConstants.SYSTEM);
                JsonUtils.convertToJsonString(broadcastMessage, BroadcastMessage::getContent, BroadcastMessage::setContent);
            }
            BroadcastMessageService broadcastMessageService = SpringUtil.getBean(BroadcastMessageService.class);
            Objects.requireNonNull(broadcastMessageService).saveList(broadcastMessages);
        }
    }

}
