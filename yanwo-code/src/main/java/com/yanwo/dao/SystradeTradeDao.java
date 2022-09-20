package com.yanwo.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanwo.entity.SystradeTradeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-04 16:51:45
 */
@Mapper
public interface SystradeTradeDao extends BaseMapper<SystradeTradeEntity> {
    public Integer selectMaxTradeId();
    BigDecimal sumByTid4portal(@Param("tids") List<String> tids);
    BigDecimal cumulativeByUser(@Param("userId")Integer userId);
    BigDecimal sumByParam(Map map);
    public BigDecimal statistics(Map<String, Object> params);
    public BigDecimal afterSale(Map params);
}
