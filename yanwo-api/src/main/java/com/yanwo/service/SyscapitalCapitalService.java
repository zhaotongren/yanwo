package com.yanwo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SyscapitalCapitalEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-07 09:55:15
 */
public interface SyscapitalCapitalService extends IService<SyscapitalCapitalEntity> {
    BigDecimal getUserWalletFee(Integer userId);
    void saveTradeOrderWelfare(String paymentId,BigDecimal payWalletFee,Integer userId);
    void saveTradeOrderRecharge(List<String> tradeIds, Integer userId, BigDecimal totalFee);
}

