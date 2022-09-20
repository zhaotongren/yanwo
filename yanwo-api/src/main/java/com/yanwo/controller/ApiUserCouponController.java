package com.yanwo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.entity.SysactivityCouponEntity;
import com.yanwo.entity.SysuserCouponEntity;
import com.yanwo.entity.UserEntity;
import com.yanwo.service.SysactivityCouponService;
import com.yanwo.service.SysuserCouponService;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/activityCoupon")
public class ApiUserCouponController extends BaseController {
    @Autowired
    SysuserCouponService sysuserCouponService;
    @Autowired
    SysactivityCouponService sysactivityCouponService;

    @GetMapping(value = "/couponList")
    public R couponList(){
        QueryWrapper<SysactivityCouponEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("coupon_status",0);
        List<SysactivityCouponEntity> list = sysactivityCouponService.list(queryWrapper);
        List resultList = new ArrayList();
        if (list.isEmpty()){
            return R.error("暂无可领优惠券！");
        }else {
            for (SysactivityCouponEntity couponEntity : list) {
                if (couponEntity.getAlreadyGet()>=couponEntity.getCount()){
                    continue;
                }
                if (couponEntity.getExpireTime()<DateUtils.currentUnixTime()){
                    couponEntity.setCouponStatus(2);
                    sysactivityCouponService.updateById(couponEntity);
                    QueryWrapper<SysuserCouponEntity> sysuserCouponEntityQueryWrapper = new QueryWrapper<>();
                    sysuserCouponEntityQueryWrapper.eq("coupon_id",couponEntity.getCouponId())
                            .eq("get_status",0);
                    List<SysuserCouponEntity> sysuserCouponEntities = sysuserCouponService.list(sysuserCouponEntityQueryWrapper);
                    if (sysuserCouponEntities.isEmpty()){
                        continue;
                    }
                    for (SysuserCouponEntity sysuserCouponEntity : sysuserCouponEntities) {
                        sysuserCouponEntity.setGetStatus(2);
                        sysuserCouponService.updateById(sysuserCouponEntity);
                    }
                    continue;
                }
                Map resultMap = new HashMap();
                resultMap.put("coupon",couponEntity);
                resultMap.put("expiration",DateUtils.getDateStrByTimestamp(couponEntity.getExpireTime(),DateUtils.DATE_TIME_PATTERN));
                resultList.add(resultMap);
            }
        }
        return R.ok().put("couponList",resultList);
    }

    /**
     * 用户的优惠券列表
     * @param token
     * @return
     */
    @GetMapping(value = "/userCoupon")
    public R userCoupon(@RequestHeader String token,@RequestParam String type){
        UserEntity userEntity = getUserFromTokenApp(token);
        if (userEntity==null){
            return R.error("请登录！");
        }else {
            List list = new ArrayList();
            Integer status = null;
            if (!type.isEmpty()){
                status = Integer.valueOf(type);
            }
            List<SysactivityCouponEntity> couponEntityList = sysuserCouponService.userCouponList(userEntity.getUserId(),status);
            Iterator<SysactivityCouponEntity> iterator = couponEntityList.iterator();
            while(iterator.hasNext()){
                Map map = new HashMap();
                SysactivityCouponEntity next = iterator.next();
                map.put("coupon",next);
                map.put("expiration",DateUtils.getDateStrByTimestamp(next.getExpireTime(),DateUtils.DATE_TIME_PATTERN));
                list.add(map);
            }
            return R.ok().put("userCoupons",list);
        }
    }

    /**
     * 领取优惠券
     * @param token
     * @param couponId
     * @return
     */
    @RequestMapping(value = "/getCoupon",method = RequestMethod.POST)
    public R getCoupon(@RequestHeader String token, @RequestParam Integer couponId){
        UserEntity userEntity = getUserFromTokenApp(token);
        if (userEntity==null){
            return R.error("请登录！");
        }
        SysactivityCouponEntity couponServiceById = sysactivityCouponService.getById(couponId);
        Integer alreadyGet = couponServiceById.getAlreadyGet();
        QueryWrapper<SysuserCouponEntity> sysuserCouponEntityQueryWrapper = new QueryWrapper<>();
        sysuserCouponEntityQueryWrapper.eq("user_id",userEntity.getUserId()).eq("coupon_id",couponId);
        List<SysuserCouponEntity> sysuserCouponEntities = sysuserCouponService.list(sysuserCouponEntityQueryWrapper);
        if (!sysuserCouponEntities.isEmpty()){
            if (sysuserCouponEntities.size()>=couponServiceById.getRestrictNumber()){
                return R.error("您已领取了"+couponServiceById.getRestrictNumber()+"张,不能再领取了");
            }
            SysuserCouponEntity sysuserCouponEntity = new SysuserCouponEntity();
            sysuserCouponEntity.setCouponId(couponId);
            sysuserCouponEntity.setUserId(userEntity.getUserId());
            sysuserCouponEntity.setGetStatus(0);
            sysuserCouponEntity.setGetTime(DateUtils.currentUnixTime());
            sysuserCouponService.save(sysuserCouponEntity);
            couponServiceById.setAlreadyGet(alreadyGet+1);
            sysactivityCouponService.updateById(couponServiceById);
        }else {
            SysuserCouponEntity sysuserCouponEntity = new SysuserCouponEntity();
            sysuserCouponEntity.setCouponId(couponId);
            sysuserCouponEntity.setUserId(userEntity.getUserId());
            sysuserCouponEntity.setGetStatus(0);
            sysuserCouponEntity.setGetTime(DateUtils.currentUnixTime());
            sysuserCouponService.save(sysuserCouponEntity);
            couponServiceById.setAlreadyGet(alreadyGet+1);
            sysactivityCouponService.updateById(couponServiceById);
        }
        return R.ok();
    }
}
