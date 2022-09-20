package com.yanwo.dao;

import com.yanwo.entity.SysuserShoppingCartEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-06 10:01:29
 */
@Mapper
public interface SysuserShoppingCartDao extends BaseMapper<SysuserShoppingCartEntity> {
    public int updateCartNum(@Param("userId") Integer userId, @Param("skuId") Integer skuId, @Param("num") Integer num);

}
