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
import com.yanwo.entity.SysleaveMessageEntity;
import com.yanwo.entity.UserEntity;
import com.yanwo.service.SysindexAdService;
import com.yanwo.service.SysindexArtService;
import com.yanwo.service.SysleaveMessageService;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;


/**
 * 资产接口
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/leavemsg")
@Api(tags="首页接口")
public class ApiLeaveMsgController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(ApiLeaveMsgController.class);

    @Autowired
    SysleaveMessageService sysleaveMessageService;

    @PostMapping("save")
    @ApiOperation("留言")
    public R save(@RequestHeader("token")String token, String leaveMessage,String imgList) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//加入购物车需要先登录
                return R.auth("请登录");
            }
            if(StringUtils.isBlank(leaveMessage)){
                return R.error("留言内容不能为空");
            }
            SysleaveMessageEntity sysleaveMessage = new SysleaveMessageEntity();
            sysleaveMessage.setLeaveMessage(URLEncoder.encode(leaveMessage, "utf-8"));
            sysleaveMessage.setImgList(imgList);
            sysleaveMessage.setUserId(user.getUserId());
            sysleaveMessage.setCreateTime(DateUtils.currentUnixTime());
            sysleaveMessageService.save(sysleaveMessage);
            return R.ok();
        }catch (Exception e){
            logger.info("留言异常",e);
            return R.error();
        }
    }

    @PostMapping("list")
    @ApiOperation("留言列表")
    public R list(@RequestHeader("token")String token) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//加入购物车需要先登录
                return R.auth("请登录");
            }
            List<SysleaveMessageEntity> list = sysleaveMessageService.list();
            for(SysleaveMessageEntity leaveMsg:list){
                leaveMsg.setLeaveMessage(URLDecoder.decode(leaveMsg.getLeaveMessage(), "utf-8"));
                leaveMsg.setDateStr(DateUtils.getDateStrByTimestamp(leaveMsg.getCreateTime(),DateUtils.DATE_TIME_PATTERN));
            }
            return R.okput(list);
        }catch (Exception e){
            logger.info("留言异常",e);
            return R.error();
        }
    }



}
