package com.yanwo.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SysFreightEntity;
import com.yanwo.utils.PageUtils;

import java.util.Map;

/**
 * 运费模板
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-14 11:49:07
 */
public interface SysFreightService extends IService<SysFreightEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

