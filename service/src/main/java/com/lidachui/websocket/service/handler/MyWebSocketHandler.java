package com.lidachui.websocket.service.handler;

import static com.lidachui.websocket.common.constants.CommonConstants.TRUE;
import static com.lidachui.websocket.common.constants.NumberConstants.ONE;

import cn.hutool.core.text.CharSequenceUtil;
import com.lidachui.websocket.common.constants.CommonConstants;
import com.lidachui.websocket.common.util.JsonUtils;
import com.lidachui.websocket.common.util.SpringUtil;
import com.lidachui.websocket.common.util.UUIDGenerator;
import com.lidachui.websocket.dal.model.WebSocketMessage;
import com.lidachui.websocket.service.MessageService;
import com.lidachui.websocket.service.config.WebsocketConfig;
import com.lidachui.websocket.service.handler.message.MessageHandler;
import com.lidachui.websocket.service.manager.ConnectionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
    private UserInfoExtractor userInfoExtractor;

    @Resource
    private ConnectionManager connectionManager;

    @Resource
    public void setMessageHandlers(List<MessageHandler> messageHandlers) {
        // 使用 Spring 的依赖注入机制自动注入所有 MessageHandler 实现类，并将其注册到 MESSAGE_HANDLERS 映射表中
        for (MessageHandler handler : messageHandlers) {
            MESSAGE_HANDLERS.put(handler.getMessageType(), handler);
        }
    }

    /**
     * 客户端与服务器建立连接的时候触发
     *
     * @param ctx ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 暂时只添加到channelGroup，用户信息在握手完成后处理
        WebsocketConfig.getChannelGroup().add(ctx.channel());
        log.debug("客户端TCP连接建立，等待WebSocket握手: {}", ctx.channel().id().asShortText());
    }

    /**
     * 客户端与服务器关闭连接的时候触发
     *
     * @param ctx ctx
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        // 移除连接（如果连接从未认证成功，removeConnection会安全处理）
        connectionManager.removeConnection(ctx.channel());
        log.debug("客户端连接断开: {}", ctx.channel().id().asShortText());
    }

    /**
     * 用户事件触发
     *
     * @param ctx ctx
     * @param evt evt
     * @throws Exception 异常
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            Channel channel = ctx.channel();
            WebSocketServerProtocolHandler.HandshakeComplete handshakeComplete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
            String requestUri = handshakeComplete.requestUri();
            HttpHeaders headers = handshakeComplete.requestHeaders();

            log.info("HANDSHAKE_COMPLETE，ID->{}，URI->{}", channel.id().asLongText(), requestUri);

            // 使用用户信息提取器获取用户信息
            Map<String, String> userInfo = userInfoExtractor.extractUserInfo(requestUri, headers);
            String userId = userInfo.get("userId");
            String source = userInfo.get("source");

            if (CharSequenceUtil.isNotEmpty(userId)) {
                log.info("用户连接成功，userId: {}, 来源: {}, channelId: {}", userId, source, channel.id().asShortText());

                // 使用连接管理器添加连接
                connectionManager.addConnection(channel, userId);

                // 可以在这里添加用户上线通知等逻辑
                notifyUserOnline(userId, channel);

            } else {
                log.warn("无法获取用户信息，断开连接，channelId: {}", channel.id().asShortText());
                channel.disconnect();
                ctx.close();
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 用户上线通知
     */
    private void notifyUserOnline(String userId, Channel channel) {
        try {
            // 这里可以添加用户上线的业务逻辑
            // 比如：通知好友上线、更新在线状态等
            log.debug("用户 {} 上线，channel: {}", userId, channel.id().asShortText());
        } catch (Exception e) {
            log.error("处理用户上线通知失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 服务器接受客户端的数据信息
     *
     * @param ctx ctx
     * @param msg 味精
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        // 处理客户端发送的消息
        handleMsg(ctx, msg.text());
    }

    /**
     * 处理客户端发送的消息
     *
     * @param ctx ctx
     * @param msg 味精
     */
    private void handleMsg(ChannelHandlerContext ctx, String msg) {
        try {
            if (CommonConstants.HEARTBEAT.equals(msg)) {
                return;
            }
            WebSocketMessage webSocketMessage = JsonUtils.fromJson(msg, WebSocketMessage.class);
            if (CharSequenceUtil.isNotEmpty(webSocketMessage.getMessageId())){
                webSocketMessage.setSendTime(new Date());
            }else {
                webSocketMessage.setSendTime(new Date()).setMessageId(UUIDGenerator.uuid());
            }
            if (CharSequenceUtil.isNotEmpty(webSocketMessage.getType())) {
                // 根据消息类型选择对应的消息处理器进行处理
                MessageHandler handler = MESSAGE_HANDLERS.get(webSocketMessage.getType());
                Objects.requireNonNull(handler);
                handler.handleMessage(ctx.channel(), webSocketMessage);
            }
        } catch (Exception e) {
            // 发生异常，输出错误日志
            log.error("服务器处理消息出错: " + e.getMessage());
        }
    }

    /**
     * 通道读完整 重写 channelReadComplete() 方法，在每次读取完数据后发送确认消息
     *
     * @param ctx ctx
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        // 发送确认消息，告知客户端已经收到了这个消息
        ctx.writeAndFlush(new TextWebSocketFrame("已收到您的消息"));
    }

    /**
     * 移除用户连接
     *
     * @param channel 通道
     */
    public void removeUser(Channel channel) {
        ConcurrentHashMap<Channel, String> userChannelMap = WebsocketConfig.getUserChannelMap();
        for (Map.Entry<Channel, String> entry : userChannelMap.entrySet()) {
            Channel existChannel = entry.getKey();
            if (Objects.equals(existChannel,channel) && channel.isActive()){
                userChannelMap.remove(channel);
            }
        }
    }

    /**
     * 发送消息(提供给接口调用)
     *
     * @param message 消息
     */
    public void sendMessage(WebSocketMessage message) {
        // 如果接收者不为空，则发送给指定的用户
        if (CharSequenceUtil.isNotEmpty(message.getReceiver())) {
            MessageHandler messageHandler = MESSAGE_HANDLERS.get(message.getType());
            Objects.requireNonNull(messageHandler);
            messageHandler.handleMessage(null, message);
        } else { // 否则，默认发送给所有连接的客户端
            WebsocketConfig.getChannelGroup().writeAndFlush(new TextWebSocketFrame(JsonUtils.toJson(message)));
            message.setIsSend(TRUE);
            MessageService messageService = SpringUtil.getBean(MessageService.class);
            Objects.requireNonNull(messageService).save(message);
        }
    }


}
