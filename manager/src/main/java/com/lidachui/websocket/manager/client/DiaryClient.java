package com.lidachui.websocket.manager.client;

import com.lidachui.simpleRequest.annotation.*;
import com.lidachui.simpleRequest.constants.RequestClientType;
import com.lidachui.websocket.common.result.Result;
import org.springframework.http.HttpMethod;

import java.util.*;

/** DiaryClient @Author lihuijie @Description: @SINCE 2023/11/3 16:06 */
@RestClient(
    baseUrl = "https://www.bjca.xyz",
    name = "diaryClient",
    clientType = RequestClientType.OKHTTP)
public interface DiaryClient {

  @RestRequest(path = "/diary/api/login", method = HttpMethod.POST)
  @Retry
  Result login(
      @BodyParam Map<String, Object> params,
      @ResponseHeader(name = "Authorization") Map<String, String> headers);

  @RestRequest(path = "/diary/user/info", method = HttpMethod.GET)
  Result getUserInfo(@HeaderParam(value = "Authorization") String token);
}
