package com.lidachui.websocket.web.controller;

import com.lidachui.websocket.common.result.Result;
import com.lidachui.websocket.service.manager.ConnectionManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 连接监控接口
 *
 * @Author lihuijie
 * @Description: 连接监控相关接口
 * @SINCE 2023/4/11 9:14
 */
@RestController
@RequestMapping("/api/monitor")
public class ConnectionMonitorApi {

    @Resource
    private ConnectionManager connectionManager;


    /**
     * 获取连接统计信息
     */
    @GetMapping("/connections/stats")
    public Result<Map<String, Object>> getConnectionStats() {
        Map<String, Object> stats = connectionManager.getConnectionStats();
        return Result.success(stats);
    }

    /**
     * 检查用户是否在线
     */
    @GetMapping("/user/online")
    public Result<Boolean> isUserOnline(@RequestParam String userId) {
        boolean online = connectionManager.isUserOnline(userId);
        return Result.success(online);
    }

    /**
     * 强制断开用户连接
     */
    @GetMapping("/user/disconnect")
    public Result<String> disconnectUser(@RequestParam String userId) {
        connectionManager.closeUserConnections(userId);
        return Result.success("用户连接已断开");
    }

    /**
     * 获取在线用户数
     */
    @GetMapping("/users/count")
    public Result<Integer> getOnlineUserCount() {
        int count = connectionManager.getOnlineUserCount();
        return Result.success(count);
    }

    /**
     * 获取总连接数
     */
    @GetMapping("/connections/count")
    public Result<Integer> getTotalConnectionCount() {
        int count = connectionManager.getTotalConnectionCount();
        return Result.success(count);
    }
}