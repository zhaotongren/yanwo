package com.yanwo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanwo.entity.SyscategoryCatEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SyscategoryCatDao extends BaseMapper<SyscategoryCatEntity> {

    /**
     * 查询全部分类列表
     */
    List queryCatList();
    /**
     * 删除分类之前的查询
     */
    Integer beforeDeleteCheck(Integer cateId);


}
