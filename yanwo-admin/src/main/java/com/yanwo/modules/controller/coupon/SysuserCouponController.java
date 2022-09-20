package com.yanwo.modules.controller.coupon;

import java.util.Arrays;
import java.util.Map;

import com.yanwo.modules.service.SysuserCouponService;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yanwo.entity.SysuserCouponEntity;



/**
 * 用户优惠券
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-06-01 18:08:32
 */
@RestController
@RequestMapping("yanwo/sysusercoupon")
public class SysuserCouponController {
    @Autowired
    private SysuserCouponService sysuserCouponService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("yanwo:sysusercoupon:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysuserCouponService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{getId}")
    @RequiresPermissions("yanwo:sysusercoupon:info")
    public R info(@PathVariable("getId") Integer getId){
        SysuserCouponEntity sysuserCoupon = sysuserCouponService.getById(getId);

        return R.ok().put("sysuserCoupon", sysuserCoupon);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("yanwo:sysusercoupon:save")
    public R save(@RequestBody SysuserCouponEntity sysuserCoupon){
        sysuserCouponService.save(sysuserCoupon);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("yanwo:sysusercoupon:delete")
    public R delete(@RequestBody Integer[] getIds){
        sysuserCouponService.removeByIds(Arrays.asList(getIds));

        return R.ok();
    }

}
