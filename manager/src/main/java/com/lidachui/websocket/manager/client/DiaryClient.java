package com.lidachui.websocket.manager.client;

import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Header;
import com.lidachui.websocket.common.result.Result;


/**
 * DiaryClient
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/11/3 16:06
 */
public interface DiaryClient {


    /**
     * 获取用户信息
     *
     * @param token 令牌
     * @return {@code JSONObject}
     */
    @Get("http://127.0.0.1:2081/diary/user/userInfo")
    Result userInfo(@Header("Authorization") String token);

    /**
     * 用户列表
     *
     * @param token 令牌
     * @return {@code Result}
     */
    @Get("http://127.0.0.1:2081/diary/user/userList")
    Result userList(@Header("Authorization") String token);
}
