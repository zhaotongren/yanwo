package com.yanwo.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.UserVipEntity;
import com.yanwo.utils.PageUtils;

import java.util.Map;

/**
 * 用户购买会员的记录
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-04 15:30:43
 */
public interface UserVipService extends IService<UserVipEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

