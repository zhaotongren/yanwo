package com.yanwo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.dao.SysitemItemDao;
import com.yanwo.entity.SysitemItemEntity;
import com.yanwo.service.SysitemItemService;
import com.yanwo.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("sysitemItemService")
public class SysitemItemServiceImpl extends ServiceImpl<SysitemItemDao, SysitemItemEntity> implements SysitemItemService {

    @Autowired
    SysitemItemDao sysitemItemDao;

    @Override
    public Integer itemStore(Integer itemId) {
        return sysitemItemDao.itemStore(itemId);
    }

    @Override
    public Integer itemRate(Integer itemId) {
        return sysitemItemDao.itemRate(itemId);
    }
}
