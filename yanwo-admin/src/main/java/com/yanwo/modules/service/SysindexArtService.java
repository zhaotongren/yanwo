package com.yanwo.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SysindexArtEntity;
import com.yanwo.utils.PageUtils;

import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-05 10:55:05
 */
public interface SysindexArtService extends IService<SysindexArtEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

