package com.lidachui.websocket.service.policy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.lidachui.websocket.common.annotation.ReadBroadcastConfig;
import com.lidachui.websocket.common.constants.CommonConstants;
import com.lidachui.websocket.common.constants.MessageBroadcastPolicyType;
import com.lidachui.websocket.common.constants.NumberConstants;
import com.lidachui.websocket.common.result.Result;
import com.lidachui.websocket.common.util.HttpClientUtil;
import com.lidachui.websocket.common.util.JsonUtils;
import com.lidachui.websocket.common.util.SpringUtil;
import com.lidachui.websocket.dal.model.BroadcastConfig;
import com.lidachui.websocket.dal.model.BroadcastMessage;
import com.lidachui.websocket.service.BroadcastMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;

import static com.lidachui.websocket.common.constants.CommonConstants.NONE_STR;


/**
 * HttpMessageBroadcastPolicy
 *
 * @Author lihuijie
 * @Description: http广播消息策略
 * @SINCE 2023/4/19 11:27
 */
@Service
@Slf4j
public class HttpMessageBroadcastPolicy extends AbstractMessageBroadcastPolicy {
    @ReadBroadcastConfig(policy = MessageBroadcastPolicyType.HTTP)
    private final List<BroadcastConfig> configs;

    public HttpMessageBroadcastPolicy(List<BroadcastConfig> configs) {
        this.configs = configs;
    }

    /**
     * 广播逻辑
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
            sendBroadcastMessage(broadcastMessage, broadcastMessages, config);
        }
//        saveBroadcastMessage(broadcastMessages);
    }

    /**
     * 具体发送过程
     */
    private static void sendBroadcastMessage(BroadcastMessage broadcastMessage, List<BroadcastMessage> broadcastMessages, BroadcastConfig config) {
        log.info("服务: {} 标题:{} 地址:{}", config.getChannel(), config.getServer(), broadcastMessage.getTitle(), config.getAddress());
        broadcastMessage
                .setChannel(NONE_STR)
                .setServer(config.getServer())
                .setPolicy(MessageBroadcastPolicyType.HTTP.name());
        try {
            String response = HttpClientUtil.doPost(new URI(config.getAddress()), HttpClientUtil.DEFAULT_HEADERS, JsonUtils.toJson(broadcastMessage));
            Result result = JsonUtils.fromJson(response, Result.class);
            broadcastMessage.setSendStatus(result.getCode().equals(NumberConstants.ZERO) ? CommonConstants.SUCCESS : CommonConstants.FAIL)
                    .setFailReason(result.getCode().equals(NumberConstants.ZERO) ? null : result.getMsg());
            log.info(response);
        } catch (Exception e) {
            log.error(e.getMessage());
            broadcastMessage.setSendStatus(CommonConstants.FAIL)
                    .setFailReason(e.getMessage());
        }
        broadcastMessages.add(broadcastMessage);
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
