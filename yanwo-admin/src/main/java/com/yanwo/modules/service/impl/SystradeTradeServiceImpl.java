package com.yanwo.modules.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.GUtils;
import com.yanwo.common.utils.Query;
import com.yanwo.dao.*;
import com.yanwo.entity.*;
import com.yanwo.modules.service.SysitemItemService;
import com.yanwo.modules.service.SystradeOrderService;
import com.yanwo.modules.service.SystradeTradeService;
import com.yanwo.modules.service.UserService;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.JsonUtils;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("systradeTradeService")
public class SystradeTradeServiceImpl extends ServiceImpl<SystradeTradeDao, SystradeTradeEntity> implements SystradeTradeService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SystradeOrderService systradeOrderService;
    @Autowired
    private UserService userService;
    @Autowired
    private SysConfigDao sysConfigDao;
    @Autowired
    private SyscapitalCapitalDao syscapitalCapitalDao;
    @Autowired
    private SyscapitalCapitalDetailDao syscapitalCapitalDetailDao;
    @Autowired
    private SystradeTradeDao systradeTradeDao;
    @Autowired
    private SysitemItemService sysitemItemService;
    @Autowired
    private SysaftersalesRefundsDao sysaftersalesRefundsDao;
    @Autowired
    private SyscapitalCapitalIntegralDao syscapitalCapitalIntegralDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params, long pageSize, long pageNum) {
/*        List list = new ArrayList();
        if (!params.get("userName").toString().equals("") && params.get("mobile").toString().equals("")) {
            list = idList(params.get("userName").toString(),"");
        }
        if (!params.get("mobile").toString().equals("")&& params.get("userName").toString().equals("")) {
            list = idList("",params.get("mobile").toString());
        }
        if (!params.get("mobile").toString().equals("")&& !params.get("userName").toString().equals("")) {
            list = idList(params.get("userName").toString(),params.get("mobile").toString());
        }*/
        IPage iPage = null;
        try {
            Page<SystradeTradeEntity> pageW = new Page();
            pageW.setSize(pageSize);
            pageW.setCurrent(pageNum);
            QueryWrapper<SystradeTradeEntity> tradeWrapper = new QueryWrapper<>();
            tradeWrapper.ne("status",6).ne("status",1);
            tradeWrapper.eq(!params.get("tid").toString().equals(""), "tid", params.get("tid").toString())
                    .eq(!params.get("status").toString().equals(""), "status", params.get("status").toString())
                    .ge(!params.get("startime").toString().equals(""), "created_time", GUtils.stringDateToStringTimestamp(params.get("startime").toString()))
                    .le(!params.get("endtime").toString().equals(""), "created_time", GUtils.stringDateToStringTimestamp(params.get("endtime").toString()))
                    .eq("deleted", false);
            tradeWrapper.orderByDesc("created_time");
            iPage = page(pageW, tradeWrapper);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new PageUtils(formatPage(iPage));
    }

    IPage formatPage(IPage page) {
        List<SystradeTradeEntity> list = page.getRecords();
        for (SystradeTradeEntity trade : list) {
            Integer userid = trade.getUserId();
            UserEntity userEntity = userService.getById(userid);
            if (userEntity==null){
                continue;
            }
            try {
                trade.setUserName(userEntity.getNickName()==null?null:URLDecoder.decode(userEntity.getNickName(), "utf-8"));
                trade.setRemark(trade.getRemark()!=null ? URLDecoder.decode(trade.getRemark(), "utf-8"):"");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            trade.setRealName(userEntity.getRealName());
            trade.setCardId(userEntity.getCardId());
            trade.setUserPic(userEntity.getCerHandPic());
            List<SystradeOrderEntity> orderList = systradeOrderService.getOrderByTid(trade.getTid());
            trade.setOrderList(orderList);
            trade.setCreatedTimeValue(GUtils.IntegerToDate(trade.getCreatedTime()));
            System.out.println(GUtils.IntegerToDate(trade.getCreatedTime()));
            if (trade.getPayTime() != null) {
                trade.setPayTimeValue(GUtils.IntegerToDate(trade.getPayTime()));
            } else {
                trade.setPayTimeValue("");
            }
            if (trade.getConsignTime() != null) {
                trade.setConsignTimeValue(GUtils.IntegerToDate(trade.getConsignTime()));
            } else {
                trade.setConsignTimeValue("");
            }
            if (trade.getEndTime() != null) {
                trade.setFinishTimeValue(GUtils.IntegerToDate(trade.getEndTime()));
            } else {
                trade.setFinishTimeValue("");
            }
            if (trade.getSettlementTime() != null) {
                trade.setSettlementTimeValue(GUtils.IntegerToDate(trade.getSettlementTime()));
            } else {
                trade.setSettlementTimeValue("");
            }

        }

        return page;
    }

    List idList(String userName, String mobile) {
        List relist = new ArrayList();
        Map map = new HashMap();
        map.put("nickName", userName);

        map.put("mobile", mobile);
        System.out.println(userName);
        PageUtils page = userService.queryPage(map);
        List<UserEntity> uelist = (List<UserEntity>) page.getList();
        for (UserEntity userEntity : uelist) {
            relist.add(userEntity.getUserId());
        }
        return relist;
    }

    @Override
    public void settlementTrade() {
        try {
            Integer settle_period = Integer.valueOf(sysConfigDao.getByKey("settle.period"));
            BigDecimal integral_rate = new BigDecimal(sysConfigDao.getByKey("integral_rate"));
            //查询已完成状态的订单
            QueryWrapper<SystradeTradeEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", "4");//已完成
//            queryWrapper.eq("type", 0);//普通单子
            List<SystradeTradeEntity> tradelist = this.list(queryWrapper);
            Integer nowtime = DateUtils.currentUnixTime();
            for (SystradeTradeEntity trade : tradelist) {
                if (trade.getEndTime() != null && nowtime - trade.getEndTime() >= settle_period * 24 * 60 * 60) {
                    System.out.println("------------------订单号：" + trade.getTid() + "开始结算------------------------");
                    List<SystradeOrderEntity> orderList = systradeOrderService.getOrderByTid(trade.getTid());
                    int calFlag = 0;
                    for (int a = 0; a < orderList.size(); a++) {
                        SystradeOrderEntity order = orderList.get(a);
                        if (order.getIsSettlement() == 1) {//已经结算过 跳过
                            continue;
                        }
                        //售后已经结束，判断要结算的金额
                        if (toolCheckAftersales(order, settle_period) == 0) {
                            //积分或者秒杀订单  不返利 直接更新为已完结
                            if(trade.getType() == 1 || trade.getType() == 3){
                                order.setIsSettlement(1);
                                order.setStatus("5");
                                systradeOrderService.updateById(order);
                                continue;
                            }
                            BigDecimal num = toolCheckAftersalesNum(order);//判断还需要结算多少
                            if (num.compareTo(BigDecimal.valueOf(0)) == 1) {
                                //开始结算返利
                                if (StringUtils.isNotBlank(trade.getRateParam())) {//返利
                                    List<Map> rateList = JsonUtils.jsonToList(trade.getRateParam(), Map.class);
                                    //商品利润 * 比例   改为 销售价*比例
//                                    BigDecimal rebateMoney = (order.getPrice().subtract(order.getCostPrice())).multiply(num).setScale(BigDecimal.ROUND_HALF_UP, 2);
                                    BigDecimal totalFee = order.getPayment();
                                    if (totalFee.compareTo(BigDecimal.ZERO) == 1) {
                                        for (Map ratemap : rateList) {
                                            Integer userId = Integer.valueOf(ratemap.get("userId").toString());//收益人
                                            BigDecimal rate = new BigDecimal(ratemap.get("rate").toString());//返利比例
                                            Integer type = Integer.valueOf(ratemap.get("type").toString());//1：直级返利 2：间接返利  3：团长返利
                                            if(totalFee.multiply(rate).compareTo(BigDecimal.ZERO) < 1){
                                                continue;
                                            }
                                            //查询资产信息
                                            QueryWrapper<SyscapitalCapitalEntity> qw = new QueryWrapper();
                                            qw.eq("user_id", userId);
                                            SyscapitalCapitalEntity capital = syscapitalCapitalDao.selectOne(qw);
                                            if (capital == null) {
                                                capital = new SyscapitalCapitalEntity();
                                                capital.setUserId(userId);
                                                capital.setTotalCapital(BigDecimal.ZERO);
                                                capital.setTotalIntegral(BigDecimal.ZERO);
                                                syscapitalCapitalDao.insert(capital);
                                            }
                                            //插入返利记录
                                            SyscapitalCapitalDetailEntity capitalDetail = new SyscapitalCapitalDetailEntity();
                                            capitalDetail.setUserId(userId);
                                            capitalDetail.setCapitalId(capital.getCapitalId());
                                            capitalDetail.setTid(trade.getTid());
                                            capitalDetail.setOid(order.getOid());
                                            capitalDetail.setCapitalType(type);
                                            capitalDetail.setCapitalDesc(getTypeValue(type));
                                            capitalDetail.setCapitalFee(totalFee.multiply(rate).setScale(2,BigDecimal.ROUND_HALF_UP));
                                            capitalDetail.setCreatedTime(DateUtils.currentUnixTime());
                                            syscapitalCapitalDetailDao.insert(capitalDetail);
                                            //更新总金额
                                            capital.setTotalCapital(capital.getTotalCapital().add(capitalDetail.getCapitalFee()));
                                            syscapitalCapitalDao.updateById(capital);
                                        }
                                    }
                                }
                                //查询用户资产信息
                                QueryWrapper<SyscapitalCapitalEntity> queryWrapper_ = new QueryWrapper();
                                queryWrapper_.eq("user_id", order.getUserId());
                                SyscapitalCapitalEntity capital_ = syscapitalCapitalDao.selectOne(queryWrapper_);
                                if (capital_ == null) {
                                    capital_ = new SyscapitalCapitalEntity();
                                    capital_.setUserId(order.getUserId());
                                    capital_.setTotalCapital(BigDecimal.ZERO);
                                    capital_.setTotalIntegral(BigDecimal.ZERO);
                                    syscapitalCapitalDao.insert(capital_);
                                }
                                //插入积分记录
                                SyscapitalCapitalIntegralEntity integralEntity = new SyscapitalCapitalIntegralEntity();
                                integralEntity.setUserId(trade.getUserId());
                                integralEntity.setCapitalId(capital_.getCapitalId());
                                integralEntity.setTid(trade.getTid());
                                integralEntity.setOid(order.getOid());
                                integralEntity.setStatus(1);//0：无效 1：有效  2：占用中
                                integralEntity.setIntegralType(1);//1：产生 2：消耗
                                integralEntity.setCreatedTime(DateUtils.currentUnixTime());
                                //产生比例是商品售价的 N%
                                BigDecimal integralFee = order.getPayment().multiply(integral_rate).setScale(2,BigDecimal.ROUND_HALF_UP);
                                integralEntity.setIntegralFee(integralFee);
                                syscapitalCapitalIntegralDao.insert(integralEntity);
                                //更新总积分
                                capital_.setTotalIntegral(capital_.getTotalIntegral().add(integralFee));
                                syscapitalCapitalDao.updateById(capital_);
                                //更新小定单
                                order.setIsSettlement(1);
                                systradeOrderService.updateById(order);
                            } else {
                                //更新小定单
                                order.setIsSettlement(1);
                                systradeOrderService.updateById(order);
                            }

                        } else {
                            calFlag++;
                        }
                    }
                    if (calFlag == 0) {//小定单全部结算
                        //更新订单状态为已完结
                        trade.setStatus("5");
                        trade.setModifiedTime(DateUtils.currentUnixTime());
                        trade.setSettlementTime(DateUtils.currentUnixTime());//结算时间
                        this.updateById(trade);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //判断订单售后是否结束   已检验
    public int toolCheckAftersales(SystradeOrderEntity order, Integer settle_period) {
        if (StringUtils.isBlank(order.getAftersalesStatus())) {
            return 0;
        }
        QueryWrapper<SysaftersalesRefundsEntity> queryWrapper = new QueryWrapper();
        queryWrapper.eq("tid", order.getTid());
        queryWrapper.eq("oid", order.getOid());
        List<SysaftersalesRefundsEntity> refundsList = sysaftersalesRefundsDao.selectList(queryWrapper);
        for (int i = 0; i < refundsList.size(); i++) {
            SysaftersalesRefundsEntity refundsEntity = refundsList.get(i);
            //1待审核  2待回寄  3审核不通过  4待退款  5退款完成  6退款驳回
            if ("1".equals(refundsEntity.getStatus()) || "2".equals(refundsEntity.getStatus()) || "4".equals(refundsEntity.getStatus())) {
                logger.info("售后还未完成oid=" + order.getOid());
                return 1;
            }
            if (DateUtils.currentUnixTime() - refundsEntity.getModifiedTime() < settle_period * 24 * 60 * 60) {
                logger.info("售后期还未结束oid=" + order.getOid());
                return 1;
            }
        }
        return 0;
    }
    //判断售后订单还需要结算哪些
    public BigDecimal toolCheckAftersalesNum(SystradeOrderEntity order) {
        if (order.getAftersalesStatus() == null) {
            return BigDecimal.valueOf(order.getNum());
        }
        QueryWrapper<SysaftersalesRefundsEntity> queryWrapper = new QueryWrapper();
        queryWrapper.eq("tid", order.getTid());
        queryWrapper.eq("oid", order.getOid());
        queryWrapper.eq("status", "5");//查到的是退款成功的没有被驳回的
        List<SysaftersalesRefundsEntity> refundsList = sysaftersalesRefundsDao.selectList(queryWrapper);
        //计算出子订单中退款的数量
        BigDecimal refundNum = BigDecimal.valueOf(0);
        for (int a = 0; a < refundsList.size(); a++) {
            SysaftersalesRefundsEntity refund = refundsList.get(a);
            refundNum = refundNum.add(BigDecimal.valueOf(refund.getRefundNum()));//退款数量
        }
        //子订单总数量减去退款数量得到可结算数量
        BigDecimal num = BigDecimal.valueOf(order.getNum()).subtract(refundNum);
        logger.info("子订单中未退款成功数量为 num=" + num + "开始结算未成功退款的");
        if (num.compareTo(BigDecimal.valueOf(0)) == 1) {//说明要结算
            return num;
        } else {
            return BigDecimal.valueOf(0);
        }
    }

    private String getTypeValue(Integer type) {
        switch (type) {
            case 1:
                return "直级返利";
            case 2:
                return "间接返利";
            case 3:
                return "团长返利";
            default:
                return "";
        }
    }

    @Override
    public BigDecimal statistics(Map params) {
        return systradeTradeDao.statistics(params);
    }

    @Override
    public BigDecimal afterSale(Map params) {
        return systradeTradeDao.afterSale(params);
    }

    List userIdList(String userName) {
//        Map map = new HashMap();
//        map.put("nickName", userName);
//        map.put("mobile", "1");
//        System.out.println(userName);
//        PageUtils page = userService.queryPage(map);
        QueryWrapper<UserEntity> qw = new QueryWrapper();
        if (StringUtils.isNotBlank(userName)) {
            qw.like("nick_name", userName);
        }
        List<UserEntity> userEntityList = userService.list(qw);
        List<Integer> userIds = new ArrayList();
        for (UserEntity user : userEntityList) {
            userIds.add(user.getUserId());
        }
        return userIds;
    }

    @Override
    public void receiveTrade() {
        List<SystradeTradeEntity> list = systradeTradeDao.selectList(new QueryWrapper<SystradeTradeEntity>().eq("status", "3"));
        Integer receive_period = Integer.valueOf(sysConfigDao.getByKey("receive.period"));
        Integer nowtime = DateUtils.currentUnixTime();
        for (SystradeTradeEntity trade : list) {
            if (trade.getConsignTime() + receive_period * 24 * 60 * 60 <= nowtime) {
                logger.info("订单号：" + trade.getTid() + "系统自动确认收货");
                //更新订单状态为已完成
                trade.setStatus("4");
                trade.setModifiedTime(DateUtils.currentUnixTime());
                trade.setEndTime(DateUtils.currentUnixTime());
                this.updateById(trade);
            }
        }
    }
    @Override
    public void closeTrade(){
        //秒杀的单子  关闭不走这个定时器
        List<SystradeTradeEntity> list = systradeTradeDao.selectList(new QueryWrapper<SystradeTradeEntity>().eq("status","1").ne("type",3));
        Integer nowtime = DateUtils.currentUnixTime();
        for(SystradeTradeEntity trade:list){
            if(trade.getCreatedTime() + 30 * 60 <= nowtime) {
                logger.info("订单号：" + trade.getTid() + "系统自动关闭");
                //更新订单状态为已完成
                trade.setStatus("6");
                trade.setRemark("系统自动关闭");
                trade.setModifiedTime(DateUtils.currentUnixTime());
                this.updateById(trade);
            }
        }
    }

}
