package com.yanwo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SysrechargeCardEntity;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;

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
    List<SysrechargeCardEntity> queryByCardList(String cardNo);
    R recharge(Integer userId, String cardNo, String password);
}

