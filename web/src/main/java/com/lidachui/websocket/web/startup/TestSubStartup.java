package com.lidachui.websocket.web.startup;

import com.lidachui.websocket.common.util.RabbitMQUtils;
import com.lidachui.websocket.common.util.RedisPubSubUtil;
import com.lidachui.websocket.service.listener.TestChannelListener;
import com.lidachui.websocket.service.listener.TestMqListener;
import javax.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * TestSub
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/19 16:02
 */
@Component
@Order(1)
public class TestSubStartup implements ApplicationRunner {

    @Resource
    private TestChannelListener listener;
    @Resource
    private TestMqListener mqListener;

    @Override
    public void run(ApplicationArguments args) {
        RedisPubSubUtil.subscribe(listener, "test-channel");
        RabbitMQUtils.addMessageListener("test_queue", mqListener);
    }
}
