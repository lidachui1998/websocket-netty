package com.lidachui.websocket.web.startup;

import com.lidachui.websocket.manager.template.RedisPubSubUtil;
import com.lidachui.websocket.service.listener.TestChannelListener;
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
    private RedisPubSubUtil redisPubSubUtil;
    @Resource
    private TestChannelListener listener;

    @Override
    public void run(ApplicationArguments args) {
        redisPubSubUtil.subscribe(listener, "test-channel");
    }
}
