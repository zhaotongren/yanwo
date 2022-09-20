package com.yanwo.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SyscapitalCapitalIntegralEntity;
import com.yanwo.utils.PageUtils;

import java.util.Map;

/**
 * 积分记录
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-24 15:49:03
 */
public interface SyscapitalCapitalIntegralService extends IService<SyscapitalCapitalIntegralEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

