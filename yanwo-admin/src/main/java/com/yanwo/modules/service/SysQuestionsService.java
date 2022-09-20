package com.yanwo.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SysQuestionsEntity;
import com.yanwo.utils.PageUtils;

import java.util.Map;

/**
 * 问答表
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-18 10:32:32
 */
public interface SysQuestionsService extends IService<SysQuestionsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

