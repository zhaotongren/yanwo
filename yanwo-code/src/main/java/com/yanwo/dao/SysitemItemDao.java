package com.yanwo.dao;

import com.yanwo.entity.SysitemItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-08-31 14:14:58
 */
@Mapper
public interface SysitemItemDao extends BaseMapper<SysitemItemEntity> {

    public List queryList(Map<String, Object> params);
    public Integer itemStore(Integer itemId);
    public Integer itemRate(Integer itemId);
}

