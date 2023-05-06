package com.lidachui.websocket.service.component;

import com.lidachui.websocket.common.constants.ConnConstants;
import com.lidachui.websocket.common.util.SpringUtil;
import com.lidachui.websocket.manager.config.Caches;
import com.lidachui.websocket.service.initializer.MyChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


import java.util.concurrent.ThreadPoolExecutor;


/**
 * WebSocketServer
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/10 17:48
 */
@Slf4j
@Component
public class WebSocketServer {
    @Resource
    private MyChannelInitializer myChannelInitializer;
    @Resource
    @Qualifier("websocketThreadPool")
    private ThreadPoolExecutor websocketThreadPool;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private ServerBootstrap serverBootstrap;

    private static final int MAXIMUM_QUEUE_WAITING = 1024;

    private static final int BUFFER_SIZE = 592048;

    public void start() throws Exception {
        int port = Integer.parseInt(SpringUtil.getProperty("websocket.port"));

        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
        this.serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(port)
                .option(ChannelOption.SO_BACKLOG, MAXIMUM_QUEUE_WAITING)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(BUFFER_SIZE))
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(myChannelInitializer);

        ChannelFuture channelFuture = serverBootstrap.bind().sync();
        log.info("Netty WebSocket Server started on port(s): {} (http) with context path '/ws'", port);

        channelFuture.channel().closeFuture().sync();
    }

    /**
     * 释放资源 * @throws InterruptedException
     */
    @PreDestroy
    public void destroy() throws InterruptedException {
        Caches.delete(ConnConstants.CONNECTION_NUM_PREFIX);
        if (bossGroup != null) {
            bossGroup.shutdownGracefully().sync();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully().sync();
        }
    }

    @PostConstruct()
    public void init() {
        //需要开启一个新的线程来执行netty server 服务器
        websocketThreadPool.execute(() -> {
            try {
                start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
