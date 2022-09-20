package com.yanwo.dao;

import com.yanwo.entity.SysitemSeckillEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-06-02 15:15:15
 */
@Mapper
public interface SysitemSeckillDao extends BaseMapper<SysitemSeckillEntity> {
    public List<SysitemSeckillEntity> listGoodsVo();
    public int reduceStock(Long id);
    public int revertStock(Long id);
}
