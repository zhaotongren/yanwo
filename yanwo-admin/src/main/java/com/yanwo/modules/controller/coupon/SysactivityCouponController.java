package com.yanwo.modules.controller.coupon;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.entity.SysuserCouponEntity;
import com.yanwo.modules.service.SysactivityCouponService;
import com.yanwo.modules.service.SysuserCouponService;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;
import com.yanwo.validator.ValidatorUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yanwo.entity.SysactivityCouponEntity;




/**
 * 优惠券
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-06-01 14:41:36
 */
@RestController
@RequestMapping("yanwo/sysactivitycoupon")
public class SysactivityCouponController {
    @Autowired
    private SysactivityCouponService sysactivityCouponService;
    @Autowired
    private SysuserCouponService sysuserCouponService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("yanwo:sysactivitycoupon:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysactivityCouponService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{couponId}")
    @RequiresPermissions("yanwo:sysactivitycoupon:info")
    public R info(@PathVariable("couponId") Integer couponId){
        SysactivityCouponEntity sysactivityCoupon = sysactivityCouponService.getById(couponId);
        return R.ok().put("sysactivityCoupon", sysactivityCoupon);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("yanwo:sysactivitycoupon:save")
    public R save(@RequestBody SysactivityCouponEntity sysactivityCoupon){
        sysactivityCoupon.setCouponType(0);
        sysactivityCoupon.setCreateTime(DateUtils.currentUnixTime());
        sysactivityCouponService.save(sysactivityCoupon);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("yanwo:sysactivitycoupon:update")
    public R update(@RequestBody SysactivityCouponEntity sysactivityCoupon){
        ValidatorUtils.validateEntity(sysactivityCoupon);
        SysactivityCouponEntity byId = sysactivityCouponService.getById(sysactivityCoupon.getCouponId());
        if (byId.getCouponStatus()==2){
            return R.error("已过期优惠券不能修改！");
        }
        sysactivityCouponService.updateById(sysactivityCoupon);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("yanwo:sysactivitycoupon:delete")
    public R delete(@RequestBody Integer[] couponIds){
        for (Integer couponId : couponIds) {
            QueryWrapper<SysuserCouponEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("coupon_id",couponId);
            List<SysuserCouponEntity> list = sysuserCouponService.list(queryWrapper);
            if (!list.isEmpty()){
                return R.error("该优惠券已被用户领取，无法删除！");
            }
        }
        sysactivityCouponService.removeByIds(Arrays.asList(couponIds));

        return R.ok();
    }

}
