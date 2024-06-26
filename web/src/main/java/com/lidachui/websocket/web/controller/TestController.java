package com.lidachui.websocket.web.controller;

import com.alibaba.fastjson.JSON;
import com.lidachui.websocket.common.result.Result;
import com.lidachui.websocket.common.util.RabbitMQUtils;
import com.lidachui.websocket.service.listener.TestMqListener;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TestController
 *
 * @author: lihuijie
 * @date: 2024/6/26 13:50
 * @version: 1.0
 */
@RestController
@RequestMapping("/api/test")
public class TestController {


  @GetMapping("/sendMqMessage")
  public Result sendMqMessage() {
    RabbitMQUtils.sendMessage("test_exchanges", "", JSON.toJSONString("test message"));
    return Result.success("SUCCESS");
  }

}
