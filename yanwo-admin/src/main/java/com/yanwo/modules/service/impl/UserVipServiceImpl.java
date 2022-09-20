package com.yanwo.modules.service.impl;

import com.yanwo.dao.UserVipDao;
import com.yanwo.entity.UserVipEntity;
import com.yanwo.modules.service.UserVipService;
import com.yanwo.utils.PageUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.Query;



@Service("userVipService")
public class UserVipServiceImpl extends ServiceImpl<UserVipDao, UserVipEntity> implements UserVipService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserVipEntity> page = this.page(
                new Query<UserVipEntity>().getPage(params),
                new QueryWrapper<UserVipEntity>()
        );

        return new PageUtils(page);
    }

}
