package com.yanwo.modules.service.impl;

import com.yanwo.modules.service.SysuserCouponService;
import com.yanwo.utils.PageUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.Query;

import com.yanwo.dao.SysuserCouponDao;
import com.yanwo.entity.SysuserCouponEntity;


@Service("sysuserCouponService")
public class SysuserCouponServiceImpl extends ServiceImpl<SysuserCouponDao, SysuserCouponEntity> implements SysuserCouponService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SysuserCouponEntity> page = this.page(
                new Query<SysuserCouponEntity>().getPage(params),
                new QueryWrapper<SysuserCouponEntity>()
        );

        return new PageUtils(page);
    }

}
