package com.yanwo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SysPoppingRecordEntity;
import com.yanwo.utils.PageUtils;

import java.util.Map;

/**
 * 弹窗记录
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-06-08 11:06:51
 */
public interface SysPoppingRecordService extends IService<SysPoppingRecordEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

