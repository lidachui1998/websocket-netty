package com.lidachui.websocket.web.controller;


import com.lidachui.websocket.api.request.WebSocketMessageRequest;
import com.lidachui.websocket.common.result.Result;
import com.lidachui.websocket.service.MessageSendService;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * MessageApi
 *
 * @Author lihuijie
 * @Description: 消息发送相关接口
 * @SINCE 2023/4/11 9:14
 */
@RestController
@RequestMapping("/api/service")
public class MessageSendApi {
    @Resource
    private MessageSendService messageSendService;

    /**
     * 发送消息
     */
    @PostMapping("/pushMessage/message")
    public Result<String> sendMessage(@RequestBody @Valid WebSocketMessageRequest request) {
        messageSendService.sendMessage(request);
        return Result.success("发送成功!");
    }
}
