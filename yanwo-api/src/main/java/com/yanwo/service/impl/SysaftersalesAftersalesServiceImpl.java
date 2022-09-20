package com.yanwo.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.Query;

import com.yanwo.dao.SysaftersalesAftersalesDao;
import com.yanwo.entity.SysaftersalesAftersalesEntity;
import com.yanwo.service.SysaftersalesAftersalesService;


@Service("sysaftersalesAftersalesService")
public class SysaftersalesAftersalesServiceImpl extends ServiceImpl<SysaftersalesAftersalesDao, SysaftersalesAftersalesEntity> implements SysaftersalesAftersalesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SysaftersalesAftersalesEntity> page = this.page(
                new Query<SysaftersalesAftersalesEntity>().getPage(params),
                new QueryWrapper<SysaftersalesAftersalesEntity>()
        );

        return new PageUtils(page);
    }

}
