package com.yanwo.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.dao.SysitemItemDao;
import com.yanwo.dao.SystradeLogisticsDao;
import com.yanwo.dao.SystradeTradeDao;
import com.yanwo.entity.SysitemItemEntity;
import com.yanwo.entity.SystradeLogisticsEntity;
import com.yanwo.entity.SystradeTradeEntity;
import com.yanwo.service.LogisticsService;
import com.yanwo.service.SysitemItemService;
import com.yanwo.utils.*;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("logisticsService")
public class LogisticsServiceImpl extends ServiceImpl<SystradeLogisticsDao, SystradeLogisticsEntity> implements LogisticsService {

    private Logger log = LoggerFactory.getLogger(LogisticsServiceImpl.class);

    @Autowired
    SystradeTradeDao systradeTradeDao;
    @Autowired
    protected RedisUtils redisUtils;

    @Override
    public R selectById(String tid, Integer userId) {
        Map resultmap = new HashMap();
        try {
            QueryWrapper<SystradeTradeEntity> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("tid",tid);
            List<SystradeTradeEntity> systradeTradeEntities = systradeTradeDao.selectList(queryWrapper);

            if (systradeTradeEntities != null && systradeTradeEntities.size() != 0) {
                String no = systradeTradeEntities.get(0).getCourierNumber();//快递单号
                String com = systradeTradeEntities.get(0).getCourierCode();//快递公司简称
                String name = systradeTradeEntities.get(0).getCourierName();//快递公司名称
                log.info("用户:" + userId + ",使用查询物流接口，no:" + no + ",com:" + com);
                if (no == null || com == null || no == "" || com == "" || no.equals("undefined") || com.equals("undefined")) {
                    //本系统存储物流单号或物流公司简称为空  返回300
                    resultmap.put("LogisticCode",no);
                    resultmap.put("ShipperCode",com);
                    resultmap.put("LogisticName",name);
                    resultmap.put("Traces","暂无物流信息");
                    return R.error(300, resultmap);
                } else {
                    Map logistics;
                    if (redisUtils.existsKey("Wuliu" + no + com)) {
                        //本系统redis有物流信息
                        String value = redisUtils.get("Wuliu" + no + com);
                        JSONObject jasonObject = JSONObject.fromObject(value);
                        if(jasonObject!=null){
                            Map logisticsInfo = JsonUtils.jsonToMap(jasonObject.toString());
                            if("true".equals(logisticsInfo.get("Success")+"")){
                                return R.okput(JsonUtils.jsonToMap(jasonObject.toString()));
                            }
                            else{
                                //物流接口没有返回200 没有物流信息  返回300
                                resultmap.put("LogisticCode",no);
                                resultmap.put("ShipperCode",com);
                                resultmap.put("LogisticName",name);
                                resultmap.put("Traces","暂无物流信息");
                                return R.error(300, resultmap);
                            }
                        }
                        else{
                            //本系统物流信息内容为空  返回300
                            resultmap.put("LogisticCode",no);
                            resultmap.put("ShipperCode",com);
                            resultmap.put("LogisticName",name);
                            resultmap.put("Traces","暂无物流信息");
                            return R.error(300, resultmap);
                        }
                    } else {
                        //调用物流接口
                        KdniaoUtil api = new KdniaoUtil();
                        logistics = api.getOrderTracesByJson(com.toUpperCase(), no);
                        logistics.put("LogisticName",name);
                        JSONObject jsonObject = JSONObject.fromObject(logistics);
                        if(jsonObject!=null){
                            Map logisticsInfo =JsonUtils.jsonToMap(jsonObject.toString());
                            if("true".equals(logisticsInfo.get("Success")+"")){
                                List<Map> deliveryList = JsonUtils.jsonToList(JsonUtils.objectToJson(logisticsInfo.get("Traces")),Map.class);
                                List<Map> Traces = new ArrayList<>();
                                for(int i=0;i<deliveryList.size();i++){
                                    Map dmap = deliveryList.get(i);
                                    String AcceptTime = (String) dmap.get("AcceptTime");
                                    String AcceptTime1 = DateUtils.stringToFormatString(AcceptTime,"MM-dd");
                                    String AcceptTime2 = DateUtils.stringToFormatString(AcceptTime,"HH:mm");
                                    dmap.put("dayTime",AcceptTime1);
                                    dmap.put("hourTime",AcceptTime2);
                                    Traces.add(dmap);
                                }
                                logisticsInfo.put("Traces",Traces);
                                SystradeTradeEntity trade = systradeTradeDao.selectById(Long.valueOf(systradeTradeEntities.get(0).getTid()));
                                logisticsInfo.put("receiverAddress",trade.getReceiverAddress());
                                jsonObject = JSONObject.fromObject(logisticsInfo);
                                redisUtils.set("Wuliu" + no + com, jsonObject.toString(), 60 * 30);
                                return R.okput(JsonUtils.jsonToMap(jsonObject.toString()));
                            }
                            else{
                                //物流接口没有返回200 没有物流信息  返回300
                                resultmap.put("LogisticCode",no);
                                resultmap.put("ShipperCode",com);
                                resultmap.put("LogisticName",name);
                                resultmap.put("Traces","暂无物流信息");
                                return R.error(300, resultmap);
                            }
                        }
                        else{
                            //物流信息内容为空  返回300
                            resultmap.put("LogisticCode",no);
                            resultmap.put("ShipperCode",com);
                            resultmap.put("LogisticName",name);
                            resultmap.put("Traces","暂无物流信息");
                            return R.error(300, resultmap);
                        }
                    }
                }
            } else {
                //本系统数据库没有查询到快递单号 和快递名称  返回300
                resultmap.put("LogisticCode", "暂无");
                resultmap.put("LogisticName", "暂无");
                return R.error(300, "暂无物流信息");
            }
        }
        catch (Exception e){
            log.info("查物流异常",e);
            resultmap.put("LogisticCode", "暂无");
            resultmap.put("LogisticName", "暂无");
            return R.error(300, "暂无数据");
        }
    }
}
