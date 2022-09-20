package com.yanwo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SysPoppingEntity;
import com.yanwo.utils.PageUtils;

import java.util.Map;

/**
 * 弹窗
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-06-08 11:06:51
 */
public interface SysPoppingService extends IService<SysPoppingEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

