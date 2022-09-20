package com.yanwo.service.impl;

import com.yanwo.utils.PageUtils;
import com.yanwo.utils.Query;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yanwo.dao.SysPoppingRecordDao;
import com.yanwo.entity.SysPoppingRecordEntity;
import com.yanwo.service.SysPoppingRecordService;


@Service("sysPoppingRecordService")
public class SysPoppingRecordServiceImpl extends ServiceImpl<SysPoppingRecordDao, SysPoppingRecordEntity> implements SysPoppingRecordService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SysPoppingRecordEntity> page = this.page(
                new Query<SysPoppingRecordEntity>().getPage(params),
                new QueryWrapper<SysPoppingRecordEntity>()
        );

        return new PageUtils(page);
    }

}
