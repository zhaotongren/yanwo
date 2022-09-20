/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yanwo.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.entity.SysindexAdEntity;
import com.yanwo.entity.SysindexArtEntity;
import com.yanwo.service.*;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.List;


/**
 * 资产接口
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/index")
@Api(tags="首页接口")
public class ApiIndexController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(ApiIndexController.class);
    @Autowired
    private SysindexAdService sysindexAdService;
    @Autowired
    private SysindexArtService sysindexArtService;

    @PostMapping("lunbo")
    @ApiOperation("轮播图")
    public R lunbo(int type) {
        try {
            QueryWrapper<SysindexAdEntity> queryWrapper = new QueryWrapper();
            queryWrapper.eq("enable_flag",0);
            queryWrapper.eq("type",type);
            queryWrapper.orderByAsc("sort");
            List<SysindexAdEntity> list = sysindexAdService.list(queryWrapper);
            return R.okput(list);
        }catch (Exception e){
            logger.info("查询轮播图异常",e);
            return R.error();
        }
    }

    @PostMapping("art")
    @ApiOperation("文章列表")
    public R art(String type) {
        try {
            QueryWrapper<SysindexArtEntity> queryWrapper = new QueryWrapper();
            queryWrapper.eq("type",1);
            queryWrapper.eq("enable_flag",0);
            queryWrapper.orderByDesc("update_time");
            List<SysindexArtEntity> list = sysindexArtService.list(queryWrapper);
            if("1".equals(type)){//查询前五条
                if(list != null && list.size() > 3){
                    list = list.subList(0,3);
                }
            }
            for(SysindexArtEntity art:list){
                art.setDescription(URLDecoder.decode(art.getDescription(), "UTF-8"));
                art.setDateStr(DateUtils.getDateStrByTimestamp(art.getUpdateTime(),DateUtils.DATE_TIME_PATTERN));
            }
            return R.okput(list);
        }catch (Exception e){
            logger.info("查询文章列表异常",e);
            return R.error();
        }
    }
    @PostMapping("artInfo")
    @ApiOperation("文章详情")
    public R artInfo(Integer id) {
        try {
            SysindexArtEntity art = sysindexArtService.getById(id);
            art.setDescription(URLDecoder.decode(art.getDescription(), "UTF-8"));
            art.setDateStr(DateUtils.getDateStrByTimestamp(art.getUpdateTime(),DateUtils.DATE_TIME_PATTERN));
            return R.okput(art);
        }catch (Exception e){
            logger.info("查询文章详情异常",e);
            return R.error();
        }
    }
    @PostMapping("aboutUs")
    @ApiOperation("关于我们")
    public R aboutUs() {
        try {
            QueryWrapper<SysindexArtEntity> queryWrapper = new QueryWrapper();
            queryWrapper.eq("type",2);
            queryWrapper.eq("enable_flag",0);
            queryWrapper.orderByDesc("update_time");
            List<SysindexArtEntity> list = sysindexArtService.list(queryWrapper);
            SysindexArtEntity art = new SysindexArtEntity();
            if(list != null && list.size() > 0){
                art = list.get(0);
                art.setDescription(URLDecoder.decode(art.getDescription(), "UTF-8"));
                art.setDateStr(DateUtils.getDateStrByTimestamp(art.getUpdateTime(),DateUtils.DATE_TIME_PATTERN));
            }
            return R.okput(art);
        }catch (Exception e){
            logger.info("查询关于我们异常",e);
            return R.error();
        }
    }


}
