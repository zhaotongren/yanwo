package com.yanwo.service.impl;

import com.yanwo.dao.SysuserShoppingCartDao;
import com.yanwo.entity.SysuserShoppingCartEntity;
import com.yanwo.service.SysuserShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


@Service("sysuserShoppingCartService")
public class SysuserShoppingCartServiceImpl extends ServiceImpl<SysuserShoppingCartDao, SysuserShoppingCartEntity> implements SysuserShoppingCartService {
    @Autowired
    private SysuserShoppingCartDao sysuserShoppingCartDao;

    @Override
    public int updateCartNum(Integer userId, Integer skuId, Integer num){
        return sysuserShoppingCartDao.updateCartNum(userId, skuId, num);
    }
}
