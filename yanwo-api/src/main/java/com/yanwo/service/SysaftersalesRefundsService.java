package com.yanwo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SystradeTradeEntity;
import com.yanwo.utils.PageUtils;
import com.yanwo.entity.SysaftersalesRefundsEntity;
import com.yanwo.utils.R;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-16 14:44:58
 */
public interface SysaftersalesRefundsService extends IService<SysaftersalesRefundsEntity> {

    PageUtils queryPage(int userId, int currPage);
    R tradeCancel(String tid, String reason, Integer userId, HttpServletRequest request);
    R refundgood(SysaftersalesRefundsEntity refundsEntity);
    R cancellogistics(Integer refundsId);
}

