package com.yanwo.modules.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SystradeTradeEntity;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-04 16:51:45
 */
public interface SystradeTradeService extends IService<SystradeTradeEntity> {

    PageUtils queryPage( Map<String, Object> params,long pageSize,long pageNum);
    void settlementTrade();
    BigDecimal statistics(Map params);
    BigDecimal afterSale(Map params);
    void receiveTrade();
    void closeTrade();

}

