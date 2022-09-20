package com.yanwo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SysitemItemEntity;
import com.yanwo.entity.SystradeLogisticsEntity;
import com.yanwo.utils.R;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-08-31 14:14:58
 */
public interface LogisticsService extends IService<SystradeLogisticsEntity> {

    R selectById(String tid,Integer userId);

}

