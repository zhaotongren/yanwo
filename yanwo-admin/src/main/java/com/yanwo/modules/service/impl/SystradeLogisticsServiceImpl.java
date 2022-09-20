package com.yanwo.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.Query;
import com.yanwo.dao.SystradeLogisticsDao;
import com.yanwo.entity.SystradeLogisticsEntity;
import com.yanwo.modules.service.SystradeLogisticsService;
import com.yanwo.utils.PageUtils;
import org.springframework.stereotype.Service;
import java.util.Map;



@Service("systradeLogisticsService")
public class SystradeLogisticsServiceImpl extends ServiceImpl<SystradeLogisticsDao, SystradeLogisticsEntity> implements SystradeLogisticsService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SystradeLogisticsEntity> page = this.page(
                new Query<SystradeLogisticsEntity>().getPage(params),
                new QueryWrapper<SystradeLogisticsEntity>()
        );

        return new PageUtils(page);
    }

}
