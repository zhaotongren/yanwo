package com.yanwo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by Administrator on 2018/7/22 0022.
 * hkh
 * 随机生成14位的货号
 *
 */
public class BnUtils {

    /**
     * 随机生成一个14位的货号
     * @return
     */
    public static String getBnPath(){
        return getRandomString(14);
    }

    /**
     * 随机生成一个文件夹的名字
     * @return
     */
    public static String getDirPath(){
        return getRandomString(10);
    }

    private static String getRandomString(int length){
        String str="1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number =random.nextInt(36);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
    public static String getCouponCode(String type){
        String str="1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        Date d = new Date();
        String dateNowStr = sdf.format(d);
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        sb.append("MRZG");
        sb.append(type);
        sb.append(dateNowStr);
        for(int i=0;i<5;i++){
            int number =random.nextInt(36);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
    public static String getRandomNum(int length){
        String str="1234567890";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number =random.nextInt(10);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
