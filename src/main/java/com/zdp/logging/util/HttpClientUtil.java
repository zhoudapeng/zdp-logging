package com.zdp.logging.util;

import com.zdp.logging.consts.ParamEnum;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:zhoudapeng8888@126.com">zhoudapeng</a>
 * Date 2018/4/25
 * Time 下午4:46
 */
public class HttpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    public static String doGet(String url, Map<String, String> param) {


        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();

        String resultString = "";
        CloseableHttpResponse response = null;
        long startTime = 0;
        long endTime = 0;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);
            String traceId = TraceIdUtil.getTraceId();
            httpGet.addHeader(ParamEnum.TRACE_ID,traceId);
            startTime = System.currentTimeMillis();
            // 执行请求
            response = httpclient.execute(httpGet);
            endTime = System.currentTimeMillis();
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("http get request,url=" + url + ",params=" + param  + ",response=" + resultString + ",time cost=" + (endTime - startTime) + "ms");
        return resultString;
    }

    public static String doGet(String url) {
        return doGet(url, null);
    }

    public static String doPost(String url, Map<String, String> param) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        long startTime = 0;
        long endTime = 0;
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            String traceId = TraceIdUtil.getTraceId();
            httpPost.addHeader(ParamEnum.TRACE_ID,traceId);
            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList,"utf-8");
                httpPost.setEntity(entity);
            }
            // 执行http请求
            startTime = System.currentTimeMillis();
            response = httpClient.execute(httpPost);
            endTime = System.currentTimeMillis();
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        logger.info("http get request,url=" + url + ",params=" + param + ",time cost="  + (endTime - startTime) + "ms" );
        return resultString;
    }

    public static String doPost(String url) {
        return doPost(url, null);
    }

    public static String doPostJson(String url, String json) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        long startTime = 0;
        long endTime = 0;
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            String traceId = TraceIdUtil.getTraceId();
            httpPost.addHeader(ParamEnum.TRACE_ID,traceId);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            startTime = System.currentTimeMillis();
            response = httpClient.execute(httpPost);
            endTime = System.currentTimeMillis();
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        logger.info("http postjson request,url=" + url + ",param=" + json + ",time cost="  + (endTime - startTime) + "ms" );
        return resultString;
    }
}
