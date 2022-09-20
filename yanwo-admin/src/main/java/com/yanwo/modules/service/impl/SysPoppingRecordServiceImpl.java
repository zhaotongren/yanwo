package com.yanwo.modules.service.impl;

import com.yanwo.modules.service.SysPoppingRecordService;
import com.yanwo.utils.PageUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.Query;

import com.yanwo.dao.SysPoppingRecordDao;
import com.yanwo.entity.SysPoppingRecordEntity;


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
