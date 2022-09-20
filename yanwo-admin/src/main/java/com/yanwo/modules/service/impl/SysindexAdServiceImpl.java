package com.yanwo.modules.service.impl;

import com.yanwo.dao.SysindexAdDao;
import com.yanwo.entity.SysindexAdEntity;
import com.yanwo.modules.service.SysindexAdService;
import com.yanwo.utils.PageUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.Query;


@Service("sysindexAdService")
public class SysindexAdServiceImpl extends ServiceImpl<SysindexAdDao, SysindexAdEntity> implements SysindexAdService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SysindexAdEntity> qw = new QueryWrapper<>();
        if(params.get("type") != null && StringUtils.isNotBlank(params.get("type").toString())){
            qw.eq("type",params.get("type").toString());
        }else{
            qw.eq("type",1);
        }
        IPage<SysindexAdEntity> page = this.page(
                new Query<SysindexAdEntity>().getPage(params),
                qw
        );

        return new PageUtils(page);
    }

}
