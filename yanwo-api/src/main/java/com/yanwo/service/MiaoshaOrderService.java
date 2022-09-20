package com.yanwo.service;


import com.yanwo.entity.MiaoshaOrderEntity;
import com.yanwo.entity.SysitemSeckillEntity;
import com.yanwo.entity.UserEntity;

public interface MiaoshaOrderService{

    public MiaoshaOrderEntity getMiaoshaOrderByUserIdGoodsId(Integer userId, long seckillId,Integer randomNumber);
    public String getMiaoshaResult(Integer userId, long seckillId,Integer randomNumber);
    public void miaosha(UserEntity user, SysitemSeckillEntity sysitemSeckill,Integer addrId,Integer randomNumber);
}

