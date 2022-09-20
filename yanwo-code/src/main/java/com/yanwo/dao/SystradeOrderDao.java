package com.yanwo.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanwo.entity.SystradeOrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-04 16:51:45
 */
@Mapper
public interface SystradeOrderDao extends BaseMapper<SystradeOrderEntity> {

    List<SystradeOrderEntity> getOrderByID(String tid);
    public BigDecimal selectOrderPayment(@Param("tids")List<Long> tids);
    public Integer selectMaxOrderId();
	
}
