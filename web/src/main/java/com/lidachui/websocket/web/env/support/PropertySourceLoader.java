package com.lidachui.websocket.web.env.support;

import java.util.*;

/**
 * @author lihuijie
 * @date 2022/11/18 9:48
 * @since 1.0
 */
public interface PropertySourceLoader {

    /**
     * 加载配置
     *
     * @return
     */
    Map<String, String> load();
}
