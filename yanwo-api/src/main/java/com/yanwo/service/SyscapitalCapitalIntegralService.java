package com.yanwo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SyscapitalCapitalIntegralEntity;
import com.yanwo.utils.PageUtils;


/**
 * 积分记录
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-27 17:59:34
 */
public interface SyscapitalCapitalIntegralService extends IService<SyscapitalCapitalIntegralEntity> {

    PageUtils queryPage(Integer userId, int page);
}

