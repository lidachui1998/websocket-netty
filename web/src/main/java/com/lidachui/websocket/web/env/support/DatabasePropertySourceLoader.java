package com.lidachui.websocket.web.env.support;

import java.util.*;

/**
 * @author lihuijie
 * @date 2022/11/18 9:45
 * @since 1.0
 */
public class DatabasePropertySourceLoader implements PropertySourceLoader {

    private PropertySourcesDatabaseHelper dbHelper;

    public DatabasePropertySourceLoader(String jdbcUrl, String user, String password,
                                        String driverClassName) {
        this.dbHelper = new PropertySourcesDatabaseHelper(jdbcUrl, user, password, driverClassName);
    }

    @Override
    public Map<String, String> load() {
        return this.dbHelper.executeSql();
    }
}

