package com.yanwo.service.impl;

import com.yanwo.utils.PageUtils;
import com.yanwo.utils.Query;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.yanwo.dao.UserApplyDao;
import com.yanwo.entity.UserApplyEntity;
import com.yanwo.service.UserApplyService;


@Service("userApplyService")
public class UserApplyServiceImpl extends ServiceImpl<UserApplyDao, UserApplyEntity> implements UserApplyService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserApplyEntity> page = this.page(
                new Query<UserApplyEntity>().getPage(params),
                new QueryWrapper<UserApplyEntity>()
        );

        return new PageUtils(page);
    }

}
