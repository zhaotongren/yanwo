package com.yanwo.controller;

import com.yanwo.entity.SystradeTradeEntity;
import com.yanwo.entity.UserEntity;
import com.yanwo.service.LogisticsService;
import com.yanwo.service.SystradeTradeService;
import com.yanwo.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/logistics")
public class LogisticsController extends BaseController{

    @Autowired
    private LogisticsService logisticsService;
    @Autowired
    private SystradeTradeService systradeTradeService;


    @RequestMapping(value = "/select",method = RequestMethod.POST)
    public R select(/*@RequestHeader("token") String token,*/ String tid){
/*        UserEntity user = getUserFromTokenApp(token);
        if (user == null) {//查看订单需要先登录
            return R.auth("请登录");
        }*/
        try {
            return logisticsService.selectById(tid,19);
        }catch (Exception e){
            Map map=new HashMap<>();
            SystradeTradeEntity trade = systradeTradeService.getById(tid);
            map.put("LogisticCode",trade.getCourierNumber());
            map.put("ShipperCode",trade.getCourierCode());
            map.put("Traces","暂无物流信息");
            return R.okput(map);
        }

    }

}
