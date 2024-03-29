package com.lidachui.websocket.service.handler;


import com.lidachui.websocket.common.constants.ErrorCode;
import com.lidachui.websocket.common.exception.BizException;
import com.lidachui.websocket.common.result.Result;
import com.lidachui.websocket.common.util.LogExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * GlobalExceptionHandler
 *
 * @Author lihuijie
 * @Description:全局异常统一处理
 * @SINCE 2022/7/3 16:05
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     *
     * @param e 业务异常
     * @return 返回自定义的结果对象
     */
    @ExceptionHandler(value = BizException.class)
    public Result<String> bizException(BizException e) {
        log.info(LogExceptionUtil.getExceptionMessage(e));
        return new Result<>(e.getCode(), e.getDetailMessage());
    }

    /**
     * 处理通用异常
     *
     * @param e 异常
     * @return 返回自定义的结果对象
     */
    @ExceptionHandler(value = Exception.class)
    public Result<String> commonException(Exception e) {
        log.error(LogExceptionUtil.getExceptionMessage(e));
        return Result.error(e.getMessage());
    }

    /**
     * 处理重复键异常
     *
     * @param e 重复键异常
     * @return 返回自定义的结果对象
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<String> handleDuplicateKeyException(DuplicateKeyException e) {
        log.info(LogExceptionUtil.getExceptionMessage(e));
        return Result.fail(ErrorCode.DATA_ALREADY_EXISTS);
    }

    /**
     * 处理非法参数异常
     *
     * @param e 非法参数异常
     * @return 返回自定义的结果对象
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.info(LogExceptionUtil.getExceptionMessage(e));
        return Result.error(e.getMessage());
    }
}

