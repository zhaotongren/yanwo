package com.yanwo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.dao.SysactivityCouponDao;
import com.yanwo.dao.SysuserCouponDao;
import com.yanwo.entity.SysactivityCouponEntity;
import com.yanwo.entity.SysuserCouponEntity;
import com.yanwo.service.SysactivityCouponService;
import com.yanwo.service.SysuserCouponService;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("sysuserCouponService")
public class SysuserCouponServiceImpl extends ServiceImpl<SysuserCouponDao, SysuserCouponEntity> implements SysuserCouponService {

    @Autowired
    SysuserCouponDao couponDao;
    @Autowired
    SysactivityCouponService sysactivityCouponService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SysuserCouponEntity> page = this.page(
                new Query<SysuserCouponEntity>().getPage(params),
                new QueryWrapper<SysuserCouponEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SysactivityCouponEntity> userCouponList(Integer userId,Integer type) {
        QueryWrapper<SysuserCouponEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        if (type!=null){
            queryWrapper.eq("get_status",type);
        }
        List<SysuserCouponEntity> list = couponDao.selectList(queryWrapper);
        List<SysactivityCouponEntity> couponEntityList = new ArrayList<>();
        for (SysuserCouponEntity sysuserCouponEntity : list) {
            SysactivityCouponEntity couponEntity = sysactivityCouponService.getById(sysuserCouponEntity.getCouponId());
            if (couponEntity.getCouponStatus()==2){
                couponEntityList.add(couponEntity);
                if (sysuserCouponEntity.getGetStatus()!=2){
                    sysuserCouponEntity.setGetStatus(2);
                    couponDao.updateById(sysuserCouponEntity);
                }

            }else if (couponEntity.getExpireTime()< DateUtils.currentUnixTime()){
                couponEntity.setCouponStatus(2);
                sysactivityCouponService.updateById(couponEntity);
                sysuserCouponEntity.setGetStatus(2);
                couponDao.updateById(sysuserCouponEntity);
                couponEntityList.add(couponEntity);
            }else{
                couponEntity.setCouponStatus(sysuserCouponEntity.getGetStatus());
                couponEntityList.add(couponEntity);
            }
        }
        return couponEntityList;
    }
}
