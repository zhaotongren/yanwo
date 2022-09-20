package com.yanwo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SysQuestionsEntity;
import com.yanwo.utils.PageUtils;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-16 14:44:58
 */
public interface SysQuestionsService extends IService<SysQuestionsEntity> {
    PageUtils queryPage(QueryWrapper<SysQuestionsEntity> queryWrapper, Integer page);
}

