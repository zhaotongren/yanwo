package com.yanwo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.dao.SysactivityCouponDao;
import com.yanwo.entity.SysactivityCouponEntity;
import com.yanwo.service.SysactivityCouponService;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.Query;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("sysactivityCouponService")
public class SysactivityCouponServiceImpl extends ServiceImpl<SysactivityCouponDao, SysactivityCouponEntity> implements SysactivityCouponService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SysactivityCouponEntity> page = this.page(
                new Query<SysactivityCouponEntity>().getPage(params),
                new QueryWrapper<SysactivityCouponEntity>()
        );

        return new PageUtils(page);
    }

}
