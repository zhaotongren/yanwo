package com.yanwo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SyscapitalCapitalDetailEntity;
import com.yanwo.utils.PageUtils;
import io.swagger.models.auth.In;

import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-07 09:55:15
 */
public interface SyscapitalCapitalDetailService extends IService<SyscapitalCapitalDetailEntity> {
    PageUtils queryPage(Integer userId, int page, Integer type);
}

