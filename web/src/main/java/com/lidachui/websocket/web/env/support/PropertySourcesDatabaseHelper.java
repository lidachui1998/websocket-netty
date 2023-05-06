package com.lidachui.websocket.web.env.support;

import org.springframework.boot.logging.DeferredLog;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * @author lihuijie
 * @date 2022/11/18 9:45
 * @since 1.0
 */
public class PropertySourcesDatabaseHelper implements ApplicationListener<ApplicationEvent> {

    public PropertySourcesDatabaseHelper() {
    }

    private static final DeferredLog LOGGER = new DeferredLog();

    private static final String QUERY_SQL = "select config_key, if(length(trim(config_value))>0, config_value, config_def_value) as config_value from app_config where is_halt = 'F'";

    private String jdbcUrl;
    private String user;
    private String password;
    private String driverClassName;

    public PropertySourcesDatabaseHelper(String jdbcUrl, String user, String password,
      String driverClassName) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
        this.driverClassName = driverClassName;
    }

    public Map<String, String> executeSql() {
        Map<String, String> configs = new HashMap<>();

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            Class.forName(
              driverClassName == null || driverClassName.isEmpty() ? "com.mysql.jdbc.Driver"
                : driverClassName);
            conn = DriverManager.getConnection(this.jdbcUrl, user, password);

            statement = conn.prepareStatement(QUERY_SQL);

            rs = statement.executeQuery();
            while (rs.next()) {
                String configKey = rs.getString("config_key");
                String configValue = rs.getString("config_value");
                configs.put(configKey, configValue);
            }
        } catch (Exception ex) {
            LOGGER.error("error to load database configs, message: " + ex.getMessage(), ex);
        } finally {
            // release resource
            Resources.releaseJdbcResource(conn, statement, rs);
        }

        return configs;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        LOGGER.replayTo(PropertySourcesDatabaseHelper.class);
    }
}
