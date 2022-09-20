package com.yanwo.controller;

import java.io.IOException;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.entity.SysrateScoreEntity;
import com.yanwo.entity.SystradeOrderEntity;
import com.yanwo.entity.UserEntity;
import com.yanwo.service.SysrateScoreService;
import com.yanwo.service.SystradeOrderService;
import com.yanwo.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import net.sf.json.JSONArray;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/evaluate")
@Api(tags = "评价接口")
public class ApiTradeRateController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(ApiShoppingCartController.class);
    @Autowired
    private SysrateScoreService sysrateScoreService;
    @Autowired
    private SystradeOrderService systradeOrderService;

    @PostMapping("/toevaluate")
    @ApiOperation("去评价")
    public R confirm(@RequestHeader("token") String token, Long tid) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//查看订单需要先登录
                return R.auth("请登录");
            }
            QueryWrapper<SystradeOrderEntity> orderWrapper = new QueryWrapper<>();
            orderWrapper.eq("tid", tid);
            List<SystradeOrderEntity> orders = systradeOrderService.list(orderWrapper);
            //封装数据
            List<Map> orderList = new ArrayList();
            for(SystradeOrderEntity order : orders){
                Map ordermap = new HashMap();
                ordermap.put("title",order.getTitle());
                ordermap.put("specInfo",order.getSpecInfo());
                ordermap.put("oid",order.getOid().toString());
                ordermap.put("imageDefaultId",order.getPicPath());
                orderList.add(ordermap);
            }
            Map trademap = new HashMap();
            trademap.put("tid",tid);
            trademap.put("orderList",orderList);
            return R.okput(trademap);
        } catch (Exception e) {
            log.info("去评价发生异常", e);
            return R.error();
        }
    }
    @PostMapping("/evaluate")
    @ApiOperation("评价")
    public R evaluate(@RequestHeader("token") String token,
                               Long tid,
                               int attitude_score,//服务态度
                               int logistics_service_score,//物流服务
                               String[] oids,
                               String[] results,//描述相符
                               String[] contents,
                               HttpServletRequest request
    ) {
        try{
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//查看订单需要先登录
                return R.auth("请登录");
            }
            //重复操作判断
            if (redisUtils.existsKey("evaluate" + user.getUserId())) {
                return R.error("操作频繁,请10秒后重新操作");
            } else {
                redisUtils.set("evaluate" + user.getUserId(), "0", 10);
            }
            String array =  request.getParameter("imgs");
            List<String> imgs = JSONArray.fromObject(array);
            if(oids==null||results==null||contents==null){
                return R.error("提交信息错误");
            }
            if(oids.length!=results.length){
                return R.error("评价失败");
            }
            SysrateScoreEntity sysrateScore = new SysrateScoreEntity();
            sysrateScore.setUserId(user.getUserId());
            sysrateScore.setTid(tid);
            sysrateScore.setAttitudeScore(attitude_score);
            sysrateScore.setLogisticsServiceScore(logistics_service_score);
            return sysrateScoreService.tradeEvaluate(sysrateScore, oids, results,contents, imgs);

        }catch (Exception e){
            log.info("评价发生异常", e);
            return R.error();
        }
    }


}
