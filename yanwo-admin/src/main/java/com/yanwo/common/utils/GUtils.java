package com.yanwo.common.utils;



import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GUtils {


    /**
     * 获得当日凌晨0点10位时间戳
     * @return Long
     */
    public static Long  getTodayBeforeDawnTimestamp(){
        Long currentTimestamps=new Date().getTime()/1000;
        Long oneDayTimestamps= Long.valueOf(60*60*24);
        return currentTimestamps-(currentTimestamps+60*60*8)%oneDayTimestamps;
    }

    /**
     * 获得当前时间的时间时间戳
     * @return Long
     */
    public static  Long getCurrentTimestamp(){
        return  new Date().getTime()/1000;
    }


    /**
     * 获得当日凌晨时间Calendar.getTime()/Date类型
     * @return calendar
     */
    public static Calendar getTodayBeforeDawnCalendar(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar;
    }

    /**
     * 将double类型数字保留n位小数
     * @param val 值
     * @param n 保留几位小数
     * @return Double
     */

    public static Double keepDecimals(Double val,int n){
        StringBuffer pattern=new StringBuffer("#.");
        for(int i=0;i<n;i++){
            pattern.append("0");
        }

        DecimalFormat decimalFormat= new DecimalFormat(pattern.toString());
        if(n==0){
         return  Double.parseDouble(decimalFormat.format(val).replace(".",""));
        }else {
            return Double.parseDouble(decimalFormat.format(val));
        }
    }


    /*****************时间戳转换**********************/
    /**
     * Integer时间戳转String时间
     * @param time
     * @return String
     */
    public static String IntegerToDate(Integer time){
        try {
            if(time==null){
                return "";
            }
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date xx=new Date(time*1000L);
            String ss=sdf.format(xx);

            return ss;
        }catch (Exception e){
            return "";
        }

    }

    public static String birthdayToDate(Integer time){
        try {
            if(time==null){
                return "";
            }
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

            Date xx=new Date(time*1000L);
            String ss=sdf.format(xx);

            return ss;
        }catch (Exception e){
            return "";
        }
    }

    /**
     * String类型日期转Integer类型时间戳
     * */

    public static Integer stringDateToIntegerDate(String date) throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate=sf.parse(date);
        Long time=newDate.getTime()/1000;
        return time.intValue();
    }

    public static Integer stringDateToIntegerDate2(String date) throws ParseException {
        SimpleDateFormat sf = null;
        Date newDate = null;
        Long time=null;
        try {
            sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            newDate=sf.parse(date);

        }catch (ParseException e){
            sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            newDate=sf.parse(date);
        }
        time=newDate.getTime()/1000;
        return time.intValue();
    }
    /**
     * String类型日期转String类型时间戳
     * */

    public static String stringDateToStringTimestamp(String date) throws ParseException {
        if (date==null||date.equals(""))
            return "";
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate=sf.parse(date);
        Long time=newDate.getTime()/1000;
        return time.intValue()+"";
    }
    /**
     * String类型时间戳转String类型日期
     * @param data
     * @return String
     */
    public static String stringToDate(String data){
        if (data==null||data.equals(""))
            return "";
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = new Date(Integer.parseInt(data)*1000L);
        String a = sf.format(d);
        return a;
    }
    //判断是不是为空
    public static Object wisempty(Object data){
        if(data==null||data.equals(""))
            return "";
        return data;
    }

    /**
     * 字符串截取
     * */
	public static String formatString(String str,Integer length){

        if(str.length()<length){
            return str;
        }
	    return str.substring(0,length)+"...";
    }

    /**
     * 对map指定键的值进行非空判断并返回string
     * @param map
     * @param key
     * @return
     */
    public String mapValueToString(Map map, String key){
        if(map!=null) {
            if (map.get(key) != null) {
                return map.get(key).toString();
            }
        }
        return "";
    }
    /**
     * 对map指定键的值进行非空判断并返回Integer
     * @param map
     * @param key
     * @return
     */
    public Integer mapValueToInteger(Map map,String key){
        if(map!=null) {
            if (map.get(key) != null) {
                return Integer.parseInt(map.get(key).toString());
            }
        }
        return 0;
    }
    /**
     * 对map指定键的值进行非空判断并返回Double
     * @param map
     * @param key
     * @return
     */
    public Double mapValueToDouble(Map map,String key){
        if(map!=null) {
            if (map.get(key) != null) {
                return Double.parseDouble(map.get(key).toString());
            }
        }
        return 0d;
    }

    public static Boolean checkMap(Map map,String key){
        if(map.get(key)==null){
            return false;
        }
        if(map.get(key).toString().trim().equals("")){
            return false;
        }
        return true;
    }

    public static Boolean checkObject(Object obj){
        if(obj==null){
            return false;
        }
        if(obj.toString().equals("")){
            return false;
        }
        return true;
    }


    /**
     *
     * Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ： 4E00-9FBF：CJK 统一表意符号
     Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ：F900-FAFF：CJK 兼容象形文字
     Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A ：3400-4DBF：CJK 统一表意符号扩展 A
     Character.UnicodeBlock.GENERAL_PUNCTUATION ：2000-206F：常用标点
     Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION ：3000-303F：CJK 符号和标点
     Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS ：FF00-FFEF：半角及全角形式
     * */



    public static boolean isCHinese(char c){
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if(ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                ||ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                ||ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                ||ub == Character.UnicodeBlock.GENERAL_PUNCTUATION  // GENERAL_PUNCTUATION 判断中文的“号
                ||ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION     // CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号
                ||ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS    // HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号
        )
            return true;
        return false;
    }
    public static boolean isCHinese(String str){
        char[] ch =  str.toCharArray();
        for (char c : ch) {
            if(isCHinese(c))
                return true;
        }
        return false;
    }

    /**
     * 微信昵称转码 转
     * @param nickname
     * @return
     */
    public static String wxNickNameEncoder(String nickname){

        nickname = (new BASE64Encoder()).encodeBuffer(nickname.getBytes());


        return nickname;
    }

    /**
     * 微信昵称解码
     * @param nickname
     * @return
     */
    public static String wxNickNameDecode(String nickname){
        try {
            nickname = new String(new BASE64Decoder().decodeBuffer(nickname), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nickname;

    }


    public static Map<String, Object> obj2Map(Object obj) {
        Map<String, Object> map=new HashMap<>();
        Field[] fields=obj.getClass().getDeclaredFields(); // 获取对象对应类中的所有属性域
        for (int i = 0; i < fields.length; i++) {
            String varName = fields[i].getName();
            //varName=varName.toUpperCase();///将key置为大写，默认为对象的属性
            boolean accessFlag=fields[i].isAccessible(); // 获取原来的访问控制权限
            fields[i].setAccessible(true);// 修改访问控制权限
            try {
                Object object =fields[i].get(obj); // 获取在对象中属性fields[i]对应的对象中的变量
                if (object!=null) {
                    map.put(varName, object);
                }else {
                    map.put(varName, null);
                }
                fields[i].setAccessible(accessFlag);// 恢复访问控制权限
            } catch (IllegalArgumentException | IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return map;
    }


}
