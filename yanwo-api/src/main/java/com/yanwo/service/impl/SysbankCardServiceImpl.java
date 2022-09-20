package com.yanwo.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yanwo.dao.SysbankCardDao;
import com.yanwo.entity.SysbankCardEntity;
import com.yanwo.service.SysbankCardService;


@Service("sysbankCardService")
public class SysbankCardServiceImpl extends ServiceImpl<SysbankCardDao, SysbankCardEntity> implements SysbankCardService {

}
