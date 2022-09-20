package com.yanwo.dao;

import com.yanwo.entity.SysrateTraderateEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-16 10:05:42
 */
@Mapper
public interface SysrateTraderateDao extends BaseMapper<SysrateTraderateEntity> {

    List<SysrateTraderateEntity> selectRate(Integer itemId);
    Integer countRate(Integer itemId);
}
