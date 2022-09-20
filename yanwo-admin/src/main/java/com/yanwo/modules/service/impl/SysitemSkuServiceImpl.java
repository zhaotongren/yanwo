package com.yanwo.modules.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.dao.SysitemItemDao;
import com.yanwo.dao.SysitemSkuDao;

import com.yanwo.entity.SysitemSkuEntity;

import com.yanwo.modules.service.SysitemSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("sysitemSkuService")
public class SysitemSkuServiceImpl extends ServiceImpl<SysitemSkuDao, SysitemSkuEntity> implements SysitemSkuService {

    @Autowired
    SysitemSkuDao sysitemSkuDao;


}
