package com.lidachui.websocket.web.task;


import com.lidachui.websocket.service.BroadcastConfigService;
import javax.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 刷新广播配置
 * RefreshBroadcastConfig
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/5/26 11:28
 * @date 2023/05/26
 */
@Component
public class RefreshBroadcastConfigTask {

    @Resource
    private BroadcastConfigService broadcastConfigService;

    @Scheduled(cron = "0 0/10 * * * ? ")
    public void doExecute() {
        broadcastConfigService.refreshConfig();
    }

}
