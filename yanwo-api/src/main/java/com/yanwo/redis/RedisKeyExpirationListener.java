/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yanwo.redis;


import com.yanwo.entity.SysitemItemEntity;
import com.yanwo.entity.SysitemSeckillEntity;
import com.yanwo.entity.SystradeOrderEntity;
import com.yanwo.entity.SystradeTradeEntity;
import com.yanwo.service.SysitemItemService;
import com.yanwo.service.SysitemSeckillService;
import com.yanwo.service.SystradeOrderService;
import com.yanwo.service.SystradeTradeService;
import com.yanwo.utils.GUtils;
import com.yanwo.utils.RedisUtils;
import net.bytebuddy.asm.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {


    @Autowired
    private SystradeTradeService systradeTradeService;

    @Autowired
    private SystradeOrderService systradeOrderService;

    @Autowired
    private SysitemSeckillService sysitemSeckillService;

    @Autowired
    private SysitemItemService sysitemItemService;

    @Autowired
    protected RedisUtils redisUtils;

    @Autowired
    private RedisService redisService;

    private final static Logger logger = LoggerFactory.getLogger(RedisKeyExpirationListener.class);

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 用户做自己的业务处理即可,注意message.toString()可以获取失效的key
        String expiredKey = message.toString();
        if(expiredKey.startsWith("seckill_order")){
            logger.info("redis秒杀订单超过三分钟未支付自动取消通知："+expiredKey);
            Long tid = Long.valueOf(expiredKey.split("_")[2]);
            //秒杀订单超过三分钟未支付  自动关闭
            SystradeTradeEntity systradeTradeEntity = systradeTradeService.getById(tid);
            if (systradeTradeEntity.getStatus().equals("1") && systradeTradeEntity.getType() == 3){
                systradeTradeEntity.setStatus("6");
                systradeTradeEntity.setModifiedTime(GUtils.getCurrentTimestamp().intValue());
                systradeTradeService.updateById(systradeTradeEntity);
                //查询订单信息
                List<SystradeOrderEntity> orderList =  systradeOrderService.getOrdersByTid(systradeTradeEntity.getTid());
                for(SystradeOrderEntity order : orderList){
                    order.setStatus("6");
                    systradeOrderService.updateById(order);
                    //归还库存
                    SysitemSeckillEntity seckillEntity = sysitemSeckillService.getById(order.getSeckillId());
                    logger.info("三分钟关闭订单tid:"+tid+",恢复库存前Stock:"+redisService.get(GoodsKey.getMiaoshaGoodsStock.getPrefix() + order.getSeckillId()));
                    redisUtils.set("miaosha_" + order.getSeckillId(),"0");//设置为有库存
                    redisService.incr(GoodsKey.getMiaoshaGoodsStock, "" +  order.getSeckillId());
                    String store = redisService.get(GoodsKey.getMiaoshaGoodsStock.getPrefix() + order.getSeckillId());
                    logger.info("三分钟关闭订单tid:"+tid+",恢复库存后Stock:"+store);
                    //恢复库存
                    seckillEntity.setSeckillStock(Integer.valueOf(store));
                    sysitemSeckillService.updateById(seckillEntity);
                }
                redisUtils.delete(expiredKey);
            }
        }else if(expiredKey.startsWith("seckill_end")){
            logger.info("redis秒杀商品活动结束自动关闭通知："+expiredKey);
            Long seckillId = Long.valueOf(expiredKey.split("_")[2]);
            //秒杀时间结束  自动关闭
            SysitemSeckillEntity seckillEntity = sysitemSeckillService.getById(seckillId);
            seckillEntity.setStatus(2);
            sysitemSeckillService.updateById(seckillEntity);

            SysitemItemEntity itemEntity = sysitemItemService.getById(seckillEntity.getItemId());
            itemEntity.setIsSeckill(0);
            sysitemItemService.updateById(itemEntity);
            //删除redis数据
            redisUtils.delete("miaosha_" + seckillId);
            redisUtils.delete(expiredKey);
        }else if(expiredKey.startsWith("seckill_start")){
            logger.info("redis秒杀商品活动开始通知："+expiredKey);
            Long seckillId = Long.valueOf(expiredKey.split("_")[2]);
            //秒杀时间结束  自动关闭
            SysitemSeckillEntity seckillEntity = sysitemSeckillService.getById(seckillId);
            seckillEntity.setStatus(1);
            sysitemSeckillService.updateById(seckillEntity);
            //秒杀加入redis
            redisUtils.set("miaosha_" + seckillId,0);
            redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + seckillId, seckillEntity.getSeckillStock());
            //删除redis数据
            redisUtils.delete(expiredKey);
        }
    }
}