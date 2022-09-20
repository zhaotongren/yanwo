package com.yanwo.utils;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Auther:HouRuiPeng
 * @Date:2019/4/19 11:13
 * @Description:
 */
public class kuaidiUtil {


    public Map tailPhone(String resp){
        Map map= JsonUtils.jsonToMap(resp);
        if (map.get("Traces")==null) return map;
        List<Map> list= (List<Map>) map.get("Traces");

        for (int i=0;i<list.size();i++){
            String str = list.get(i).get("AcceptStation").toString();
            String string=  checkTelephone(str);
            String string1 =  checkCellphone(str);

            if (!string.isEmpty()){
                String  s= str.replace(string,"<a href='tel:"+string+"' >"+string+"</a>");
                list.get(i).put("AcceptStation",s);
            }

            if (!string1.isEmpty()){
                String s=  str.replace(string1,"<a href='tel:"+string1+"' >"+string1+"</a>");
                list.get(i).put("AcceptStation",s);

            }

        }
        map.put("Traces",list);
        return map;
    }



    /**
     * 查询符合的手机号码
     * @param str
     */
    static String checkCellphone(String str){
        String result="";
        // 将给定的正则表达式编译到模式中
        Pattern pattern = Pattern.compile("((13[0-9])|(14[5|7])|(17[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}");
        // 创建匹配给定输入与此模式的匹配器。
        Matcher matcher = pattern.matcher(str);
        //查找字符串中是否有符合的子字符串
        while (matcher.find()) {
            result = matcher.group();
            return result;
        }
        return result;
    }

    /**
     * 查询符合的固定电话
     * @param str
     */
    static String checkTelephone(String str){
        String result="";
        // 将给定的正则表达式编译到模式中
        Pattern pattern = Pattern.compile("(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)");
        // 创建匹配给定输入与此模式的匹配器。
        Matcher matcher = pattern.matcher(str);
        //查找字符串中是否有符合的子字符串
        while (matcher.find()) {
            result = matcher.group();
            return result;
        }
        return result;
    }




}
