package com.yanwo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SysactivityCouponEntity;
import com.yanwo.entity.SysuserCouponEntity;
import com.yanwo.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 用户优惠券
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-06-01 18:08:32
 */
public interface SysuserCouponService extends IService<SysuserCouponEntity> {

    PageUtils queryPage(Map<String, Object> params);
    //获取用户优惠券列表
    List<SysactivityCouponEntity> userCouponList(Integer userId,Integer type);
}

