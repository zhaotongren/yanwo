package com.yanwo.dao;

import com.yanwo.entity.SyscapitalCapitalPayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * 钱包支付记录
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-07 15:07:28
 */
@Mapper
public interface SyscapitalCapitalPayDao extends BaseMapper<SyscapitalCapitalPayEntity> {
    BigDecimal getPayMoneyByCapital(@Param("capitalId")Integer capitalId, @Param("status")Integer status);
}
