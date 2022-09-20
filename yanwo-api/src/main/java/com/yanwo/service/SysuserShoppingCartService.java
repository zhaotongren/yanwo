package com.yanwo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.dao.SysuserShoppingCartDao;
import com.yanwo.entity.SysuserShoppingCartEntity;
import com.yanwo.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-06 10:01:29
 */
public interface SysuserShoppingCartService extends IService<SysuserShoppingCartEntity> {
    int updateCartNum(Integer userId, Integer skuId, Integer num);
}

