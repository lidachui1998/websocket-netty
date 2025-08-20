package com.lidachui.websocket.service.manager;

import com.google.common.collect.ImmutableMap;
import com.lidachui.websocket.common.constants.ConnConstants;
import com.lidachui.websocket.manager.config.Caches;
import com.lidachui.websocket.service.config.WebsocketConfig;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 连接管理器
 * 优化连接的管理和监控
 *
 * @Author lihuijie
 * @Description: 连接管理器
 * @SINCE 2023/4/10 22:00
 */
@Slf4j
@Component
public class ConnectionManager {

    /**
     * 添加连接
     */
    public void addConnection(Channel channel, String userId) {
        try {
            ChannelGroup channelGroup = WebsocketConfig.getChannelGroup();
            ConcurrentHashMap<Channel, String> userChannelMap = WebsocketConfig.getUserChannelMap();

            // 检查用户是否已有连接，如果有则关闭旧连接（可选）
            List<Channel> existingChannels = getUserChannels(userId);
            if (!existingChannels.isEmpty()) {
                log.info("用户 {} 已有 {} 个连接，新连接将共存", userId, existingChannels.size());
                // 如果需要单点登录，可以关闭旧连接
                // closeUserConnections(userId);
            }

            // 确保channel已经在channelGroup中
            if (!channelGroup.contains(channel)) {
                channelGroup.add(channel);
            }
            userChannelMap.put(channel, userId);

            // 使用实际的连接数而不是计数器
            int actualCount = userChannelMap.size();
            Caches.set(ConnConstants.CONNECTION_NUM_PREFIX, actualCount);

            log.info("用户 {} 连接成功，当前总连接数: {}", userId, actualCount);

        } catch (Exception e) {
            log.error("添加连接失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 移除连接
     */
    public void removeConnection(Channel channel) {
        try {
            ChannelGroup channelGroup = WebsocketConfig.getChannelGroup();
            ConcurrentHashMap<Channel, String> userChannelMap = WebsocketConfig.getUserChannelMap();

            // 只有在userChannelMap中存在才进行移除操作
            String userId = userChannelMap.remove(channel);
            channelGroup.remove(channel);

            // 使用实际的连接数
            int actualCount = userChannelMap.size();
            if (actualCount <= 0) {
                Caches.delete(ConnConstants.CONNECTION_NUM_PREFIX);
            } else {
                Caches.set(ConnConstants.CONNECTION_NUM_PREFIX, actualCount);
            }

            if (userId != null) {
                log.info("用户 {} 断开连接，当前总连接数: {}", userId, actualCount);
            } else {
                log.debug("移除未认证连接，当前总连接数: {}", actualCount);
            }

        } catch (Exception e) {
            log.error("移除连接失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取用户的所有连接
     */
    public List<Channel> getUserChannels(String userId) {
        ConcurrentHashMap<Channel, String> userChannelMap = WebsocketConfig.getUserChannelMap();
        return userChannelMap.entrySet().stream()
                .filter(entry -> userId.equals(entry.getValue()) && entry.getKey().isActive())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 关闭用户的所有连接
     */
    public void closeUserConnections(String userId) {
        List<Channel> channels = getUserChannels(userId);
        for (Channel channel : channels) {
            try {
                if (channel.isActive()) {
                    channel.close();
                    log.info("关闭用户 {} 的连接: {}", userId, channel.id().asShortText());
                }
            } catch (Exception e) {
                log.error("关闭用户连接失败: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 获取在线用户数
     */
    public int getOnlineUserCount() {
        ConcurrentHashMap<Channel, String> userChannelMap = WebsocketConfig.getUserChannelMap();
        return (int) userChannelMap.values().stream().distinct().count();
    }

    /**
     * 获取总连接数
     */
    public int getTotalConnectionCount() {
        // 使用实际的userChannelMap大小，确保数据一致性
        ConcurrentHashMap<Channel, String> userChannelMap = WebsocketConfig.getUserChannelMap();
        // 清理无效连接
        userChannelMap.entrySet().removeIf(entry -> !entry.getKey().isActive());
        return userChannelMap.size();
    }

    /**
     * 获取连接统计信息
     */
    public Map<String, Object> getConnectionStats() {
        ConcurrentHashMap<Channel, String> userChannelMap = WebsocketConfig.getUserChannelMap();

        // 清理无效连接
        userChannelMap.entrySet().removeIf(entry -> !entry.getKey().isActive());

        Map<String, Long> userConnectionCount = userChannelMap.values().stream()
                .collect(Collectors.groupingBy(userId -> userId, Collectors.counting()));

        return ImmutableMap.of(
                "totalConnections", getTotalConnectionCount(),
                "onlineUsers", getOnlineUserCount(),
                "userConnectionDetails", userConnectionCount
        );
    }

    /**
     * 检查用户是否在线
     */
    public boolean isUserOnline(String userId) {
        return !getUserChannels(userId).isEmpty();
    }
}