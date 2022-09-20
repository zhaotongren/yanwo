package com.yanwo.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SysrechargeCardEntity;
import com.yanwo.utils.PageUtils;

import java.util.List;
import java.util.Map;


/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-08-31 14:43:44
 */
public interface SysrechargeCardService extends IService<SysrechargeCardEntity> {
    PageUtils queryPage(Map<String, Object> params);
    List<SysrechargeCardEntity> queryByCardList(String cardNo);
}

