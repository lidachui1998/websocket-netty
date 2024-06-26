package com.lidachui.websocket.service.policy;

import com.lidachui.websocket.common.constants.MessageBroadcastPolicyType;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * MessageBroadCastFactory
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/17 23:03
 */
@Component
public class MessageBroadCastFactory {

    @Resource
    private Map<String, AbstractMessageBroadcastPolicy> policyMap;

    /**
     * 创造策略
     *
     * @param policyType 策略类型
     * @return {@code AbstractMessageBroadcastPolicy }
     */
    public AbstractMessageBroadcastPolicy createPolicy(MessageBroadcastPolicyType policyType) {
        String beanName = policyType.name().toLowerCase() + "MessageBroadcastPolicy";
        AbstractMessageBroadcastPolicy policy = policyMap.get(beanName);
        if (policy == null) {
            throw new IllegalArgumentException("Unknown message broadcast policy type: " + policyType);
        }
        return policy;
    }
}
