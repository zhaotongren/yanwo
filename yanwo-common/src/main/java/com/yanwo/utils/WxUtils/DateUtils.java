package com.yanwo.utils.WxUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

//TODO 融入common工程的DateUtils类中
public class DateUtils {
	
	/** 
       * 获取当前时间 yyyyMMddHHmmss 
     *  
     * @return String 
     */  
    public static String getCurrTime() {  
        Date now = new Date();  
        SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");  
        String s = outFormat.format(now);  
        return s;  
    }
}
