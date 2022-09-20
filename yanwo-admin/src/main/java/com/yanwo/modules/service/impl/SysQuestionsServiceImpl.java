package com.yanwo.modules.service.impl;

import com.yanwo.modules.service.SysQuestionsService;
import com.yanwo.utils.PageUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.Query;

import com.yanwo.dao.SysQuestionsDao;
import com.yanwo.entity.SysQuestionsEntity;

@Service("sysQuestionsService")
public class SysQuestionsServiceImpl extends ServiceImpl<SysQuestionsDao, SysQuestionsEntity> implements SysQuestionsService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SysQuestionsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("status", 0);

        IPage<SysQuestionsEntity> page = this.page(
                new Query<SysQuestionsEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

}
