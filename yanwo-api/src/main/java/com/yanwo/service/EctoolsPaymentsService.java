package com.yanwo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.EctoolsPaymentsEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-07 11:20:59
 */
public interface EctoolsPaymentsService extends IService<EctoolsPaymentsEntity> {
    String paymentPayready(String pay_app_id, String payname, List<String> tids, Integer userId);
    int paymentPaySyntony(String paymentId, Date gmt_close);
    int paymentPaySyntony(String paymentId, Date gmt_close,String payType ,BigDecimal payMoney);
    //不支付直接回调
    int noPayTrade(String paymode, String paymentId, Date gmt_close);
}

