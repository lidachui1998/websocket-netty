package com.lidachui.websocket.common.util;

import com.google.common.collect.ImmutableMap;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * HttpClientUtil
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/3/31 10:58
 */
public class HttpClientUtil {

    public static final Map<String, String> DEFAULT_HEADERS = ImmutableMap.of("Content-Type", HttpClientUtil.CONTENT_TYPE_JSON);
    // 字符编码常量
    private static final String CHARSET_UTF_8 = "UTF-8";
    // form-data 格式常量
    public static final String CONTENT_TYPE_FORM_DATA = "application/x-www-form-urlencoded";
    // json 格式常量
    public static final String CONTENT_TYPE_JSON = "application/json";

    /**
     * 发送 GET 请求
     *
     * @param uri 请求的 URI
     * @return 响应结果
     * @throws IOException 请求出错
     */
    public static String doGet(URI uri) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(uri);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                return handleResponse(response);
            }
        }
    }

    /**
     * 发送 GET 请求
     *
     * @param url    请求的 URL
     * @param params 请求参数
     * @return 响应结果
     * @throws Exception 请求出错
     */
    public static String doGet(String url, Map<String, Object> params) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(url);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            uriBuilder.setParameter(entry.getKey(), String.valueOf(entry.getValue()));
        }
        URI uri = uriBuilder.build();
        return doGet(uri);
    }

    /**
     * 发送 POST 请求，使用 form-data 格式
     *
     * @param uri     请求的 URI
     * @param headers 请求头部信息
     * @param params  请求参数
     * @return 响应结果
     * @throws IOException 请求出错
     */
    public static String doPost(URI uri, Map<String, String> headers, Map<String, String> params) throws IOException {
        List<NameValuePair> pairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(uri);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, CHARSET_UTF_8));
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                return handleResponse(response);
            }
        }
    }

    /**
     * 发送 POST 请求，使用 json 格式
     *
     * @param uri     请求的 URI
     * @param headers 请求头部信息
     * @param body    请求体参数
     * @return 响应结果
     * @throws IOException 请求出错
     */
    public static String doPost(URI uri, Map<String, String> headers, String body) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(uri);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
            // 设置请求体
            httpPost.setEntity(new StringEntity(body, CHARSET_UTF_8));
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                return handleResponse(response);
            }
        }
    }

    /**
     * 发送 PUT 请求，使用 json 格式
     *
     * @param uri     请求的 URI
     * @param headers 请求头部信息
     * @param body    请求体参数
     * @return 响应结果
     * @throws IOException 请求出错
     */
    public static String doPut(URI uri, Map<String, String> headers, String body) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut httpPut = new HttpPut(uri);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPut.setHeader(entry.getKey(), entry.getValue());
            }
            // 设置请求体
            httpPut.setEntity(new StringEntity(body, CHARSET_UTF_8));
            try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
                return handleResponse(response);
            }
        }
    }

    /**
     * 发送 DELETE 请求
     *
     * @param uri     请求的 URI
     * @param headers 请求头部信息
     * @return 响应结果
     * @throws IOException 请求出错
     */
    public static String doDelete(URI uri, Map<String, String> headers) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpDelete httpDelete = new HttpDelete(uri);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpDelete.setHeader(entry.getKey(), entry.getValue());
            }
            try (CloseableHttpResponse response = httpClient.execute(httpDelete)) {
                return handleResponse(response);
            }
        }
    }

    /**
     * 处理响应结果
     *
     * @param response 响应结果
     * @return 响应内容
     * @throws IOException 处理出错
     */
    public static String handleResponse(CloseableHttpResponse response) throws IOException {
        HttpEntity httpEntity = response.getEntity();
        int statusCode = response.getStatusLine().getStatusCode();
        if (httpEntity != null) {
            return EntityUtils.toString(httpEntity, CHARSET_UTF_8);
        } else {
            return "";
        }
    }
}
