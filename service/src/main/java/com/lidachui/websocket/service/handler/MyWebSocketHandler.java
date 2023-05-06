package com.lidachui.websocket.service.handler;

import cn.hutool.core.util.StrUtil;
import com.lidachui.websocket.common.constants.CommonConstants;
import com.lidachui.websocket.common.constants.ConnConstants;
import com.lidachui.websocket.common.util.JsonUtils;
import com.lidachui.websocket.common.util.LogExceptionUtil;
import com.lidachui.websocket.common.util.SpringUtil;
import com.lidachui.websocket.dal.model.WebSocketMessage;
import com.lidachui.websocket.manager.config.Caches;
import com.lidachui.websocket.service.MessageService;
import com.lidachui.websocket.service.config.WebsocketConfig;
import com.lidachui.websocket.service.handler.message.MessageHandler;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


import static com.lidachui.websocket.common.constants.CommonConstants.TRUE;
import static com.lidachui.websocket.common.constants.NumberConstants.*;

/**
 * MyWebSocketHandler
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/10 22:00
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class MyWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final String DATA_KEY = "userId=";

    /**
     * 消息处理器映射表，key 为消息类型，value 为对应的消息处理器对象
     */
    private final Map<String, MessageHandler> MESSAGE_HANDLERS = new HashMap<>();

    @Resource
    public void setMessageHandlers(List<MessageHandler> messageHandlers) {
        // 使用 Spring 的依赖注入机制自动注入所有 MessageHandler 实现类，并将其注册到 MESSAGE_HANDLERS 映射表中
        for (MessageHandler handler : messageHandlers) {
            MESSAGE_HANDLERS.put(handler.getMessageType(), handler);
        }
    }

    /**
     * 客户端与服务器建立连接的时候触发
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        WebsocketConfig.getChannelGroup().add(ctx.channel());
        // 输出日志
        log.info("与客户端建立连接，通道开启: " + ctx.channel().id());
    }

    /**
     * 客户端与服务器关闭连接的时候触发
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        WebsocketConfig.getChannelGroup().remove(ctx.channel());
        Integer connNum = Caches.get(ConnConstants.CONNECTION_NUM_PREFIX, Integer.class);
        if (ONE.equals(connNum)) {
            Caches.delete(ConnConstants.CONNECTION_NUM_PREFIX);
        } else {
            Caches.set(ConnConstants.CONNECTION_NUM_PREFIX, --connNum);
        }
        removeUser(ctx.channel().id());
        // 输出日志
        log.info("与客户端断开连接，通道关闭: " + ctx.channel().id());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            Channel channel = ctx.channel();
            WebSocketServerProtocolHandler.HandshakeComplete handshakeComplete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
            String requestUri = handshakeComplete.requestUri();
            requestUri = URLDecoder.decode(requestUri, "UTF-8");
            log.info("HANDSHAKE_COMPLETE，ID->{}，URI->{}", channel.id().asLongText(), requestUri);
            String userId = requestUri.substring(requestUri.lastIndexOf(DATA_KEY) + DATA_KEY.length());
            if (StrUtil.isNotEmpty(userId)) {
                // 获取连接数
                Integer connNum = Caches.get(ConnConstants.CONNECTION_NUM_PREFIX, Integer.class);
                connNum = Objects.isNull(connNum) ? ONE : ++connNum;
                Caches.set(ConnConstants.CONNECTION_NUM_PREFIX, connNum);
                ConcurrentHashMap<String, Channel> userChannelMap = WebsocketConfig.getUserChannelMap();
                Channel existChannel = userChannelMap.get(userId);
                if (!Objects.isNull(existChannel)) {
                    existChannel.close();
                }
                userChannelMap.put(userId, channel);
            } else {
                channel.disconnect();
                ctx.close();
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 服务器接受客户端的数据信息
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        // 处理客户端发送的消息
        handleMsg(ctx, msg.text());
        // 发送确认消息，告知客户端已经收到了这个消息
        ctx.writeAndFlush(new TextWebSocketFrame("已收到您的消息"));
    }

    /**
     * 处理客户端发送的消息
     */
    private void handleMsg(ChannelHandlerContext ctx, String msg) {
        try {
            if (CommonConstants.HEARTBEAT.equals(msg)) {
                return;
            }
            WebSocketMessage webSocketMessage = JsonUtils.fromJson(msg, WebSocketMessage.class);
            webSocketMessage.setSendTime(new Date());
            if (StrUtil.isNotEmpty(webSocketMessage.getType())) {
                // 根据消息类型选择对应的消息处理器进行处理
                MessageHandler handler = MESSAGE_HANDLERS.get(webSocketMessage.getType());
                Objects.requireNonNull(handler);
                handler.handleMessage(ctx.channel(), webSocketMessage);
            }
        } catch (Exception e) {
            // 发生异常，输出错误日志
            log.error("服务器处理消息出错: " + LogExceptionUtil.getExceptionMessage(e));
        }
    }

    /**
     * 重写 channelReadComplete() 方法，在每次读取完数据后发送确认消息
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    /**
     * 移除用户连接
     *
     * @param id 通道id
     */
    public void removeUser(ChannelId id) {
        ConcurrentHashMap<String, Channel> userChannelMap = WebsocketConfig.getUserChannelMap();
        for (Map.Entry<String, Channel> entry : userChannelMap.entrySet()) {
            if (entry.getValue().id() == id) {
                userChannelMap.remove(entry.getKey());
                break;
            }
        }
    }

    /**
     * 发送消息(提供给接口调用)
     */
    public void sendMessage(WebSocketMessage message) {
        // 如果接收者不为空，则发送给指定的用户
        if (StrUtil.isNotEmpty(message.getReceiver())) {
            MessageHandler messageHandler = MESSAGE_HANDLERS.get(message.getType());
            Objects.requireNonNull(messageHandler);
            ConcurrentHashMap<String, Channel> userChannelMap = WebsocketConfig.getUserChannelMap();
            Channel channel = userChannelMap.get(message.getReceiver());
            messageHandler.handleMessage(channel, message);
        } else { // 否则，默认发送给所有连接的客户端
            WebsocketConfig.getChannelGroup().writeAndFlush(new TextWebSocketFrame(JsonUtils.toJson(message)));
            message.setIsSend(TRUE);
            MessageService messageService = SpringUtil.getBean(MessageService.class);
            Objects.requireNonNull(messageService).save(message);
        }
    }
}
