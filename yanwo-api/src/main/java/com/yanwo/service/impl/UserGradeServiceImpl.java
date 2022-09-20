package com.yanwo.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.dao.UserGradeDao;
import com.yanwo.entity.UserGradeEntity;
import com.yanwo.service.UserGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service("userGradeService")
public class UserGradeServiceImpl extends ServiceImpl<UserGradeDao, UserGradeEntity> implements UserGradeService {

    @Autowired
    UserGradeDao userGradeDao;

    @Override
    public Integer queryAllInvite(Integer userId) {
        return userGradeDao.queryAllInvite(userId);
    }

    @Override
    public Integer queryIndirectInvite(Integer userId) {
        return userGradeDao.queryIndirectInvite(userId);
    }

}
