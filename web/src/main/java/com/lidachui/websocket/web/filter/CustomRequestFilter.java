package com.lidachui.websocket.web.filter;

import com.lidachui.simpleRequest.filter.AbstractRequestFilter;
import com.lidachui.simpleRequest.resolver.Request;
import com.lidachui.simpleRequest.resolver.RequestContext;
import com.lidachui.simpleRequest.resolver.Response;
import org.springframework.stereotype.Component;

/**
 * CustomRequestFilter
 *
 * @author: lihuijie
 * @date: 2024/11/24 1:08
 * @version: 1.0
 */
@Component
public class CustomRequestFilter extends AbstractRequestFilter {
  @Override
  public void preHandle(Request request) {
    RequestContext requestContext = getRequestContext();
    System.out.println("CustomRequestFilter preHandle");
  }

  @Override
  public void afterCompletion(Request request, Response response) {
    RequestContext requestContext = getRequestContext();
    System.out.println("CustomRequestFilter afterCompletion");
  }

  @Override
  public void error(Request request, Response response, Exception e) {
    RequestContext requestContext = getRequestContext();
    super.error(request, response, e);
  }
}
