package com.yanwo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.dao.*;
import com.yanwo.entity.*;
import com.yanwo.service.SyscapitalCapitalService;
import com.yanwo.utils.DateUtils;
import net.bytebuddy.asm.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("syscapitalCapitalService")
public class SyscapitalCapitalServiceImpl extends ServiceImpl<SyscapitalCapitalDao, SyscapitalCapitalEntity> implements SyscapitalCapitalService {
    private static final Logger logger = LoggerFactory.getLogger(SyscapitalCapitalServiceImpl.class);
    @Resource
    SyscapitalCapitalWithdrawDao syscapitalCapitalWithdrawDao;
    @Resource
    EctoolsTradePaybillDao ectoolsTradePaybillDao;
    @Resource
    SystradeTradeDao systradeTradeDao;
    @Resource
    SystradeOrderDao systradeOrderDao;
    @Resource
    SyscapitalCapitalPayDao syscapitalCapitalPayDao;
    @Resource
    SyscapitalCapitalDao syscapitalCapitalDao;
    @Resource
    private SyscapitalCapitalDetailDao capitalDetailDao;

    @Override
    public BigDecimal getUserWalletFee(Integer userId){
        QueryWrapper<SyscapitalCapitalEntity> queryWrapper1 = new QueryWrapper<SyscapitalCapitalEntity> ();
        queryWrapper1.eq("user_id",userId);
        SyscapitalCapitalEntity capital = getOne(queryWrapper1);
        if(capital != null){
            //待审核的提现金额
            BigDecimal WithdrawMoney = syscapitalCapitalWithdrawDao.getWithdrawMoneyByCapital(capital.getCapitalId(),0);
            return capital.getTotalCapital().subtract(WithdrawMoney);
        }
        return BigDecimal.ZERO;
    }
    @Override
    public void saveTradeOrderWelfare(String paymentId,BigDecimal payWalletFee,Integer userId) {
        logger.info("进入钱包抵扣计算，钱包支付金额为" + payWalletFee);
        QueryWrapper<EctoolsTradePaybillEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("payment_id",paymentId);
        List<EctoolsTradePaybillEntity> paybills = ectoolsTradePaybillDao.selectList(queryWrapper);

        List<Long> tids = new ArrayList<>();
        for (EctoolsTradePaybillEntity paybill : paybills) {
            tids.add(Long.parseLong(paybill.getTid()));
        }
        BigDecimal paymentWallet = BigDecimal.valueOf(0);
        BigDecimal allPayment = systradeOrderDao.selectOrderPayment(tids);
        //更新子订单和大订单
        logger.info("进入钱包抵扣计算，钱包抵扣金额为"+payWalletFee+"开始更新订单信息");
        for (int i = 0; i < tids.size(); i++) {
            SystradeTradeEntity trade = systradeTradeDao.selectById(tids.get(i));
            BigDecimal tradeWelfare = BigDecimal.valueOf(0);
            //查询小订单
            QueryWrapper<SystradeOrderEntity> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("tid",trade.getTid());
            List<SystradeOrderEntity> orderList = systradeOrderDao.selectList(queryWrapper1);
            for (int j = 0; j < orderList.size(); j++) {
                SystradeOrderEntity order = orderList.get(j);
                BigDecimal orderWelfare ;
                if(i==tids.size()-1&&j==orderList.size()-1){//最后一个小订单
                    orderWelfare = payWalletFee.subtract(paymentWallet).setScale(2,BigDecimal.ROUND_HALF_UP);
                }else{
                    double order_welfare = order.getPayment().doubleValue()/allPayment.doubleValue()*payWalletFee.doubleValue();
                    orderWelfare = new BigDecimal(order_welfare).setScale(2,BigDecimal.ROUND_HALF_UP);
                }
                order.setWelfareFee(orderWelfare);
                order.setPayment(order.getPayment().subtract(orderWelfare));
                systradeOrderDao.updateById(order);
                logger.info("子订单扣减钱包"+orderWelfare);

                tradeWelfare = tradeWelfare.add(orderWelfare);
                paymentWallet = paymentWallet.add(orderWelfare);
            }
            trade.setWelfareFee(tradeWelfare);
            trade.setPayment(trade.getPayment().subtract(tradeWelfare));
            systradeTradeDao.updateById(trade);
            logger.info("大订单扣减钱包"+tradeWelfare);
            //添加红利绑定订单记录表记录
            QueryWrapper<SyscapitalCapitalEntity> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("user_id",userId);
            SyscapitalCapitalEntity capital = syscapitalCapitalDao.selectOne(queryWrapper2);
            SyscapitalCapitalPayEntity welfarePay = new SyscapitalCapitalPayEntity();
            welfarePay.setTid(trade.getTid());
            welfarePay.setPaymentId(paymentId);
            welfarePay.setCapitalId(capital.getCapitalId());
            welfarePay.setPayFee(tradeWelfare);
            welfarePay.setStatus(0);
            welfarePay.setCreatedTime(DateUtils.currentUnixTime());
            welfarePay.setModifiedTime(DateUtils.currentUnixTime());
            syscapitalCapitalPayDao.insert(welfarePay);
        }
    }
    @Override
    public void saveTradeOrderRecharge(List<String> tradeIds,Integer userId,BigDecimal totalFee){
        //用户资产信息
        QueryWrapper<SyscapitalCapitalEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        SyscapitalCapitalEntity capital = syscapitalCapitalDao.selectOne(queryWrapper);
        if(capital != null){
            if(capital.getTotalRecharge().compareTo(totalFee) > 0){
                for(String tid : tradeIds){
                    //查询小订单
                    QueryWrapper<SystradeOrderEntity> queryWrapper1 = new QueryWrapper<>();
                    queryWrapper1.eq("tid",tid);
                    List<SystradeOrderEntity> orderList = systradeOrderDao.selectList(queryWrapper1);
                    for (SystradeOrderEntity order : orderList) {
                        order.setStatus("2");
                        order.setRechargeFee(order.getPayment().subtract(order.getWelfareFee()));
                        systradeOrderDao.updateById(order);
                    }
                    //更新大订单
                    SystradeTradeEntity trade = systradeTradeDao.selectById(Long.valueOf(tid));
                    trade.setRechargeFee(trade.getPayment().subtract(trade.getWelfareFee()));
                    trade.setStatus("2");
                    trade.setPayTime(DateUtils.currentUnixTime());
                    trade.setModifiedTime(DateUtils.currentUnixTime());
                    systradeTradeDao.updateById(trade);
                    //增加流水
                    SyscapitalCapitalDetailEntity capitalDetail = new SyscapitalCapitalDetailEntity();
                    capitalDetail.setCapitalId(capital.getCapitalId());
                    capitalDetail.setCapitalType(8);
                    capitalDetail.setUserId(capital.getUserId());
                    capitalDetail.setCapitalFee(totalFee.multiply(new BigDecimal(-1)));
                    capitalDetail.setCreatedTime(DateUtils.currentUnixTime());
                    capitalDetailDao.insert(capitalDetail);
                    //减少充值余额
                    capital.setTotalRecharge(capital.getTotalRecharge().subtract(totalFee));
                    capital.setTotalCapital(capital.getTotalCapital().subtract(totalFee));
                    syscapitalCapitalDao.updateById(capital);
                }
            }
        }
    }

}
