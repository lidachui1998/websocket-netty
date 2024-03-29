package com.lidachui.websocket.manager.client;

import com.dtflys.forest.annotation.Get;

import java.util.*;

/**
 * AmapClient
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/11/3 16:00
 */
public interface AmapClient {

    /**
     * 获取位置 聪明的你一定看出来了@Get注解代表该方法专做GET请求 在url中的{0}代表引用第一个参数，{1}引用第二个参数
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @return {@code Map}
     */
    @Get("http://ditu.amap.com/service/regeo?longitude={0}&latitude={1}")
    Map getLocation(String longitude, String latitude);
}
