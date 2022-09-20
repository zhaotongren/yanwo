package com.yanwo.modules.service.impl;

import com.yanwo.modules.service.SysPoppingService;
import com.yanwo.utils.PageUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.Query;

import com.yanwo.dao.SysPoppingDao;
import com.yanwo.entity.SysPoppingEntity;


@Service("sysPoppingService")
public class SysPoppingServiceImpl extends ServiceImpl<SysPoppingDao, SysPoppingEntity> implements SysPoppingService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SysPoppingEntity> page = this.page(
                new Query<SysPoppingEntity>().getPage(params),
                new QueryWrapper<SysPoppingEntity>()
        );

        return new PageUtils(page);
    }

}
