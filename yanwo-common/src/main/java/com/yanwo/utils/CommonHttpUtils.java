package com.yanwo.utils;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommonHttpUtils {
    public static String httpClienOfGet(String url, Map<String,Object> params){
        String res = "";
        StringBuilder paramsStr = null;
        if(params != null && params.size() > 0){
            for(Map.Entry<String,Object> entry : params.entrySet()){
                paramsStr.append(entry.getKey());
                paramsStr.append("=");
                try {
                    paramsStr.append(URLEncoder.encode(String.valueOf(entry.getValue()),"utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                paramsStr.append("&");
            }
        }

        if(paramsStr != null && paramsStr.length() > 0){
            url = url + "?" + paramsStr.substring(0,paramsStr.length() - 1);
        }
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpResponse response = httpClient.execute(httpGet);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                res = EntityUtils.toString(response.getEntity());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(httpClient != null){
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return res;
    }
    public static String httpClientOfPost(String url, Map<String, Object> params){
        String res = "";
        List<NameValuePair> paramList = new ArrayList<NameValuePair>();
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);

        if(params != null && params.size() > 0){
            for(Map.Entry<String,Object> entry : params.entrySet()){
                paramList.add(new BasicNameValuePair(entry.getKey(),String.valueOf(entry.getValue())));
            }
        }

        try {
            if(paramList.size() > 0){
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(paramList,"utf-8");
                httpPost.setEntity(urlEncodedFormEntity);
            }
            HttpResponse response = httpClient.execute(httpPost);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                res = EntityUtils.toString(response.getEntity());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(httpClient != null){
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return res;
    }

    public static String jsonOfPost(String url, String param){
        String res = "";
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept","application/json");
        httpPost.setHeader("Content-Type","application/json");
        String charSet = "UTF-8";
        HttpResponse response = null;

        try {
            StringEntity entity = new StringEntity(param,charSet);
            entity.setContentEncoding(charSet);
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            response = client.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int state = statusLine.getStatusCode();
            if(state == org.apache.http.HttpStatus.SC_OK){
                HttpEntity responseEntity = response.getEntity();
                res = EntityUtils.toString(responseEntity);
                return res;
            }else{
                System.out.println("请求出错");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static InputStream getminiqrQr(String urls, String param) {
        try
        {

            URL url = new URL(urls);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");// 提交模式
            // conn.setConnectTimeout(10000);//连接超时 单位毫秒
            // conn.setReadTimeout(2000);//读取超时 单位毫秒
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            // 发送请求参数
            printWriter.write(param);
            // flush输出流的缓冲
            printWriter.flush();
            //开始获取数据
            return httpURLConnection.getInputStream();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
