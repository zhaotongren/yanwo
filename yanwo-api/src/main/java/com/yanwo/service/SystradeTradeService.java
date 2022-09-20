package com.yanwo.service;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.MiaoshaOrderEntity;
import com.yanwo.entity.SysitemSeckillEntity;
import com.yanwo.entity.SysactivityCouponEntity;
import com.yanwo.entity.SystradeTradeEntity;
import com.yanwo.entity.UserEntity;
import com.yanwo.utils.PageUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-04 16:51:45
 */
public interface SystradeTradeService extends IService<SystradeTradeEntity> {
    List<String> saveTradeAndSubOrder(Integer userId, String addressId, String[] buyerMessages, String type, SysactivityCouponEntity coupon);
    BigDecimal sumBytid(List<String> tids);
    BigDecimal cumulativeByUser(Integer userId);
    PageUtils queryPage(QueryWrapper<SystradeTradeEntity> queryWrapper, int currPage);

    public Map getTradeNum(Integer userId);

    PageUtils queryMyPage(QueryWrapper<SystradeTradeEntity> queryWrapper, int currPage,Integer loginUserId);
    //秒杀创建订单
    public MiaoshaOrderEntity createOrder(UserEntity user, SysitemSeckillEntity sysitemSeckill, Integer addrId,Integer randomNumber);
}

