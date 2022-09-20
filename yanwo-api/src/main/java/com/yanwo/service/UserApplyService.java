package com.yanwo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.UserApplyEntity;
import com.yanwo.utils.PageUtils;

import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-05-08 16:57:42
 */
public interface UserApplyService extends IService<UserApplyEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

