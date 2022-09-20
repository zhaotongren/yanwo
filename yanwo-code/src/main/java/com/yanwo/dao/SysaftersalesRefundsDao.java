package com.yanwo.dao;

import com.yanwo.entity.SysaftersalesRefundsEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-16 14:44:58
 */
@Mapper
public interface SysaftersalesRefundsDao extends BaseMapper<SysaftersalesRefundsEntity> {
    public String selectMaxRetundBn();
    public List<Map>  find(Map params);

    public Integer selectAftersales(Integer userId);
}
