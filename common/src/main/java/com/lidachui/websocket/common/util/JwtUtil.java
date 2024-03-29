package com.lidachui.websocket.common.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;


import java.util.*;

import static io.jsonwebtoken.Claims.EXPIRATION;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * JwtUtil
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2022/7/6 10:48
 */
@Component
@Slf4j
public class JwtUtil {
    private static JwtUtil jwtUtil;
    private static String secret;

    /**
     * 设置密钥。
     *
     * @param secret 密钥
     */
    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        secret = getMD5(secret, secret);
        JwtUtil.secret = secret;
    }

    public static boolean verifyWebToken(HttpServletRequest req, HttpServletResponse resp) {
        // 获取请求头中的 authorization 信息
        String token = req.getHeader(AUTHORIZATION);
        // 如果为空直接返回 false
        if (token == null) {
            return false;
        }
        // 解码 Token 信息, 如果为空直接返回 false
        DecodedJWT jwtToken = JwtUtil.decode(token);
        if (jwtToken == null) {
            return false;
        }
        Map<String, Claim> claims = jwtToken.getClaims();
        Claim claim = claims.get(EXPIRATION);
        Date expiration = claim.asDate();
        if (expiration.before(new Date())){
            return false;
        }
        // 获取 Token 信息中的用户 id 信息
        String uid = jwtToken.getSubject();
        if (!StringUtils.hasLength(uid)) {
            return false;
        }
        try {
            // 继续校验
            JwtUtil.verifyToken(token);
        } catch (SignatureVerificationException e) {
            // 出现签名校验异常直接返回 false
            return false;
        } catch (Exception e) {
            log.info(LogExceptionUtil.getExceptionMessage(e));
            return false;
        }
        // 设置返回头中的 token
        resp.setHeader(AUTHORIZATION, token);
        return true;
    }


    /**
     * 解码令牌。
     *
     * @param token 令牌字符串
     * @return 解码后的令牌对象，若解码失败则返回null
     */
    public static DecodedJWT decode(String token) {
        try {
            // 返回 Token 的解码信息
            return JWT.decode(token);
        } catch (Exception e) {
            log.info(LogExceptionUtil.getExceptionMessage(e));
            return null;
        }
    }

    /**
     * 验证令牌的有效性。
     *
     * @param token 令牌字符串
     * @throws JWTVerificationException 若令牌验证失败，则抛出此异常
     */
    public static void verifyToken(String token) throws JWTVerificationException {
        // 校验 Token
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(JwtUtil.secret)).build();
        verifier.verify(token);
    }


    public static String getMD5(String password, String salt) {
        if (password == null || password == "" || password.isEmpty()) {
            return null;
        }
        String base = password + "/" + salt;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }

}

