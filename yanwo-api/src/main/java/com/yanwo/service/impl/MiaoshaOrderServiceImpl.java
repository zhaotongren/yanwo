package com.yanwo.service.impl;


import com.yanwo.entity.MiaoshaOrderEntity;
import com.yanwo.entity.SysitemSeckillEntity;
import com.yanwo.entity.UserEntity;
import com.yanwo.redis.MiaoshaKey;
import com.yanwo.redis.OrderKey;
import com.yanwo.redis.RedisService;
import com.yanwo.service.MiaoshaOrderService;
import com.yanwo.service.SysitemSeckillService;
import com.yanwo.service.SystradeTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("miaoshaOrderService")
public class MiaoshaOrderServiceImpl implements MiaoshaOrderService {

    @Autowired
    RedisService redisService;

    @Autowired
    SysitemSeckillService sysitemSeckillService;

    @Autowired
    SystradeTradeService systradeTradeService;

    @Override
    public MiaoshaOrderEntity getMiaoshaOrderByUserIdGoodsId(Integer userId, long seckillId,Integer randomNumber){
        return	redisService.get(OrderKey.getMiaoshaOrderByUidGid,""+userId+"_"+seckillId+"_"+randomNumber,MiaoshaOrderEntity.class) ;
    }

    @Override
    public String getMiaoshaResult(Integer userId, long seckillId,Integer randomNumber){
        MiaoshaOrderEntity order = getMiaoshaOrderByUserIdGoodsId(userId, seckillId,randomNumber);
        if(order != null) {
            if(order.getTid() != null){//秒杀成功
                return order.getTid();
            }else{//创建订单异常 秒杀失败
                return "-1";
            }
        }else {
            boolean isOver = getGoodsOver(seckillId);
            if(isOver) {
                return "-1";
            }else {
                return "0";
            }
        }
    }
    private boolean getGoodsOver(long seckillId) {
        return redisService.exists(MiaoshaKey.isGoodsOver, ""+seckillId);
    }

    @Transactional
    @Override
    public void miaosha(UserEntity user, SysitemSeckillEntity sysitemSeckill,Integer addrId,Integer randomNumber) {
        //减库存 下订单 写入秒杀订单
        boolean success = sysitemSeckillService.reduceStock(sysitemSeckill);
        if(success){
            systradeTradeService.createOrder(user,sysitemSeckill,addrId,randomNumber);
        }else {
            //如果库存不存在则内存标记为true
            setGoodsOver(sysitemSeckill.getId());
        }
    }
    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver, ""+goodsId, true);
    }

}
