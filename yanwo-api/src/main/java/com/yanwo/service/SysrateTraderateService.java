package com.yanwo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SysrateTraderateEntity;
import com.yanwo.utils.PageUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-16 10:05:42
 */
public interface SysrateTraderateService extends IService<SysrateTraderateEntity> {

    PageUtils queryPage(QueryWrapper<SysrateTraderateEntity> queryWrapper,Integer currPage) throws UnsupportedEncodingException;
    List<SysrateTraderateEntity> selectRate(Integer itemId);
    Integer countRate(Integer itemId);
}

