package com.lidachui.websocket.common.util;

/**
 * Iputil
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/5/8 16:41
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import lombok.Data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 获取IP地址工具类
 */
public class Iputil {

    /**
     * 1.通过request对象获取IP
     * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
     * 果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = null;
        try {
            ip = request.getHeader("x-forwarded-for");
            if (ip == null || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //使用代理，则获取第一个IP地址
        if (ip == null) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

    /**
     * 2.通过调用接口的方式获取IP
     */
    public static String getIp() {
        try {
            URL realUrl = new URL("http://whois.pconline.com.cn/ipJson.jsp");
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            conn.setReadTimeout(6000);
            conn.setConnectTimeout(6000);
            conn.setInstanceFollowRedirects(false);
            int code = conn.getResponseCode();
            StringBuilder sb = new StringBuilder();
            String ip = "";
            if (code == 200) {
                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                ip = sb.substring(sb.indexOf("ip") + 5, sb.indexOf("pro") - 3);
            }
            return ip;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 3.通过调用接口根据ip获取归属地
     */
    public static IPInfo getAddress(String ip) {
        try {
            URL realUrl = new URL("http://whois.pconline.com.cn/ipJson.jsp?ip=" + ip + "&json=true");
            String response = HttpClientUtil.doGet(realUrl.toURI());
            ObjectMapper objectMapper = new ObjectMapper();
            IPInfo ipInfo = objectMapper.readValue(response, IPInfo.class);
            return ipInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Data
    public static class IPInfo {

        private String ip;
        private String pro;
        private String proCode;
        private String city;
        private String cityCode;
        private String region;
        private String regionCode;
        private String addr;
        private String regionNames;
        private String err;
    }

    public static void main(String[] args) {
        getAddress(getIp());
    }
}