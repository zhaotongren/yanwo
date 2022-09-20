package com.yanwo.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.Query;
import com.yanwo.dao.SystradeOrderDao;
import com.yanwo.entity.SystradeOrderEntity;
import com.yanwo.modules.service.SystradeOrderService;
import com.yanwo.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;



@Service("systradeOrderService")
public class SystradeOrderServiceImpl extends ServiceImpl<SystradeOrderDao, SystradeOrderEntity> implements SystradeOrderService {
    @Autowired
    private SystradeOrderDao systradeOrderDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SystradeOrderEntity> page = this.page(
                new Query<SystradeOrderEntity>().getPage(params),
                new QueryWrapper<SystradeOrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SystradeOrderEntity> getOrderByTid(String tid) {
        return systradeOrderDao.getOrderByID(tid);
    }

}
