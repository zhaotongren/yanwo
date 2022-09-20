package com.yanwo.modules.service.impl;

import com.yanwo.dao.SyscapitalCapitalDao;
import com.yanwo.entity.SyscapitalCapitalEntity;
import com.yanwo.modules.service.SyscapitalCapitalService;
import com.yanwo.utils.PageUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.Query;




@Service("syscapitalCapitalService")
public class SyscapitalCapitalServiceImpl extends ServiceImpl<SyscapitalCapitalDao, SyscapitalCapitalEntity> implements SyscapitalCapitalService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SyscapitalCapitalEntity> page = this.page(
                new Query<SyscapitalCapitalEntity>().getPage(params),
                new QueryWrapper<SyscapitalCapitalEntity>()
        );

        return new PageUtils(page);
    }

}
