package com.yanwo.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SysitemSeckillEntity;
import com.yanwo.utils.PageUtils;

import java.util.Map;

/**
 * 
 *
 * @author wangbeibei
 * @date 2020-06-03 15:15:15
 */
public interface SysitemSeckillService extends IService<SysitemSeckillEntity> {
    PageUtils queryList(Map<String, Object> params, long pageSize, long pageCurrent) ;
}

