package com.yanwo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SysactivityCouponEntity;
import com.yanwo.utils.PageUtils;

import java.util.Map;

/**
 * 优惠券
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-06-01 14:41:36
 */
public interface SysactivityCouponService extends IService<SysactivityCouponEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

