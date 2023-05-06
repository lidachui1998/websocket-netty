package com.lidachui.websocket.common.result;

import com.alibaba.fastjson.annotation.JSONField;
import com.lidachui.websocket.common.constants.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * 封装返回类
 *
 * @author lihuijie
 * @date 2022-7-03 10:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {


    /**
     * 错误代码
     */
    private Integer code;
    /**
     * 消息
     */
    private String msg;
    /**
     * 对应返回数据
     */
    private T retVal;

    public Result(int code, String meesage) {
        setCode(code);
        setMsg(meesage);
    }

    public Result(IErrorCode errorCode, T retVal) {
        setCodeMessage(errorCode);
        setRetVal(retVal);
    }

    public Result setCodeMessage(IErrorCode codeMessage) {
        setCode(codeMessage.getCode());
        setMsg(codeMessage.getMsg());
        return this;
    }

    public Result(String msg) {
        this.code = -1;
        this.msg = msg;
    }

    @JSONField(serialize = false)
    public boolean isOk() {
        return Objects.equals(this.code, 0);
    }

    /***
     * 成功的返回对象
     * @param data
     * @return
     */
    public static Result success(Object data) {
        return new Result(ErrorCode.SUCCESS, data);
    }

    /**
     * 失败的返回对象
     *
     * @Param: ErrCodeInterface
     * @return: [ResultVO]
     **/
    public static Result fail(IErrorCode errorCode) {
        return new Result().setCodeMessage(errorCode);
    }

    /**
     * 描述: 通过errorCode和数据对象参数，构建一个新的对象
     *
     * @param errorCode, data
     * @return: [ResultVO]
     **/
    public static Result result(IErrorCode errorCode, Object data) {
        return new Result(errorCode, data);
    }

    public static Result error(Object data) {
        return new Result((String) data);
    }
}
