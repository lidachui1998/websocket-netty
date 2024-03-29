package com.lidachui.websocket.common.util;

import java.util.*;

/**
 * ObjectUtil
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/9/13 0:24
 */
public class ObjectUtil {

    /**
     * 收到按键通过价值
     *
     * @param map   地图
     * @param value 价值
     * @return {@code List<K>}
     */
    public static <K, V> List<K> getKeysByValue(Map<K, V> map, V value) {
        List<K> keys = new ArrayList<>();

        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                keys.add(entry.getKey());
            }
        }

        return keys;
    }
}
