package com.lidachui.websocket.web.env.support;


import com.lidachui.websocket.common.util.LogExceptionUtil;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author lihuijie
 * @date 2022/11/18 9:47
 * @since 1.0
 */
public class Resources implements ApplicationListener<ApplicationEvent> {

    public Resources() {
        // document why this constructor is empty
    }

    private static final DeferredLog LOGGER = new DeferredLog();

    public static void releaseJdbcResource(
            Connection conn, PreparedStatement statement, ResultSet rs) {
        try {
            if (conn != null) {
                conn.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (statement != null) {
                statement.close();
            }
        } catch (Exception e) {
            LOGGER.error("release resource error, message: " + LogExceptionUtil.getExceptionMessage(e), e);
        }
    }

    @Override
    public void onApplicationEvent(@NonNull ApplicationEvent event) {
        LOGGER.replayTo(Resources.class);
    }
}

