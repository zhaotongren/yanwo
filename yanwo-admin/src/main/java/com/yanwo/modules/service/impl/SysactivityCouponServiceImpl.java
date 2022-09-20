package com.yanwo.modules.service.impl;

import com.yanwo.modules.service.SysactivityCouponService;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.PageUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.Query;

import com.yanwo.dao.SysactivityCouponDao;
import com.yanwo.entity.SysactivityCouponEntity;


@Service("sysactivityCouponService")
public class SysactivityCouponServiceImpl extends ServiceImpl<SysactivityCouponDao, SysactivityCouponEntity> implements SysactivityCouponService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SysactivityCouponEntity> page = this.page(
                new Query<SysactivityCouponEntity>().getPage(params),
                new QueryWrapper<SysactivityCouponEntity>()
        );
        List<SysactivityCouponEntity> records = page.getRecords();
        Iterator<SysactivityCouponEntity> iterator = records.iterator();
        while (iterator.hasNext()){
            SysactivityCouponEntity next = iterator.next();
            if (next.getCouponStatus()==0){
                if (next.getExpireTime()< DateUtils.currentUnixTime()){
                    next.setCouponStatus(2);
                    this.updateById(next);
                }
            }
        }
        page.setRecords(records);
        return new PageUtils(page);
    }

}
