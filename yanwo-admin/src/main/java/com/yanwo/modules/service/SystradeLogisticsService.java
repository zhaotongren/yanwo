package com.yanwo.modules.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SystradeLogisticsEntity;
import com.yanwo.utils.PageUtils;

import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-05 18:17:58
 */
public interface SystradeLogisticsService extends IService<SystradeLogisticsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

