package com.yanwo.dao;

import com.yanwo.entity.SyscapitalCapitalWithdrawEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-07 09:55:15
 */
@Mapper
public interface SyscapitalCapitalWithdrawDao extends BaseMapper<SyscapitalCapitalWithdrawEntity> {
    BigDecimal getWithdrawMoneyByCapital(@Param("capitalId")Integer capitalId, @Param("status")Integer status);
	
}
