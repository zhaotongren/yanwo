package com.yanwo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.utils.PageUtils;
import com.yanwo.entity.SysaftersalesAftersalesEntity;

import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-16 14:44:58
 */
public interface SysaftersalesAftersalesService extends IService<SysaftersalesAftersalesEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

