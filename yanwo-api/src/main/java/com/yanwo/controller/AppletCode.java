package com.yanwo.controller;


import com.yanwo.utils.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@RestController
@RequestMapping("appletCode")
public class AppletCode extends BaseController {
    public static String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx53e0ce07db66b556&secret=c1e9b2c79cbc0a7e69f2fe363440d20b";
    @Value("${sever.image.url}")
    private String severImageUrl;
    @RequestMapping("/getwxacodeunlimit")
    public R getwxacodeunlimit(String path){
        String accesToken = (String) JsonUtils.jsonToMap(CommonHttpUtils.httpClienOfGet(TOKEN_URL, null)).get("access_token");
        String page = "";
        String scene = "";
        if (path.indexOf("?")!=-1){
            page = path.substring(0,path.indexOf("?"));
            scene = path.substring(path.indexOf("?")+1);
        }else {
            page = path;
        }
        JSONObject map = new JSONObject();
        map.put("scene",scene);
        map.put("page",page);
        InputStream inputStream = CommonHttpUtils.getminiqrQr("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token="+accesToken,map.toString());
        try {
            String filePath = "images/appletCode/"+ BnUtils.getDirPath();
            String fileName = BnUtils.getDirPath()+".png";
            OssUtil.uploadFile(filePath,fileName,inputStream);
            String image_url = severImageUrl.endsWith("/") ? severImageUrl + filePath + "/" + fileName : severImageUrl + "/" + filePath + "/" + fileName;
            return R.okput(image_url);
        }catch (Exception e) {
            e.printStackTrace();
            return R.error("生成二维码失败");
        }
    }
    @RequestMapping(value = "/getwxacode")
    public R getwxacode(String path){
        System.out.println("path:"+path);
        try {
            //获取access_token
            String access_token = (String) JsonUtils.jsonToMap(CommonHttpUtils.httpClienOfGet(TOKEN_URL, null)).get("access_token");
            //生成小程序码
            path = path.replace("amp;","&");
            InputStream is = getminiqrQr(path, access_token);
            try {
                String filePath = "images/appletCode/"+ BnUtils.getDirPath();
                String fileName = BnUtils.getDirPath()+".png";
                OssUtil.uploadFile(filePath,fileName,is);
                String image_url = severImageUrl.endsWith("/") ? severImageUrl + filePath + "/" + fileName : severImageUrl + "/" + filePath + "/" + fileName;
                return R.okput(image_url);
            }catch (Exception e) {
                e.printStackTrace();
                return R.error("生成二维码失败");
            }

        }catch (Exception e){
            e.printStackTrace();
            return R.error("生成二维码失败");
        }
    }
    /**
     * 生成带参小程序二维码
     * @param path	参数
     * @param accessToken	token
     */
    public static InputStream getminiqrQr(String path, String accessToken) {
        try
        {
            //base64转码
//            sceneStr =  Base64.encodeBase64String(sceneStr.getBytes());
//            URL url = new URL("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token="+accessToken);
            URL url = new URL("https://api.weixin.qq.com/wxa/getwxacode?access_token="+accessToken);
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
            net.sf.json.JSONObject paramJson = new JSONObject();
//            paramJson.put("scene", sceneStr);
//            paramJson.put("page", "pages/detail/detail");
            paramJson.put("path", path);
            paramJson.put("width", 100);
//            paramJson.put("auto_color", true);
            printWriter.write(paramJson.toString());
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
