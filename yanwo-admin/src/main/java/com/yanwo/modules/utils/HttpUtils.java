package com.yanwo.modules.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class HttpUtils {

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param urlNameString   发送请求的URL
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String urlNameString) {
        String result = "";
        BufferedReader in = null;
        try {

            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    public static String sendPost(String curl, String param) {
        String result = "";// 返回的结果
        BufferedReader in = null;// 读取响应输入流
        try {
            //创建连接
            URL url = new URL(curl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoOutput(true); //是否打开outputStream 相对于程序，即我们向远程服务器写入数据，默认为false，不打开
            connection.setDoInput(true);  //输入流，获取到返回的响应内容， 默认为true，所以get请求时可以不设置这个连接信息
            connection.setRequestMethod("POST"); //发送请求的方式
            connection.setUseCaches(false); //不使用缓存
            connection.setInstanceFollowRedirects(true); //重定向，一般浏览器才需要
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=utf-8"); //设置服务器解析数据的方式

            connection.connect();

            //POST请求
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            out.write(param);
            out.flush();
            out.close();

            //读取响应
            // 定义BufferedReader输入流来读取URL的响应,并设置编码方式
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            // 读取返回的内容
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Http请求方法内部问题");
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

}
