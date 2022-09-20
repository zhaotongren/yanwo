package com.yanwo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SysitemItemEntity;

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

    public Integer itemStore(Integer itemId);
    public Integer itemRate(Integer itemId);
}

