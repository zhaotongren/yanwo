package com.yanwo.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SysuserCouponEntity;
import com.yanwo.utils.PageUtils;

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
}

