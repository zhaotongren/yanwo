package com.yanwo.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SysleaveMessageEntity;
import com.yanwo.utils.PageUtils;

import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-05 14:57:16
 */
public interface SysleaveMessageService extends IService<SysleaveMessageEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

