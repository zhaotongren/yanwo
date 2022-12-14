package com.yanwo.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.opensearch.sdk.dependencies.com.google.common.collect.Maps;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.Constant.Constants;
import com.yanwo.dao.*;
import com.yanwo.entity.*;
import com.yanwo.service.*;
import com.yanwo.utils.*;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigDecimal;
import java.util.*;


@Service("ectoolsPaymentsService")
public class EctoolsPaymentsServiceImpl extends ServiceImpl<EctoolsPaymentsDao, EctoolsPaymentsEntity> implements EctoolsPaymentsService {
    private static final Logger logger = LoggerFactory.getLogger(EctoolsPaymentsServiceImpl.class);
    @Autowired
    private UserDao userDao;
    @Autowired
    protected RedisUtils redisUtils;
    @Autowired
    private SystradeTradeDao systradeTradeDao;
    @Autowired
    private EctoolsPaymentsDao ectoolsPaymentsDao;
    @Autowired
    private EctoolsTradePaybillDao ectoolsTradePaybillDao;
    @Autowired
    private SystradeOrderDao systradeOrderDao;
    @Autowired
    private SysitemItemDao sysitemItemDao;
    @Autowired
    private SyscapitalCapitalPayDao syscapitalCapitalPayDao;
    @Autowired
    private SyscapitalCapitalDetailDao syscapitalCapitalDetailDao;
    @Autowired
    private SyscapitalCapitalDao syscapitalCapitalDao;
    @Autowired
    private UserGradeDao userGradeDao;
    @Autowired
    private  UserVipDao userVipDao;
    @Autowired
    private SysConfigDao sysConfigDao;
    @Autowired
    private SyscapitalCapitalIntegralDao syscapitalCapitalIntegralDao;
    @Autowired
    private SysitemSkuService sysitemSkuService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserGradeService userGradeService;
    @Autowired
    private SystradeTradeService systradeTradeService;
    @Autowired
    private SystradeOrderService systradeOrderService;
    @Autowired
    private SysDictService sysDictService;

    @Override
    public String paymentPayready(String pay_app_id, String payname, List<String> tids, Integer userId) {
        //??????????????????????????????
        //??????redis???????????????redis???????????????????????????????????????????????????redis???
        logger.info("??????????????????????????????,????????????????????????,????????????="+pay_app_id+",??????????????????="+ StringUtils.join(tids,"-")+",???????????????id="+userId);
        UserEntity user = userDao.selectById(userId);
        String sPaymentId = redisUtils.get(Constants.REDIS_PAYMENT_ID);
        if(StringUtils.isBlank(sPaymentId)){
            redisUtils.set(Constants.REDIS_PAYMENT_ID, DateUtils.currentUnixTime()+"");
        }


        Long paymentId = redisUtils.incr(Constants.REDIS_PAYMENT_ID);
        //???????????????
        BigDecimal totaltrade=systradeTradeDao.sumByTid4portal(tids);
        //??????????????????
        EctoolsPaymentsEntity payments=new EctoolsPaymentsEntity();
        payments.setPayAppId(pay_app_id);
        payments.setPayName(payname);
        payments.setUserId(user.getUserId());
        payments.setUserName(user.getMobile());
        payments.setPaymentId(paymentId.toString());
        payments.setMoney(totaltrade);
        payments.setCurMoney(totaltrade);
        payments.setCreatedTime(DateUtils.currentUnixTime());
        payments.setStatus(Constants.PAY_MENT_STATUS_READY);//?????????
        ectoolsPaymentsDao.insert(payments);
        for (int i=0;i<tids.size();i++){
            SystradeTradeEntity trade=systradeTradeDao.selectById(tids.get(i));
            EctoolsTradePaybillEntity tradePaybill=new EctoolsTradePaybillEntity();
            tradePaybill.setTid(trade.getTid().toString());
            tradePaybill.setStatus(Constants.PAY_MENT_STATUS_READY);
            tradePaybill.setPaymentId(paymentId.toString());
            tradePaybill.setPayment(trade.getPayment().toString());
            tradePaybill.setUserId(user.getUserId().toString());
            tradePaybill.setCreatedTime(DateUtils.currentUnixTime());
            ectoolsTradePaybillDao.insert(tradePaybill);
        }
        return paymentId.toString();
    }
    @Override
    public int paymentPaySyntony(String paymentId, Date gmt_close) {
        try {
            EctoolsPaymentsEntity payments = ectoolsPaymentsDao.selectById(paymentId);
            if(!"succ".equals(payments.getStatus())){
                UserEntity userEntity = userService.getById(payments.getUserId());
                QueryWrapper<EctoolsTradePaybillEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("payment_id",paymentId);
                List<EctoolsTradePaybillEntity> paybills = ectoolsTradePaybillDao.selectList(queryWrapper);
                //????????????????????????????????????
                QueryWrapper<SysDictEntity> qw = new QueryWrapper<>();
                qw.eq("type","sms_notice");
                List<SysDictEntity> dictList = sysDictService.list(qw);
                for(EctoolsTradePaybillEntity paybil:paybills){
                    paybil.setModifiedTime((int) (gmt_close.getTime() / 1000));
                    paybil.setStatus(Constants.PAY_MENT_STATUS_SUSS);
                    paybil.setPayedTime((int) (gmt_close.getTime() / 1000));
                    ectoolsTradePaybillDao.updateById(paybil);
                    //??????????????????????????????
                    SystradeTradeEntity trade = systradeTradeDao.selectById(paybil.getTid());
                    //????????????????????????
                    QueryWrapper<SystradeOrderEntity> queryWrapper1 = new QueryWrapper<>();
                    queryWrapper1.eq("tid",trade.getTid());
                    List<SystradeOrderEntity> orders = systradeOrderDao.selectList(queryWrapper1);
                    for(SystradeOrderEntity order:orders){
                        //????????????
                        order.setStatus("2");
                        systradeOrderDao.updateById(order);
                        //?????????
                        SysitemItemEntity item = sysitemItemDao.selectById(order.getItemId());
                        item.setSoldNum(item.getSoldNum() + order.getNum());
                        sysitemItemDao.updateById(item);
                        //?????????
                        SysitemSkuEntity skuEntity = sysitemSkuService.getById(order.getSkuId());
                        skuEntity.setStore(skuEntity.getStore() - order.getNum());
                        sysitemSkuService.updateById(skuEntity);
                        //??????opensearch??????
                        updateItemCount(item);
                        //????????????
                        if(dictList != null && dictList.size() > 0){
                            String param = "{\"title\":\""+order.getTitle()+"\",\"num\":"+order.getNum()+",\"payment\":"+order.getPayment()+",\"usermobile\":"+trade.getReceiverMobile()+"}";
                            logger.info("????????????????????????????????????"+ param);
                            for(SysDictEntity dict : dictList){
                                SendMessageUtil.Sendnotify(dict.getValue(),param);
                            }
                        }
                    }
                    //????????????????????????????????????
                    updateTradeSuccess(trade,gmt_close);
                    appletSend(Long.valueOf(paybil.getTid()));//???????????????
                    send(paybil.getTid());//???????????????
                }
                //??????????????????
                payments.setPaymentId(paymentId);
                payments.setStatus(Constants.PAY_MENT_STATUS_SUSS);
                payments.setPayedTime((int) (gmt_close.getTime() / 1000));
                ectoolsPaymentsDao.updateById(payments);
            }
            return 1;
        }catch (Exception e){
            logger.info("?????????????????????",e);
            return 0;
        }
    }

    /**
     * ?????? + ??????????????????
     * @param paymentId
     * @param gmt_close
     * @param payType
     * @param payMoney
     * @return
     */
    @Override
    public int paymentPaySyntony(String paymentId, Date gmt_close,String payType ,BigDecimal payMoney) {
        try {
            EctoolsPaymentsEntity payments = ectoolsPaymentsDao.selectById(paymentId);
            if(!"succ".equals(payments.getStatus())){
                //?????????????????? = ?????????????????? - ??????????????????
                BigDecimal rechargeFee = payments.getCurMoney().subtract(payMoney);
                //???????????????????????????????????????
                QueryWrapper<EctoolsTradePaybillEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("payment_id",paymentId);
                List<EctoolsTradePaybillEntity> paybills = ectoolsTradePaybillDao.selectList(queryWrapper);
                //????????????????????????????????????
                QueryWrapper<SysDictEntity> qw = new QueryWrapper<>();
                qw.eq("type","sms_notice");
                List<SysDictEntity> dictList = sysDictService.list(qw);

                BigDecimal disTotal = BigDecimal.ZERO;
                for(int i = 0;i<paybills.size();i++){
                    EctoolsTradePaybillEntity paybil = paybills.get(i);
                    paybil.setModifiedTime((int) (gmt_close.getTime() / 1000));
                    paybil.setStatus(Constants.PAY_MENT_STATUS_SUSS);
                    paybil.setPayedTime((int) (gmt_close.getTime() / 1000));
                    ectoolsTradePaybillDao.updateById(paybil);

                    SystradeTradeEntity trade = systradeTradeDao.selectById(paybil.getTid());
                    //???????????????????????????????????????
                    BigDecimal disMoney = BigDecimal.ZERO;
                    if(i == paybills.size() - 1){
                        //??????????????????
                        disMoney = rechargeFee.subtract(disTotal);
                    }else {
                        disMoney = trade.getPayment().divide(payments.getCurMoney()).multiply(rechargeFee).setScale(2,BigDecimal.ROUND_UP);
                        disTotal = disTotal.add(disMoney);
                    }
                    trade.setRechargeFee(disMoney);
                    //??????order
                    QueryWrapper<SystradeOrderEntity> queryWrapper1 = new QueryWrapper<>();
                    queryWrapper1.eq("tid",trade.getTid());
                    List<SystradeOrderEntity> orders = systradeOrderDao.selectList(queryWrapper1);
                    for(int j=0;j<orders.size();j++) {
                        SystradeOrderEntity order = orders.get(j);
                        //disMoney > ??????
                        if(disMoney.compareTo(trade.getPostFee()) == 1){
                            //????????????   disMoney -trade.getPostFee() ??????????????????
                            BigDecimal difMoney = disMoney.subtract(trade.getPostFee());
                            BigDecimal discountTotal = BigDecimal.ZERO;
                            BigDecimal discountMoney = BigDecimal.ZERO;
                            if(j == orders.size() - 1){
                                //?????????????????????
                                discountMoney = difMoney.subtract(discountTotal);
                            }else {
                                discountMoney = order.getPayment().divide(trade.getPayment().subtract(trade.getPostFee())).multiply(difMoney);
                                discountTotal = discountTotal.add(discountMoney);
                            }
                            order.setRechargeFee(discountMoney);
                        }
                        order.setStatus("2");
                        systradeOrderDao.updateById(order);
                        //?????????
                        SysitemItemEntity item = sysitemItemDao.selectById(order.getItemId());
                        item.setSoldNum(item.getSoldNum() + order.getNum());
                        sysitemItemDao.updateById(item);
                        //?????????
                        SysitemSkuEntity skuEntity = sysitemSkuService.getById(order.getSkuId());
                        skuEntity.setStore(skuEntity.getStore() - order.getNum());
                        sysitemSkuService.updateById(skuEntity);
                        //??????opensearch??????
                        updateItemCount(item);
                        //????????????
                        if(dictList != null && dictList.size() > 0){
                            String param = "{\"title\":\""+order.getTitle()+"\",\"num\":"+order.getNum()+",\"payment\":"+order.getPayment()+",\"usermobile\":"+trade.getReceiverMobile()+"}";
                            logger.info("????????????????????????????????????"+ param);
                            for(SysDictEntity dict : dictList){
                                SendMessageUtil.Sendnotify(dict.getValue(),param);
                            }
                        }
                    }
                    //????????????????????????????????????
                    updateTradeSuccess(trade,gmt_close);
                    appletSend(Long.valueOf(paybil.getTid()));//???????????????
                    send(paybil.getTid());//???????????????
                }
                //??????????????????
                payments.setPaymentId(paymentId);
                payments.setStatus(Constants.PAY_MENT_STATUS_SUSS);
                payments.setPayedTime((int) (gmt_close.getTime() / 1000));
                ectoolsPaymentsDao.updateById(payments);
                //??????????????????
                QueryWrapper<SyscapitalCapitalEntity> capitalQW = new QueryWrapper<>();
                capitalQW.eq("user_id",payments.getUserId());
                SyscapitalCapitalEntity capital = syscapitalCapitalDao.selectOne(capitalQW);
                //????????????
                SyscapitalCapitalDetailEntity capitalDetail = new SyscapitalCapitalDetailEntity();
                capitalDetail.setCapitalId(capital.getCapitalId());
                capitalDetail.setCapitalType(8);
                capitalDetail.setUserId(capital.getUserId());
                capitalDetail.setCapitalFee(rechargeFee.multiply(new BigDecimal(-1)));
                capitalDetail.setCreatedTime(DateUtils.currentUnixTime());
                syscapitalCapitalDetailDao.insert(capitalDetail);
                //??????????????????
                capital.setTotalRecharge(capital.getTotalRecharge().subtract(rechargeFee));
                capital.setTotalCapital(capital.getTotalCapital().subtract(rechargeFee));
                syscapitalCapitalDao.updateById(capital);
            }
            return 1;
        }catch (Exception e){
            logger.info("?????????????????????",e);
            return 0;
        }
    }
    //???????????????????????????
    private void updateTradeSuccess(SystradeTradeEntity trade,Date gmt_close){
        //type:0????????????   1??????   2?????????   3??????
        if(trade.getType() != 1 && trade.getType() != 3){
            //??????????????????
            String two_rebate = sysConfigDao.getByKey("two_rebate");//????????????
            String throw_rebate = sysConfigDao.getByKey("throw_rebate");//????????????
            String leader_rebate = sysConfigDao.getByKey("leader_rebate");//????????????
            /*??????????????????*/
            List<Map> ratelist = new ArrayList();
            UserGradeEntity userGrade = userGradeDao.selectById(trade.getUserId());
            if (userGrade.getTwoUserId() != null) {//??????????????????userid
                UserEntity userParTwo = userDao.selectById(userGrade.getTwoUserId());
                //???????????????
                if (userParTwo != null) {
                    Map map = new HashMap();
                    map.put("userId", userGrade.getTwoUserId());
                    map.put("rate", two_rebate);
                    map.put("type", 1);//1???????????????
                    ratelist.add(map);
                    //???????????????
                    if (userGrade.getFirstUserId() != null) {
                        UserEntity userParOne = userDao.selectById(userGrade.getFirstUserId());
                        if (userParOne != null) {
                            Map map_ = new HashMap();
                            map_.put("userId", userGrade.getFirstUserId());
                            map_.put("rate", throw_rebate);
                            map_.put("type", 2);//2???????????????
                            ratelist.add(map_);
                        }
                    }
                }
            }
            //??????????????????????????????
            UserEntity userEntity = userService.getById(trade.getUserId());
            if (userEntity != null && userEntity.getColonel() == 1) {
                Map map = new HashMap();
                map.put("userId", userEntity.getUserId());
                map.put("rate", leader_rebate);
                map.put("type", 3);//3???????????????
                ratelist.add(map);
            }
            trade.setRateParam(JsonUtils.objectToJson(ratelist));
        }else if(trade.getType() == 3){
            //????????????????????????  ???????????????????????????????????????redis??????
            redisUtils.delete("seckill_order_"+trade.getTid());
        }
        //????????????
        trade.setStatus("2");
        trade.setPayTime((int) (gmt_close.getTime() / 1000));
        systradeTradeDao.updateById(trade);
    }
    public void updateItemCount(SysitemItemEntity item){
        if(item.getApproveStatus() != 2){
            return;
        }
        Map<String, Object> doc = Maps.newLinkedHashMap();
        doc.put("item_id", item.getItemId());
        doc.put("cat_id", item.getCatId());
        doc.put("title", item.getTitle());
        doc.put("sub_title", item.getSubTitle());
        doc.put("image_default", item.getImageDefault());
        doc.put("max_price", item.getMaxPrice());
        doc.put("min_price", item.getMinPrice());
        doc.put("sold_num", item.getSoldNum()==null?0:item.getSoldNum());
        doc.put("list_time", DateUtils.currentUnixTime());
        doc.put("grade", item.getGrade()==null?0:item.getGrade());
        doc.put("item_type",item.getItemType());
        doc.put("total_sales",item.getSoldNum() + item.getCustomSales());

        if(OpenSearchUtil.add(doc)){
            logger.info("??????"+item.getItemId()+"??????????????????????????????");
        }
        else{
            logger.info("??????"+item.getItemId()+"??????????????????????????????");
        }
    }
    @Override
    public int noPayTrade(String paymode, String paymentId, Date gmt_close) {
        try {
            EctoolsPaymentsEntity payments = ectoolsPaymentsDao.selectById(paymentId);
            if(!"succ".equals(payments.getStatus())){
                UserEntity userEntity = userService.getById(payments.getUserId());
                QueryWrapper<EctoolsTradePaybillEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("payment_id",paymentId);
                List<EctoolsTradePaybillEntity> paybills = ectoolsTradePaybillDao.selectList(queryWrapper);
                //????????????????????????????????????
                QueryWrapper<SysDictEntity> qw = new QueryWrapper<>();
                qw.eq("type","sms_notice");
                List<SysDictEntity> dictList = sysDictService.list(qw);
                for(EctoolsTradePaybillEntity paybil:paybills){
                    paybil.setModifiedTime((int) (gmt_close.getTime() / 1000));
                    paybil.setStatus(Constants.PAY_MENT_STATUS_SUSS);
                    paybil.setPayedTime((int) (gmt_close.getTime() / 1000));
                    ectoolsTradePaybillDao.updateById(paybil);
                    //??????????????????????????????
                    SystradeTradeEntity trade = systradeTradeDao.selectById(paybil.getTid());
                    //????????????????????????
                    QueryWrapper<SystradeOrderEntity> queryWrapper1 = new QueryWrapper<>();
                    queryWrapper1.eq("tid",trade.getTid());
                    List<SystradeOrderEntity> orders = systradeOrderDao.selectList(queryWrapper1);
                    for(SystradeOrderEntity order:orders){
                        //?????????
                        SysitemItemEntity item = sysitemItemDao.selectById(order.getItemId());
                        item.setSoldNum(item.getSoldNum() + order.getNum());
                        sysitemItemDao.updateById(item);
                        //?????????
                        SysitemSkuEntity skuEntity = sysitemSkuService.getById(order.getSkuId());
                        skuEntity.setStore(skuEntity.getStore() - order.getNum());
                        sysitemSkuService.updateById(skuEntity);
                        //??????opensearch??????
                        updateItemCount(item);
                        //????????????
                        if (dictList != null && dictList.size() > 0){
                            for(SysDictEntity dict : dictList){
                                SendMessageUtil.SendnotifyIntegral(dict.getValue());
                            }
                        }
                    }
                    //????????????
                    pointPaySuccess(trade);
                    //????????????????????????????????????
                    updateTradeSuccess(trade,gmt_close);
                }
                //??????????????????
                payments.setPaymentId(paymentId);
                payments.setStatus(Constants.PAY_MENT_STATUS_SUSS);
                payments.setPayedTime((int) (gmt_close.getTime() / 1000));
                ectoolsPaymentsDao.updateById(payments);
            }
            return 1;
        }catch (Exception e){
            logger.info("?????????????????????",e);
            return 0;
        }
    }
    //????????????????????????
    public void pointPaySuccess(SystradeTradeEntity trade) {
        QueryWrapper<SyscapitalCapitalEntity> qw = new QueryWrapper();
        qw.eq("user_id",trade.getUserId());
        SyscapitalCapitalEntity capitalEntity = syscapitalCapitalDao.selectOne(qw);
        if(capitalEntity != null){
            //????????????????????????
            SyscapitalCapitalIntegralEntity integralEntity = new SyscapitalCapitalIntegralEntity();
            integralEntity.setIntegralFee(trade.getPayment());
            integralEntity.setIntegralType(2);
            integralEntity.setCapitalId(capitalEntity.getCapitalId());
            integralEntity.setUserId(capitalEntity.getUserId());
            integralEntity.setTid(trade.getTid());
            integralEntity.setCreatedTime(DateUtils.currentUnixTime());
            integralEntity.setStatus(1);
            syscapitalCapitalIntegralDao.insert(integralEntity);
            //???????????????
            capitalEntity.setTotalIntegral(capitalEntity.getTotalIntegral().subtract(trade.getPayment()));
            syscapitalCapitalDao.updateById(capitalEntity);
        }
    }

    public String getAppletAccessToken(){
        String accessToken = redisUtils.get(Constants.APPLETACCESSTOKEN);
        if(StringUtils.isBlank(accessToken)){
            String message = HttpUtils.sendGet("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
                    + "wx53e0ce07db66b556" + "&secret=" + "c1e9b2c79cbc0a7e69f2fe363440d20b");
            JSONObject demoJson =JSONObject.fromObject(message);
            accessToken = demoJson.getString("access_token");
            String expiresIn = demoJson.getString("expires_in");
            redisUtils.set(Constants.APPLETACCESSTOKEN,accessToken,Integer.valueOf(expiresIn));
        }else{

        }
        return accessToken;
    }

    public String getAccessToken(){
        String accessToken = redisUtils.get(Constants.ACCESSTOKEN);
        if(org.apache.commons.lang.StringUtils.isBlank(accessToken)){
            String message = HttpUtils.sendGet("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
                    + "wx7a855d5b2991ca2e" + "&secret=" + "f092b15b5df2cc45f5c545f4fd35c655");
            System.out.println(message);
            JSONObject demoJson =JSONObject.fromObject(message);
            accessToken = demoJson.getString("access_token");
            String expiresIn = demoJson.getString("expires_in");
            redisUtils.set(Constants.ACCESSTOKEN,accessToken,Integer.valueOf(expiresIn));
        }else{

        }
        System.out.println(accessToken);
        return accessToken;
    }


    /**
     * ??????????????????????????????
     * @param tid
     * @return
     */
    public String appletSend(Long tid) {
        try {
            SystradeTradeEntity trade = systradeTradeService.getById(tid);
            String accessToken =getAppletAccessToken();

            System.out.println(accessToken);
            String postUrl = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + accessToken;

            QueryWrapper<SystradeOrderEntity> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("tid",tid);
            List<SystradeOrderEntity> list = systradeOrderService.list(queryWrapper);

            UserEntity userEntity = userDao.selectById(trade.getUserId());

            for(SystradeOrderEntity order:list){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("touser", userEntity.getWxOpenId());   // openid
                jsonObject.put("template_id", "Qf5NG9CnDQTjCrb4OoYp_oggrWbZ6iHpyFpk_yBCTUo");
                jsonObject.put("page", "/pages/mine/all_order/all_order");

                JSONObject data = new JSONObject();

                JSONObject keyword1 = new JSONObject();
                keyword1.put("value", order.getTitle());

                JSONObject keyword2 = new JSONObject();
                keyword2.put("value", "??"+order.getPayment());

                JSONObject keyword3 = new JSONObject();
                keyword3.put("value", order.getTid());

                /* --------------------------------------------------------------------- */
                JSONObject keyword4 = new JSONObject();
                keyword4.put("value", GUtils.IntegerToDate(trade.getPayTime()));
                /* --------------------------------------------------------------------- */

                JSONObject keyword5 = new JSONObject();
                keyword5.put("value", trade.getRemark()!=null?trade.getRemark():"???");


                data.put("thing1", keyword1);
                data.put("amount7", keyword2);
                data.put("character_string6", keyword3);
                data.put("time4", keyword4);
                data.put("thing5", keyword5);


                jsonObject.put("data", data);

                System.out.println(jsonObject.toString());

                String string = HttpUtils.sendPost(postUrl, jsonObject.toString());

                com.alibaba.fastjson.JSONObject result = JSON.parseObject(string);
                int errcode = result.getIntValue("errcode");
                if (errcode == 0) {
                    // ????????????
                    System.out.println("????????????");
                    appletDirect(tid);//???????????????
                } else {
                    // ????????????
                    System.out.println("????????????");
                    System.out.println(string);
                    System.out.println(result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return "";
    }

    public String appletDirect(Long tid){
        try {
            SystradeTradeEntity trade = systradeTradeService.getById(tid);

            if(trade.getType()==3){
                return "";
            }

            String accessToken =getAppletAccessToken();

            System.out.println(accessToken);
            String postUrl = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + accessToken;

            String two_rebate = sysConfigDao.getByKey("two_rebate");//????????????

            QueryWrapper<UserGradeEntity> wrapper=new QueryWrapper<>();
            wrapper.eq("user_id",trade.getUserId());
            UserGradeEntity userGradeEntity = userGradeService.getOne(wrapper);


            QueryWrapper<SystradeOrderEntity> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("tid",tid);
            List<SystradeOrderEntity> list = systradeOrderService.list(queryWrapper);

            for(SystradeOrderEntity order:list){
                if(null!=userGradeEntity.getParIdTwo()){
                    UserEntity user = userService.getById(userGradeEntity.getParIdTwo());
                    if(null!=user){
                        BigDecimal multiply = order.getPayment().multiply(new BigDecimal(two_rebate));

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("touser", user.getWxOpenId());   // openid
                        jsonObject.put("template_id", "QFHbulPNKPfmIM-PjLObE3X1wMbfNqkZlF29q27923o");
                        jsonObject.put("page", "pages/home/index/index");

                        JSONObject data = new JSONObject();

                        JSONObject keyword1 = new JSONObject();
                        keyword1.put("value", "???????????????????????????????????????");

                        JSONObject keyword2 = new JSONObject();
                        keyword2.put("value", "??"+multiply);

                        JSONObject keyword3 = new JSONObject();
                        keyword3.put("value", "?????????????????????????????????");

                        JSONObject keyword4 = new JSONObject();
                        keyword4.put("value", "?????????????????????");


                        data.put("thing1", keyword1);
                        data.put("amount2", keyword2);
                        data.put("thing3", keyword3);
                        data.put("thing4", keyword4);


                        jsonObject.put("data", data);

                        System.out.println(jsonObject.toString());

                        String string = HttpUtils.sendPost(postUrl, jsonObject.toString());

                        com.alibaba.fastjson.JSONObject result = JSON.parseObject(string);
                        int errcode = result.getIntValue("errcode");
                        if (errcode == 0) {
                            // ????????????
                            System.out.println("????????????");
                            appletIndirect(tid);//???????????????
                        } else {
                            // ????????????
                            System.out.println("????????????");
                            System.out.println(string);
                            System.out.println(result);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return "";
    }


    public String appletIndirect(Long tid){
        try {
            SystradeTradeEntity trade = systradeTradeService.getById(tid);
            String accessToken =getAppletAccessToken();

            System.out.println(accessToken);
            String postUrl = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + accessToken;

            String throw_rebate = sysConfigDao.getByKey("throw_rebate");//????????????

            QueryWrapper<UserGradeEntity> wrapper=new QueryWrapper<>();
            wrapper.eq("user_id",trade.getUserId());
            UserGradeEntity userGradeEntity = userGradeService.getOne(wrapper);


            QueryWrapper<SystradeOrderEntity> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("tid",tid);
            List<SystradeOrderEntity> list = systradeOrderService.list(queryWrapper);

            for(SystradeOrderEntity order:list){
                if(null!=userGradeEntity.getParIdOne()){
                    UserEntity user = userService.getById(userGradeEntity.getParIdOne());
                    if(null!=user){

                        BigDecimal multiply = order.getPayment().multiply(new BigDecimal(throw_rebate));

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("touser", user.getWxOpenId());   // openid
                        jsonObject.put("template_id", "QFHbulPNKPfmIM-PjLObE3X1wMbfNqkZlF29q27923o");
                        jsonObject.put("page", "pages/home/index/index");

                        JSONObject data = new JSONObject();

                        JSONObject keyword1 = new JSONObject();
                        keyword1.put("value", "???????????????????????????????????????");

                        JSONObject keyword2 = new JSONObject();
                        keyword2.put("value", "??"+multiply);

                        JSONObject keyword3 = new JSONObject();
                        keyword3.put("value", "?????????????????????????????????");

                        JSONObject keyword4 = new JSONObject();
                        keyword4.put("value", "?????????????????????");


                        data.put("thing1", keyword1);
                        data.put("amount2", keyword2);
                        data.put("thing3", keyword3);
                        data.put("thing4", keyword4);


                        jsonObject.put("data", data);

                        System.out.println(jsonObject.toString());

                        String string = HttpUtils.sendPost(postUrl, jsonObject.toString());

                        com.alibaba.fastjson.JSONObject result = JSON.parseObject(string);
                        int errcode = result.getIntValue("errcode");
                        if (errcode == 0) {
                            // ????????????
                            System.out.println("????????????");
                        } else {
                            // ????????????
                            System.out.println("????????????");
                            System.out.println(string);
                            System.out.println(result);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return "";
    }


    public void send(String tid) {
        try {
            String accessToken = getAccessToken();

            SystradeTradeEntity trade = systradeTradeService.getById(tid);

            System.out.println(accessToken);
            String postUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;

            QueryWrapper<UserGradeEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", trade.getUserId());
            UserGradeEntity userGradeEntity = userGradeService.getOne(wrapper);


            QueryWrapper<SystradeOrderEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("tid", tid);
            List<SystradeOrderEntity> list = systradeOrderService.list(queryWrapper);

            for (SystradeOrderEntity order : list) {
                UserEntity user = userService.getById(order.getUserId());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("touser", user.getPublicOpenId());   // openid
                jsonObject.put("template_id", "OwHPprYq4EFrgeFnNp1iHzWNDAY6CevhBwiekDKOJbo");
                jsonObject.put("url", "/pages/mine/all_order/all_order");

                JSONObject data = new JSONObject();
                JSONObject first = new JSONObject();
                first.put("value", "??????????????????????????????????????????????????????????????????");
                first.put("color", "#173177");
                JSONObject keyword1 = new JSONObject();
                keyword1.put("value", order.getTitle());
                keyword1.put("color", "#173177");
                JSONObject keyword2 = new JSONObject();
                keyword2.put("value", order.getTid());
                keyword2.put("color", "#173177");
                JSONObject keyword3 = new JSONObject();
                keyword3.put("value", order.getPayment());
                keyword3.put("color", "#173177");
                JSONObject keyword4 = new JSONObject();
                keyword4.put("value", trade.getPayTime());
                keyword4.put("color", "#173177");
                JSONObject keyword5 = new JSONObject();
                keyword5.put("value", trade.getReceiverName()+" "+trade.getReceiverMobile()+" "+trade.getReceiverAddress());
                keyword5.put("color", "#173177");

                JSONObject remark = new JSONObject();
                remark.put("value", trade.getRemark());
                remark.put("color", "#173177");


                data.put("first", first);
                data.put("keyword1", keyword1);
                data.put("keyword2", keyword2);
                data.put("keyword3", keyword3);
                data.put("keyword4", keyword4);
                data.put("keyword5", keyword5);
                data.put("remark", remark);

                jsonObject.put("data", data);

                System.out.println(jsonObject.toString());

                String string = HttpUtils.sendPost(postUrl, jsonObject.toString());

                com.alibaba.fastjson.JSONObject result = JSON.parseObject(string);
                int errcode = result.getIntValue("errcode");
                if (errcode == 0) {
                    // ????????????
                    System.out.println("????????????");
                    direct(tid);
                } else {
                    // ????????????
                    System.out.println("????????????");
                    System.out.println(string);
                    System.out.println(errcode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String direct(String tid){
        try {
            SystradeTradeEntity trade = systradeTradeService.getById(tid);
            if(trade.getType()==3){
                return "";
            }

            String accessToken =getAccessToken();

            System.out.println(accessToken);
            String postUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;

            String two_rebate = sysConfigDao.getByKey("two_rebate");//????????????

            QueryWrapper<UserGradeEntity> wrapper=new QueryWrapper<>();
            wrapper.eq("user_id",trade.getUserId());
            UserGradeEntity userGradeEntity = userGradeService.getOne(wrapper);


            QueryWrapper<SystradeOrderEntity> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("tid",tid);
            List<SystradeOrderEntity> list = systradeOrderService.list(queryWrapper);

            for(SystradeOrderEntity order:list){
                if(null!=userGradeEntity.getParIdTwo()){
                    UserEntity user = userService.getById(userGradeEntity.getParIdTwo());
                    if(null!=user){
                        BigDecimal multiply = order.getPayment().multiply(new BigDecimal(two_rebate));

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("touser", user.getPublicOpenId());   // openid
                        jsonObject.put("template_id", "gUvnljTB6MJ8StH3Z5-UOgJzH_eTbXZ69NMjSpB-omg");
                        jsonObject.put("url", "pages/home/index/index");

                        JSONObject data = new JSONObject();
                        JSONObject first = new JSONObject();
                        first.put("value", "??????????????????????????????");
                        first.put("color", "#173177");
                        JSONObject keyword1 = new JSONObject();
                        keyword1.put("value", order.getTid());
                        keyword1.put("color", "#173177");
                        JSONObject keyword2 = new JSONObject();
                        keyword2.put("value", "??????????????????");
                        keyword2.put("color", "#173177");
                        JSONObject keyword3 = new JSONObject();
                        keyword3.put("value", trade.getPayTime());
                        keyword3.put("color", "#173177");
                        JSONObject keyword4 = new JSONObject();
                        keyword4.put("value", multiply);
                        keyword4.put("color", "#173177");
                        JSONObject keyword5 = new JSONObject();
                        keyword5.put("value", "????????????");
                        keyword5.put("color", "#173177");

                        JSONObject remark = new JSONObject();
                        remark.put("value", trade);
                        remark.put("color", "#173177");


                        data.put("first", first);
                        data.put("keyword1", keyword1);
                        data.put("keyword2", keyword2);
                        data.put("keyword3", keyword3);
                        data.put("keyword4", keyword4);
                        data.put("keyword5", keyword5);
                        data.put("remark", remark);

                        jsonObject.put("data", data);

                        System.out.println(jsonObject.toString());

                        String string = HttpUtils.sendPost(postUrl, jsonObject.toString());

                        com.alibaba.fastjson.JSONObject result = JSON.parseObject(string);
                        int errcode = result.getIntValue("errcode");
                        if (errcode == 0) {
                            // ????????????
                            System.out.println("????????????");
                            indirect(tid);
                        } else {
                            // ????????????
                            System.out.println("????????????");
                            System.out.println(string);
                            System.out.println(result);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return "";
    }

    public String indirect(String tid){
        try {
            SystradeTradeEntity trade = systradeTradeService.getById(tid);
            String accessToken =getAppletAccessToken();

            System.out.println(accessToken);
            String postUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;

            String throw_rebate = sysConfigDao.getByKey("throw_rebate");//????????????

            QueryWrapper<UserGradeEntity> wrapper=new QueryWrapper<>();
            wrapper.eq("user_id",trade.getUserId());
            UserGradeEntity userGradeEntity = userGradeService.getOne(wrapper);


            QueryWrapper<SystradeOrderEntity> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("tid",tid);
            List<SystradeOrderEntity> list = systradeOrderService.list(queryWrapper);

            for(SystradeOrderEntity order:list){
                if(null!=userGradeEntity.getParIdOne()){
                    UserEntity user = userService.getById(userGradeEntity.getParIdOne());
                    if(null!=user){
                        BigDecimal multiply = order.getPayment().multiply(new BigDecimal(throw_rebate));

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("touser", user.getPublicOpenId());   // openid
                        jsonObject.put("template_id", "PY2L16aDJIhKvjVQDHhaCGOQu6J_FkVKkPuOzXhzJrU");
                        jsonObject.put("url", "pages/home/index/index");

                        JSONObject data = new JSONObject();
                        JSONObject first = new JSONObject();
                        first.put("value", "??????????????????????????????");
                        first.put("color", "#173177");
                        JSONObject keyword1 = new JSONObject();
                        keyword1.put("value", order.getTid());
                        keyword1.put("color", "#173177");
                        JSONObject keyword2 = new JSONObject();
                        keyword2.put("value", "??????????????????");
                        keyword2.put("color", "#173177");
                        JSONObject keyword3 = new JSONObject();
                        keyword3.put("value", trade.getPayTime());
                        keyword3.put("color", "#173177");
                        JSONObject keyword4 = new JSONObject();
                        keyword4.put("value", multiply);
                        keyword4.put("color", "#173177");
                        JSONObject keyword5 = new JSONObject();
                        keyword5.put("value", "????????????");
                        keyword5.put("color", "#173177");

                        JSONObject remark = new JSONObject();
                        remark.put("value", trade);
                        remark.put("color", "#173177");


                        data.put("first", first);
                        data.put("keyword1", keyword1);
                        data.put("keyword2", keyword2);
                        data.put("keyword3", keyword3);
                        data.put("keyword4", keyword4);
                        data.put("keyword5", keyword5);
                        data.put("remark", remark);

                        jsonObject.put("data", data);


                        System.out.println(jsonObject.toString());

                        String string = HttpUtils.sendPost(postUrl, jsonObject.toString());

                        com.alibaba.fastjson.JSONObject result = JSON.parseObject(string);
                        int errcode = result.getIntValue("errcode");
                        if (errcode == 0) {
                            // ????????????
                            System.out.println("????????????");
                        } else {
                            // ????????????
                            System.out.println("????????????");
                            System.out.println(string);
                            System.out.println(result);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return "";
    }


}
