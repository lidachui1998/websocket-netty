package com.lidachui.websocket.service.handler;

import cn.hutool.core.text.CharSequenceUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lidachui.websocket.common.util.JsonUtils;
import com.lidachui.websocket.common.util.JwtUtil;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户信息提取器 支持从URL参数、Header、Token等多种方式获取用户信息 @Author lihuijie @Description: 用户信息提取器 @SINCE 2023/4/10
 * 22:00
 */
@Slf4j
@Component
public class UserInfoExtractor {

  private static final String USER_ID_PARAM = "userId";
  private static final String USER_INFO_HEADER = "X-User-Info";
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String TOKEN_HEADER = "X-Token";

  /**
   * 从请求中提取用户信息
   *
   * @param requestUri 请求URI
   * @param headers HTTP头信息
   * @return 用户信息Map，包含userId等信息
   */
  public Map<String, String> extractUserInfo(String requestUri, HttpHeaders headers) {
    Map<String, String> userInfo = new HashMap<>();

    try {
      // 1. 优先从Header中获取用户信息
      String userInfoFromHeader = extractFromHeader(headers);
      if (CharSequenceUtil.isNotEmpty(userInfoFromHeader)) {
        userInfo.put("userId", userInfoFromHeader);
        userInfo.put("source", "header");
        return userInfo;
      }

      // 2. 从Token中解析用户信息
      String userInfoFromToken = extractFromToken(headers);
      if (CharSequenceUtil.isNotEmpty(userInfoFromToken)) {
        userInfo.put("userId", userInfoFromToken);
        userInfo.put("source", "token");
        return userInfo;
      }

      // 3. 从URL参数中获取（兼容原有方式）
      String userInfoFromUrl = extractFromUrl(requestUri);
      if (CharSequenceUtil.isNotEmpty(userInfoFromUrl)) {
        userInfo.put("userId", userInfoFromUrl);
        userInfo.put("source", "url");
        return userInfo;
      }

    } catch (Exception e) {
      log.error("提取用户信息失败: {}", e.getMessage(), e);
    }

    return userInfo;
  }

  /** 从Header中提取用户信息 */
  private String extractFromHeader(HttpHeaders headers) {
    try {
      // 方式1: 直接从X-User-Info头获取
      String userInfo = headers.get(USER_INFO_HEADER);
      if (CharSequenceUtil.isNotEmpty(userInfo)) {
        // 如果是Base64编码的JSON，先解码
        if (isBase64(userInfo)) {
          String decoded = new String(Base64.getDecoder().decode(userInfo), StandardCharsets.UTF_8);
          Map<String, Object> userMap = JsonUtils.fromJson(decoded, Map.class);
          return (String) userMap.get("userId");
        }
        return userInfo;
      }

      // 方式2: 从Authorization头获取Bearer token中的用户信息
      String authorization = headers.get(AUTHORIZATION_HEADER);
      if (CharSequenceUtil.isNotEmpty(authorization) && authorization.startsWith("Bearer ")) {
        String token = authorization.substring(7);
        return parseUserIdFromJWT(token);
      }

      // 方式3: 从X-Token头获取
      String token = headers.get(TOKEN_HEADER);
      if (CharSequenceUtil.isNotEmpty(token)) {
        return parseUserIdFromJWT(token);
      }

    } catch (Exception e) {
      log.warn("从Header提取用户信息失败: {}", e.getMessage());
    }
    return null;
  }

  /** 从Token中解析用户ID */
  private String extractFromToken(HttpHeaders headers) {
    try {
      String token = headers.get(TOKEN_HEADER);
      if (CharSequenceUtil.isEmpty(token)) {
        String authorization = headers.get(AUTHORIZATION_HEADER);
        if (CharSequenceUtil.isNotEmpty(authorization)) {
          if (!authorization.startsWith("Bearer ")) {
            token = authorization;
          } else {
            token = authorization.substring(7);
          }
        }
      }

      if (CharSequenceUtil.isNotEmpty(token)) {
        return parseUserIdFromJWT(token);
      }
    } catch (Exception e) {
      log.warn("从Token提取用户信息失败: {}", e.getMessage());
    }
    return null;
  }

  /** 从URL中提取用户信息（兼容原有方式） */
  private String extractFromUrl(String requestUri) {
    try {
      requestUri = URLDecoder.decode(requestUri, StandardCharsets.UTF_8.name());

      // 方式1: userId=xxx 参数形式
      String userIdKey = USER_ID_PARAM + "=";
      if (requestUri.contains(userIdKey)) {
        String userId =
            requestUri.substring(requestUri.lastIndexOf(userIdKey) + userIdKey.length());
        // 处理可能存在的其他参数
        int ampIndex = userId.indexOf('&');
        if (ampIndex != -1) {
          userId = userId.substring(0, ampIndex);
        }
        return userId;
      }

      // 方式2: /ws/userId 路径形式
      String prefix = "/ws/";
      int startIndex = requestUri.indexOf(prefix);
      if (startIndex != -1) {
        startIndex += prefix.length();
        String userId = requestUri.substring(startIndex);
        // 移除可能的查询参数
        int queryIndex = userId.indexOf('?');
        if (queryIndex != -1) {
          userId = userId.substring(0, queryIndex);
        }
        return userId;
      }

    } catch (Exception e) {
      log.warn("从URL提取用户信息失败: {}", e.getMessage());
    }
    return null;
  }

  /** 从JWT Token中解析用户ID 这里简化处理，实际项目中应该使用JWT库进行完整验证 */
  private String parseUserIdFromJWT(String token) {
    try {
      DecodedJWT decode = JwtUtil.decode(token);
      return decode.getClaim("sub").asString();
    } catch (Exception e) {
      log.warn("解析JWT Token失败: {}", e.getMessage());
    }
    return null;
  }

  /** 判断字符串是否为Base64编码 */
  private boolean isBase64(String str) {
    try {
      Base64.getDecoder().decode(str);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
