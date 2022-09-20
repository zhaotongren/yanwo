package com.yanwo.modules.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SysitemItemEntity;
import com.yanwo.utils.PageUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-08-31 14:14:58
 */
public interface SysitemItemService extends IService<SysitemItemEntity> {

    PageUtils queryList(Map<String, Object> params,long pageSize, long pageCurrent) ;
    void putAway(Integer[] ids,Integer status);
}

