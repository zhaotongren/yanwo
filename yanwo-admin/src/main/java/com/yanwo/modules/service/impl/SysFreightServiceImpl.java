package com.yanwo.modules.service.impl;

import com.yanwo.dao.SysFreightDao;
import com.yanwo.entity.SysFreightEntity;
import com.yanwo.modules.service.SysFreightService;
import com.yanwo.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.Query;

@Service("sysFreightService")
public class SysFreightServiceImpl extends ServiceImpl<SysFreightDao, SysFreightEntity> implements SysFreightService {

    @Autowired
    SysFreightDao sysFreightDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<SysFreightEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status",1);

        IPage<SysFreightEntity> page = this.page(
                new Query<SysFreightEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

}
