package com.yanwo.modules.service.impl;

import com.yanwo.modules.service.SysopenIdService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.dao.SysopenIdDao;
import com.yanwo.entity.SysopenIdEntity;


@Service("sysopenIdService")
public class SysopenIdServiceImpl extends ServiceImpl<SysopenIdDao, SysopenIdEntity> implements SysopenIdService {

}
