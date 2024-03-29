package com.lidachui.websocket.web.filter;


import com.lidachui.websocket.common.exception.BizException;
import com.lidachui.websocket.common.util.JwtUtil;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import tk.mybatis.mapper.util.StringUtil;

import java.io.IOException;

import static com.lidachui.websocket.common.constants.ErrorCode.TOKEN_EXPIRED;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


/**
 * 认证过滤器
 *
 * @author wtw
 */
@Component
public class JWTAuthenticationFilter implements Filter {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String jwtToken = request.getHeader(AUTHORIZATION);
        if (StringUtil.isEmpty(jwtToken)) {
            resolver.resolveException(request, response,
                    null, new BizException(TOKEN_EXPIRED));
            return;
        }
        if (!JwtUtil.verifyWebToken(request, response)) {
            resolver.resolveException(request, response,
                    null, new BizException(TOKEN_EXPIRED));
            return;
        }
        chain.doFilter(request, response);
    }
}
