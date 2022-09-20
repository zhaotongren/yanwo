package com.yanwo.modules.service.impl;

import com.yanwo.dao.UserGradeDao;
import com.yanwo.entity.UserGradeEntity;
import com.yanwo.modules.service.UserGradeService;
import com.yanwo.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.Query;


@Service("userGradeService")
public class UserGradeServiceImpl extends ServiceImpl<UserGradeDao, UserGradeEntity> implements UserGradeService {
    @Autowired
    private UserGradeDao userGradeDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserGradeEntity> page = this.page(
                new Query<UserGradeEntity>().getPage(params),
                new QueryWrapper<UserGradeEntity>()
        );

        return new PageUtils(page);
    }
    @Override
    public void cancelByParIdOne(Integer parIdOne){
        userGradeDao.cancelByParIdOne(parIdOne);
    }
    @Override
    public void cancelByParIdTwo(Integer parIdTwo){
        userGradeDao.cancelByParIdTwo(parIdTwo);
    }

}
