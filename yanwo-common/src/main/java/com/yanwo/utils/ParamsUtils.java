/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yanwo.utils;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 查询数据
 *
 * @author Mark sunlightcs@gmail.com
 */
public class ParamsUtils {

    public static Map formatMap(String[] keys, Map map){
        for(String key:keys){
            if(map.get(key)==null){
                map.put(key,"");
            }
        }
        return map;
    }

}
