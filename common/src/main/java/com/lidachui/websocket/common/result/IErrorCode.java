package com.lidachui.websocket.common.result;

/**
 * IErrorCode
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2022/7/3 16:20
 */
public interface IErrorCode {
    /**
     * 描述:得到错误码
     * @date 2020/11/21
     **/
    Integer getCode();
    /**
     * 描述:得到错误消息
     * @Author Hank
     **/
    String getMsg();
}
