package com.yanwo.modules.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SyscapitalCapitalEntity;
import com.yanwo.utils.PageUtils;

import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-17 10:10:25
 */
public interface SyscapitalCapitalService extends IService<SyscapitalCapitalEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

