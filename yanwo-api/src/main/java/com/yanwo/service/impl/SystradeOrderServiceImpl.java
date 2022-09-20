package com.yanwo.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.dao.SystradeOrderDao;
import com.yanwo.entity.SystradeOrderEntity;
import com.yanwo.service.SystradeOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("systradeOrderService")
public class SystradeOrderServiceImpl extends ServiceImpl<SystradeOrderDao, SystradeOrderEntity> implements SystradeOrderService {
    @Autowired
    SystradeOrderDao systradeOrderDao;

    @Override
    public List<SystradeOrderEntity> getOrdersByTid(String tid){
        return systradeOrderDao.getOrderByID(tid);
    }
}
