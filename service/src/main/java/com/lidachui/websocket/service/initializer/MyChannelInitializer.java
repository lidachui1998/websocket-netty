package com.lidachui.websocket.service.initializer;

import com.lidachui.websocket.service.handler.MyWebSocketHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * MyChannelInitializer
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/11 21:35
 */
@Component
@Slf4j
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Resource
    private MyWebSocketHandler myWebSocketHandler;

    private static final int MAXIMUM_CONTENT_LENGTH = 8192;

    private static final int MAX_FRAME_SIZE = 65536 * 10;

    private static final String WEBSOCKET_PROTOCOL = "websocket";

    private static final String PATH = "/ws";

    @Override
    protected void initChannel(SocketChannel ch) {
        // 添加 WebSocket 相关的组件和处理器
        log.info("收到新连接:" + ch.remoteAddress());
        ch.pipeline().addLast(new HttpServerCodec());
        ch.pipeline().addLast(new ChunkedWriteHandler());
        ch.pipeline().addLast(new HttpObjectAggregator(MAXIMUM_CONTENT_LENGTH));
        ch.pipeline().addLast(new WebSocketServerProtocolHandler(PATH,true, MAX_FRAME_SIZE));
        ch.pipeline().addLast(myWebSocketHandler);
    }
}
