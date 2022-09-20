package com.yanwo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.dao.SyscategoryCatDao;
import com.yanwo.entity.SyscategoryCatEntity;
import com.yanwo.service.SysCatService;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2018/6/5.
 */
@Service("sysCatService")
public class SysCatServiceImpl extends ServiceImpl<SyscategoryCatDao, SyscategoryCatEntity> implements SysCatService {


}
