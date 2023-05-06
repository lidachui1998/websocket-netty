package com.lidachui.websocket.common.exception;

import com.lidachui.websocket.common.result.IErrorCode;

/**
 * BizException
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/19 9:37
 */
public class BizException extends RuntimeException {

    private int code;
    private String detailMessage;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
        this.detailMessage = message;
    }

    public BizException(IErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMsg());
    }

    public int getCode() {
        return code;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

}
