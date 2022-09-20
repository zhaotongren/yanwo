package com.yanwo.utils.WxUtils;



import java.util.Date;

public class GUtils {


    /**
     * 获得当前时间的时间时间戳
     * @return Long
     */
    public static  Long getCurrentTimestamp(){
        return  new Date().getTime()/1000;
    }
    /**
     * 获得当前时间的时间时间戳
     * @return Integer
     */
    public static  Integer getIntCurrentTimestamp(){
        Long time=new Date().getTime()/1000;
        return time.intValue();
    }
}
