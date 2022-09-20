package com.yanwo.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.Query;
import com.yanwo.dao.SysnotifyMessageDao;
import com.yanwo.entity.SysnotifyMessageEntity;
import com.yanwo.modules.service.SysnotifyMessageService;
import com.yanwo.utils.PageUtils;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service("sysnotifyMessageService")
public class SysnotifyMessageServiceImpl extends ServiceImpl<SysnotifyMessageDao, SysnotifyMessageEntity> implements SysnotifyMessageService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SysnotifyMessageEntity> page = this.page(
                new Query<SysnotifyMessageEntity>().getPage(params),
                new QueryWrapper<SysnotifyMessageEntity>()
        );

        return new PageUtils(page);
    }

}
