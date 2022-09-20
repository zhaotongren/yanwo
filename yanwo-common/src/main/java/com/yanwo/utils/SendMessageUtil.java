package com.yanwo.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


@Component
public class SendMessageUtil {

    private static Logger log = LoggerFactory.getLogger(SendMessageUtil.class);


    //1、发送验证码
    public static int SendMessage(String phone,String code) {
        return SendSmsUtils.SendSms(phone, "SMS_173631167","{\"code\":"+code+"}");
    }

    //2、下单通知
    public static int Sendnotify(String phone,String param) {
        return SendSmsUtils.SendSms(phone, "SMS_190782528",param);
    }
    //2、下单通知
    public static int SendnotifyIntegral(String phone) {
        return SendSmsUtils.SendSms(phone, "SMS_190783774","");
    }

    public static int generateSmsCode(){
        return (int)((Math.random()*9+1)*1000);
    }


    public static void main(String[] args) {
        String param = "{\"title\":\"臻品优选燕窝粽子，儿时的味道加新塑造，美味更出“粽”\",\"num\":1,\"payment\":186.00,\"usermobile\":13783693060}";
        Sendnotify("15637492128",param);
    }

}
