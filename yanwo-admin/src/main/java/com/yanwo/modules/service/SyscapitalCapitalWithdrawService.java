package com.yanwo.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SyscapitalCapitalWithdrawEntity;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;

import java.util.Map;

/**
 * 钱包提现记录
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-11 15:38:45
 */
public interface SyscapitalCapitalWithdrawService extends IService<SyscapitalCapitalWithdrawEntity> {

    PageUtils queryPage(Map<String, Object> params);
    R audit(Map map);
}

