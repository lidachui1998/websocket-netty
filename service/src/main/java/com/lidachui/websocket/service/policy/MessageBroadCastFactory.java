package com.lidachui.websocket.service.policy;

import com.lidachui.websocket.common.constants.MessageBroadcastPolicyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * MessageBroadCastFactory
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/17 23:03
 */
@Component
public class MessageBroadCastFactory {

    @Autowired
    private Map<String, AbstractMessageBroadcastPolicy> policyMap;

    public AbstractMessageBroadcastPolicy createPolicy(MessageBroadcastPolicyType policyType) {
        String beanName = policyType.name().toLowerCase() + "MessageBroadcastPolicy";
        AbstractMessageBroadcastPolicy policy = policyMap.get(beanName);
        if (policy == null) {
            throw new IllegalArgumentException("Unknown message broadcast policy type: " + policyType);
        }
        return policy;
    }
}
