package com.yanwo.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.entity.*;
import com.yanwo.service.*;
import com.yanwo.utils.GUtils;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trade")
@Api(tags = "订单接口")
public class ApiTradeController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(ApiShoppingCartController.class);
    @Autowired
    private SystradeTradeService systradeTradeService;

    @Autowired
    private SystradeOrderService systradeOrderService;

    @Autowired
    private SysitemItemService sysitemItemService;

    @Autowired
    private EctoolsTradePaybillService ectoolsTradePaybillService;

    @Autowired
    private SysDictService sysDictService;


    @PostMapping("/trade")
    @ApiOperation("查找用户的订单列表")
    public R trade(@RequestHeader("token") String token, @RequestParam(defaultValue = "0") String status,@RequestParam(defaultValue = "1") Integer page) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//查看订单需要先登录
                return R.auth("请登录");
            }
            Integer userId = user.getUserId();

            QueryWrapper<SystradeTradeEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).eq("deleted",false);
            queryWrapper.orderByDesc("tid");
            if (!status.equals("0") && status != null) {
                queryWrapper.eq("status", status);
                if("4".equals(status)){//已完成 未评价
                    queryWrapper.eq("buyer_rate","0");
                }
            }
            PageUtils pages = systradeTradeService.queryPage(queryWrapper,page);
            return R.okput(pages);
        } catch (Exception e) {
            log.info("获取订单列表发生异常", e);
            return R.error();
        }
    }

    SystradeTradeEntity rlist(SystradeTradeEntity s) {
        if (s.getCreatedTime() != null) {
            s.setCreatedTimeValue(GUtils.IntegerToDate(s.getCreatedTime()));
        } else {
            s.setCreatedTimeValue("");
        }
        if (s.getPayTime() != null) {
            s.setPayTimeValue(GUtils.IntegerToDate(s.getPayTime()));
        } else {
            s.setPayTimeValue("");
        }
        if (s.getConsignTime() != null) {
            s.setConsignTimeValue(GUtils.IntegerToDate(s.getConsignTime()));
        } else {
            s.setConsignTimeValue("");
        }
        if (s.getEndTime() != null) {
            s.setFinishTimeValue(GUtils.IntegerToDate(s.getEndTime()));
        } else {
            s.setFinishTimeValue("");
        }

        return s;
    }

    ;

    @PostMapping("/confirm")
    @ApiOperation("确认收货")
    public R confirm(@RequestHeader("token") String token, String tid) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//查看订单需要先登录
                return R.auth("请登录");
            }

            SystradeTradeEntity systradeTradeEntity = systradeTradeService.getById(tid);
            systradeTradeEntity.setStatus("4");
            systradeTradeEntity.setEndTime(GUtils.getCurrentTimestamp().intValue());
            systradeTradeEntity.setModifiedTime(GUtils.getCurrentTimestamp().intValue());
            systradeTradeService.updateById(systradeTradeEntity);
            return R.ok();
        } catch (Exception e) {
            log.info("获取确认收货发生异常", e);
            return R.error();
        }
    }

    @PostMapping("/info")
    @ApiOperation("订单详情")
    public R order(@RequestHeader("token") String token, String tid) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//查看订单需要先登录
                return R.auth("请登录");
            }
            SystradeTradeEntity trade = systradeTradeService.getById(tid);
            Map trademap = new HashMap();
            trademap.put("tid",trade.getTid());
            trademap.put("status",trade.getStatus());
            trademap.put("statusName",sysDictService.getValueByCode("status",trade.getStatus()));
            trademap.put("createdTime",GUtils.IntegerToDate(trade.getCreatedTime()));
            trademap.put("payTime",trade.getPayTime() != null ?GUtils.IntegerToDate(trade.getPayTime()):"");
            trademap.put("consignTime",trade.getConsignTime() != null ?GUtils.IntegerToDate(trade.getConsignTime()):"");
            trademap.put("endTime",trade.getEndTime() != null ?GUtils.IntegerToDate(trade.getEndTime()):"");
            trademap.put("buyRate",trade.getBuyerRate());
            trademap.put("type",trade.getType());

            QueryWrapper<SystradeOrderEntity> orderWrapper = new QueryWrapper<>();
            orderWrapper.eq("tid", trade.getTid());
            List<SystradeOrderEntity> orders = systradeOrderService.list(orderWrapper);
            //小订单
            List<Map> orderList = new ArrayList();
            int productNum = 0;
            for(SystradeOrderEntity order : orders){
                Map ordermap = new HashMap();
                ordermap.put("title",order.getTitle());
                ordermap.put("specInfo",order.getSpecInfo());
                ordermap.put("num",order.getNum());
                ordermap.put("price",order.getPrice());
                ordermap.put("oid",order.getOid().toString());
                ordermap.put("imageDefaultId",order.getPicPath());
                ordermap.put("aftersalesStatus", StringUtils.isNotBlank(order.getAftersalesStatus()) ? order.getAftersalesStatus() : "0");
                ordermap.put("aftersalesStatusName",sysDictService.getValueByCode("aftersales_status",order.getAftersalesStatus()));
                orderList.add(ordermap);
                productNum = productNum + order.getNum();
            }
            trademap.put("postFee",trade.getPostFee());
            trademap.put("totalFee",trade.getTotalFee());
            trademap.put("paymentFee",trade.getPayment());
            trademap.put("productNum",productNum);
            trademap.put("receiverAddress",trade.getReceiverAddress());
            trademap.put("receiverName",trade.getReceiverName());
            trademap.put("receiverMobile",trade.getReceiverMobile());
            Map resultmap = new HashMap();
            resultmap.put("trade",trademap);
            trademap.put("orderList",orderList);
            return R.okput(trademap);
        } catch (Exception e) {
            log.info("获取订单详情发生异常", e);
            return R.error();
        }
    }
    @PostMapping("/close")
    @ApiOperation("关闭未支付订单")
    public R cancel(@RequestHeader("token") String token, String tid) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//查看订单需要先登录
                return R.auth("请登录");
            }
            SystradeTradeEntity systradeTradeEntity = systradeTradeService.getById(tid);

            if (!systradeTradeEntity.getStatus().equals("1")){
                return R.auth("未支付的订单可以关闭，其他不可以");
            }
            systradeTradeEntity.setStatus("6");
            systradeTradeEntity.setModifiedTime(GUtils.getCurrentTimestamp().intValue());
            systradeTradeService.updateById(systradeTradeEntity);
            return R.ok();
        } catch (Exception e) {
            log.info("取消未支付订单发生异常", e);
            return R.error();
        }
    }

    @PostMapping("/courier")
    @ApiOperation("物流信息")
    public R courier(@RequestHeader("token") String token, String tid) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//查看订单需要先登录
                return R.auth("请登录");
            }
            SystradeTradeEntity systradeTradeEntity = systradeTradeService.getById(tid);

            return R.okput(rlist(systradeTradeEntity));
        } catch (Exception e) {
            log.info("获取物流信息发生异常", e);
            return R.error();
        }
    }

    @PostMapping("/delete")
    @ApiOperation("删除订单")
    public R delete(@RequestHeader("token") String token, String tid) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//删除订单需要先登录
                return R.auth("请登录");
            }
            SystradeTradeEntity tradeEntity = systradeTradeService.getById(tid);

            if(!user.getUserId().equals(tradeEntity.getUserId())){//不是本人的订单
                return R.error("请选择自己的订单");
            }
            if(tradeEntity==null){
                return R.error("该订单不存在");
            }
            if (!tradeEntity.getStatus().equals("6")){
                return R.error("已关闭的订单可以删除，其他不可以");
            }
            tradeEntity.setTid(tid);
            tradeEntity.setDeleted(true);
            tradeEntity.setModifiedTime(GUtils.getCurrentTimestamp().intValue());
            if( systradeTradeService.updateById(tradeEntity)){
                return R.ok();
            }else {
                return R.error();
            }
        } catch (Exception e) {
            log.info("删除订单异常", e);
            return R.error();
        }
    }
    @PostMapping("/distributionTrade")
    @ApiOperation("获取用户的分销订单列表")
    public R distributionTrade(@RequestHeader("token") String token,@RequestParam(defaultValue = "1") Integer page) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//查看订单需要先登录
                return R.auth("请登录");
            }
            Integer userId = user.getUserId();

            QueryWrapper<SystradeTradeEntity> queryWrapper = new QueryWrapper<>();
            //7:已取消
            queryWrapper.eq("deleted",false).eq("type",0).ne("status","7");
            queryWrapper.like("rate_param","\"userId\":"+userId+"}");
            PageUtils pages = systradeTradeService.queryMyPage(queryWrapper,page,userId);
            return R.okput(pages);
        } catch (Exception e) {
            log.info("获取用户的分销订单列表", e);
            return R.error();
        }
    }


}
