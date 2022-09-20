package com.yanwo.modules.service.impl;

import com.yanwo.dao.SysindexArtDao;
import com.yanwo.entity.SysindexArtEntity;
import com.yanwo.modules.service.SysindexArtService;
import com.yanwo.utils.PageUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.Query;


@Service("sysindexArtService")
public class SysindexArtServiceImpl extends ServiceImpl<SysindexArtDao, SysindexArtEntity> implements SysindexArtService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SysindexArtEntity> page = this.page(
                new Query<SysindexArtEntity>().getPage(params),
                new QueryWrapper<SysindexArtEntity>().ne("type","3")
        );

        return new PageUtils(page);
    }

}
