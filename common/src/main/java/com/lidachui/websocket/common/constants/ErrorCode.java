package com.lidachui.websocket.common.constants;


import com.lidachui.websocket.common.result.IErrorCode;

/**
 * ErrorCode
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2022/7/3 16:20
 */
public enum ErrorCode implements IErrorCode {
    /***
     * 1. 以下错误码的定义，需要提前与前端沟通
     * 2. 错误码按模块进行错误码规划
     * 3. 所有错误码枚举类均需要实现错误码接口类
     */
    SUCCESS(0, "操作成功"),
    SYSTEM_BUSY(10000, "系统繁忙,请稍后再试!"),
    FORM_VALIDATION_ERROR(10001, "表单验证错误"),
    // 用户登录方面错误码
    LOGIN_ERROR(101001, "你还未登陆,请及时登陆"),
    TOKEN_ERROR(101002, "登录凭证已过期，请重新登录"),
    VERIFICATION_FAILED(101003, "验证码发送失败,请您检查邮箱是否输入正确!"),
    DATA_ALREADY_EXISTS(101004,"已存在该记录")
    ;
    private Integer code;
    private String msg;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.msg = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}