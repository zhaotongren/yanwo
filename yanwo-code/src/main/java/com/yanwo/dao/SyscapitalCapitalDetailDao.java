package com.yanwo.dao;

import com.yanwo.entity.SyscapitalCapitalDetailEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-07 09:55:15
 */
@Mapper
public interface SyscapitalCapitalDetailDao extends BaseMapper<SyscapitalCapitalDetailEntity> {
    BigDecimal sumCapitalByParam(Map map);

    
    public List findDetailList(Map params);
}
