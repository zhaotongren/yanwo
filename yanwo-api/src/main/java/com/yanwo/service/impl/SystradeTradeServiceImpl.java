package com.yanwo.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.Constant.Constants;
import com.yanwo.dao.*;
import com.yanwo.entity.*;
import com.yanwo.redis.MiaoshaKey;
import com.yanwo.redis.OrderKey;
import com.yanwo.redis.RedisService;
import com.yanwo.service.*;
import com.yanwo.utils.*;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;


@Service("systradeTradeService")
public class SystradeTradeServiceImpl extends ServiceImpl<SystradeTradeDao, SystradeTradeEntity> implements SystradeTradeService {
    private static final Logger logger = LoggerFactory.getLogger(SystradeTradeServiceImpl.class);
    @Autowired
    protected RedisUtils redisUtils;
    @Autowired
    SysitemItemDao sysitemItemDao;
    @Autowired
    SystradeTradeDao systradeTradeDao;
    @Autowired
    SystradeOrderDao systradeOrderDao;
    @Autowired
    SysuserUserAddrsDao sysuserUserAddrsDao;
    @Autowired
    SysitemSkuDao sysitemSkuDao;
    @Autowired
    SysFreightService sysFreightService;
    @Autowired
    private SysuserShoppingCartService shoppingCartService;
    @Autowired
    private SystradeOrderService systradeOrderService;
    @Autowired
    private SysDictService sysDictService;
    @Autowired
    SysaftersalesRefundsDao sysaftersalesRefundsDao;
    @Autowired
    private UserService userService;
    @Autowired
    SysactivityCouponService sysactivityCouponService;
    @Autowired
    private EctoolsPaymentsService ectoolsPaymentsService;
    @Autowired
    private RedisService redisService;
    @Autowired
    SysuserCouponService sysuserCouponService;

    @Override
    @Transactional
    public MiaoshaOrderEntity createOrder(UserEntity user, SysitemSeckillEntity sysitemSeckill, Integer addrId,Integer randomNumber){
        try {
            List<String> tradeIds = new ArrayList<String>();
            //????????????
            SysuserUserAddrsEntity Addrs = sysuserUserAddrsDao.selectById(addrId);
            //?????????
            SystradeTradeEntity trade = new SystradeTradeEntity();
            /*tid?????????????????????????????????+???????????????*/
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            String time = df.format(new Date());
            String tid = time + BnUtils.getRandomNum(4);
            trade.setTid(tid);
            trade.setUserId(user.getUserId());
            trade.setAddrId(addrId);
            trade.setStatus("1");
            trade.setReceiverName(Addrs.getName());
            String area0 = "";
            if (StringUtils.isNotBlank(Addrs.getArea())) {
                area0 = Addrs.getArea().split(":")[0].replaceAll("/", "");
            }
            trade.setReceiverAddress(area0 + " " + Addrs.getAddr());
            trade.setReceiverMobile(Addrs.getMobile());
            trade.setCreatedTime(DateUtils.currentUnixTime());
            trade.setType(3);//0:???????????? 1???????????? 2????????? 3??????
            //????????????
            String areaCode = Addrs.getArea().split(":")[1];
            String provinceCode = areaCode.split("/")[0];
            String cityCode = areaCode.split("/")[1];
            SysitemItemEntity item = sysitemItemDao.selectById(sysitemSeckill.getItemId());
            Map postmap = sysFreightService.postFee(item.getFreightId(), provinceCode, cityCode);
            if (postmap == null) {
                logger.info("?????????????????????????????????FreightId=" + item.getFreightId() + ";Addrs.provinceCode=" + provinceCode + ";Addrs.cityCode=" + cityCode + "--------??????");
            } else {
                BigDecimal postFee = new BigDecimal(postmap.get("postFee").toString());
                trade.setTotalFee(sysitemSeckill.getSeckillPrice());
                trade.setPayment(sysitemSeckill.getSeckillPrice().add(postFee).setScale(2, BigDecimal.ROUND_HALF_UP));
                trade.setPostFee(postFee);
                trade.setSeckillId(sysitemSeckill.getId());
                systradeTradeDao.insert(trade);
                //?????????????????????????????????????????????????????????????????????????????????????????????
                SysitemSkuEntity sku = sysitemSkuDao.selectById(sysitemSeckill.getSkuId());
                SystradeOrderEntity order = new SystradeOrderEntity();
                order.setTid(trade.getTid());
                order.setUserId(user.getUserId());
                order.setItemId(item.getItemId());
                order.setNum(1);
                order.setPrice(sysitemSeckill.getSeckillPrice());//?????????
                order.setCostPrice(sku.getCostPrice());
                order.setTotalFee(sysitemSeckill.getSeckillPrice());
                order.setPayment(sysitemSeckill.getSeckillPrice());
                order.setTitle(item.getTitle());
                order.setPicPath(sku.getImg());
                order.setSkuId(sku.getSkuId());
                order.setSpecInfo(sku.getTitle());
                order.setStatus("1");
                order.setSeckillId(sysitemSeckill.getId());
                systradeOrderDao.insert(order);
                //?????????????????????
                tradeIds.add(trade.getTid());
                String paymentId = ectoolsPaymentsService.paymentPayready(Constants.TRADE_PAY_ID_WE_CHAT, Constants.TRADE_PAY_NAME_WE_CHAT, tradeIds, user.getUserId());
                MiaoshaOrderEntity miaoshaOrder = new MiaoshaOrderEntity();
                miaoshaOrder.setTid(trade.getTid());
                miaoshaOrder.setItemId(item.getItemId());
                miaoshaOrder.setSkuId(sku.getSkuId());
                miaoshaOrder.setSeckillId(sysitemSeckill.getId());
                miaoshaOrder.setUserId(user.getUserId());
                miaoshaOrder.setPaymentId(paymentId);
                redisService.set(OrderKey.getMiaoshaOrderByUidGid, "" + user.getUserId() + "_" + sysitemSeckill.getId() + "_" + randomNumber, miaoshaOrder);
                //????????????????????????  ?????????????????????
                redisUtils.set("seckill_order_" + trade.getTid(), miaoshaOrder, 180);
                return miaoshaOrder;
            }
        }catch (Exception e){
            logger.info("????????????????????????",e);
            MiaoshaOrderEntity miaoshaOrder = new MiaoshaOrderEntity();
            miaoshaOrder.setSeckillId(sysitemSeckill.getId());
            miaoshaOrder.setUserId(user.getUserId());
            redisService.set(OrderKey.getMiaoshaOrderByUidGid, "" + user.getUserId() + "_" + sysitemSeckill.getId() + "_" + randomNumber, miaoshaOrder);
        }
        return null;
    }


    @Override
    public List<String> saveTradeAndSubOrder(Integer userId, String addressId,String[] buyerMessages,String type,SysactivityCouponEntity coupon){
        Integer nowTime = Math.toIntExact(System.currentTimeMillis() / 1000);
        List<String> tradeIds = new ArrayList<String>();
        //????????????
        SysuserUserAddrsEntity Addrs = sysuserUserAddrsDao.selectById(Integer.valueOf(addressId));
        //??????redis?????????????????????
        String redisOrderJson = redisUtils.get(Constants.REDIS_ORDER_PORTAL + ":" + userId);
        logger.info("??????:"+userId+",??????????????????:"+redisOrderJson+"...");
        List<SysuserShoppingCartEntity> l = JsonUtils.jsonToList(redisOrderJson, SysuserShoppingCartEntity.class);
        //???????????????
        BigDecimal amount = BigDecimal.ZERO;
        //???????????????
        BigDecimal couponMoney = coupon==null?BigDecimal.ZERO:coupon.getMoney();
        int itemNum = 0;

        //????????????????????????
        Map<Integer, List> map = new HashMap<Integer, List>();
        for (int i = 0; i < l.size(); i++) {
            SysuserShoppingCartEntity c = l.get(i);
            SysitemItemEntity item = sysitemItemDao.selectById(c.getItemId());
            SysitemSkuEntity sku = sysitemSkuDao.selectById(c.getSkuId());
            amount = amount.add(sku.getPrice().multiply(new BigDecimal(c.getQuantity())));
            Integer freightId = item.getFreightId();
            List cartList4Shop = map.get(freightId);//??????map?????????list??????????????????????????????????????????
            if (cartList4Shop == null) {
                cartList4Shop = new LinkedList();
                map.put(freightId, cartList4Shop);
            }
            itemNum++;
            //??????????????????????????????list???
            cartList4Shop.add(c);
        }
        Set<Map.Entry<Integer, List>> set = map.entrySet();
        Map<String, List> dlytmplmap = new HashMap<String, List>();
        int d = 0;//?????????
        BigDecimal deductionNum = BigDecimal.ZERO;
        for (Map.Entry<Integer, List> entry : set) {
            Integer freightId = entry.getKey();
            List<SysuserShoppingCartEntity> list = entry.getValue();
            //?????????
            SystradeTradeEntity trade = new SystradeTradeEntity();
            /*tid?????????????????????????????????+???????????????*/
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            String time = df.format(new Date());
            String tid = time + BnUtils.getRandomNum(4);
            trade.setTid(tid);
            trade.setUserId(userId);
            trade.setAddrId(Integer.valueOf(addressId));
            trade.setStatus("1");
            trade.setReceiverName(Addrs.getName());
            String area0 = "";
            if(StringUtils.isNotBlank(Addrs.getArea())){
                area0 = Addrs.getArea().split(":")[0].replaceAll("/","");
            }
            trade.setReceiverAddress(area0+" "+Addrs.getAddr());
            trade.setReceiverMobile(Addrs.getMobile());
            trade.setCreatedTime(DateUtils.currentUnixTime());
            trade.setType(Integer.valueOf(type));//0:???????????? 1????????????

            BigDecimal totalmoney = new BigDecimal(0.0);//???????????????
            //????????????
            String areaCode = Addrs.getArea().split(":")[1];
            String provinceCode = areaCode.split("/")[0];
            String cityCode = areaCode.split("/")[1];
            Map postmap = sysFreightService.postFee(freightId, provinceCode,cityCode);
            if (postmap == null) {
                logger.info("???????????????????????????FreightId=" + freightId+ ";Addrs.provinceCode=" + provinceCode + ";Addrs.cityCode=" + cityCode + "--------??????");
                tradeIds.add("-3");
                return tradeIds;
            }
            BigDecimal postFee = new BigDecimal(postmap.get("postFee").toString());
            //????????????????????????
            BigDecimal tradeWelfareFee = BigDecimal.ZERO;
            for(SysuserShoppingCartEntity cart : list){
                SysitemItemEntity item = sysitemItemDao.selectById(cart.getItemId());
                SysitemSkuEntity sku = sysitemSkuDao.selectById(cart.getSkuId());
                //??????
                if(sku.getStore() == null || sku.getStore() < cart.getQuantity()){
                    tradeIds.add("-1");
                    return tradeIds;
                }
                //??????
                if(item.getApproveStatus() != 2){
                    tradeIds.add("-2");
                    return tradeIds;
                }
                /**
                //??????redis???????????????redis???????????????????????????????????????????????????redis???
                String sOrderId = redisUtils.get(Constants.REDIS_ORDER_ID);
                if (StringUtils.isEmpty(sOrderId)) {
                    redisUtils.set(Constants.REDIS_ORDER_ID, systradeOrderDao.selectMaxOrderId() + "");
                } else {
                    SystradeOrderEntity order1 = systradeOrderDao.selectById(Long.parseLong(sOrderId));
                    if (order1 != null) {
                        redisUtils.set(Constants.REDIS_ORDER_ID, systradeOrderDao.selectMaxOrderId() + "");
                    }
                }
                Long orderId = redisUtils.incr(Constants.REDIS_ORDER_ID);
                redisUtils.set(Constants.REDIS_ORDER_ID, orderId + "");
                 **/
                //?????????????????????????????????????????????????????????????????????????????????????????????
                SystradeOrderEntity order = new SystradeOrderEntity();
//                order.setOid(orderId); // ???????????????
                order.setTid(trade.getTid());
                order.setUserId(userId);
                order.setItemId(item.getItemId());
                order.setNum(cart.getQuantity());
                BigDecimal num = new BigDecimal(cart.getQuantity());
                BigDecimal price = sku.getPrice();
                BigDecimal welfareFee = BigDecimal.ZERO;
                if("1".equals(type)){
                    price = sku.getIntegral();
                }else if ("2".equals(type)){
                    BigDecimal divide = price.multiply(num).divide(amount,2, RoundingMode.HALF_UP);
                    welfareFee = couponMoney.multiply(divide);
                }
                order.setPrice(price);
                order.setCostPrice(sku.getCostPrice());

                BigDecimal total = num.multiply(price);
                //??????????????????
                if (itemNum==1){
                    welfareFee=couponMoney.subtract(deductionNum);
                }else {
                    deductionNum=deductionNum.add(welfareFee);
                }
                order.setWelfareFee(welfareFee);
                order.setTotalFee(total);
                order.setPayment(total.subtract(welfareFee));
                order.setTitle(item.getTitle());
                order.setPicPath(sku.getImg());
                order.setSkuId(sku.getSkuId());
                order.setSpecInfo(sku.getTitle());
                order.setStatus("1");
                systradeOrderDao.insert(order);
                totalmoney = totalmoney.add(total);
                tradeWelfareFee = tradeWelfareFee.add(welfareFee);
                itemNum--;
            }
            trade.setTotalFee(totalmoney);
            trade.setPayment(totalmoney.add(postFee).subtract(tradeWelfareFee).setScale(2, BigDecimal.ROUND_HALF_UP));
            trade.setPostFee(postFee);
            //????????????????????????
            if (buyerMessages != null && buyerMessages.length > 0) {
                try {
                    trade.setRemark(URLEncoder.encode(buyerMessages[d], "utf-8"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            systradeTradeDao.insert(trade);
            tradeIds.add(tid);
            d++;
        }
        //??????????????????????????????
        if("2".equals(type)){
            coupon.setAlreadyUse(coupon.getAlreadyUse()+1);
            sysactivityCouponService.updateById(coupon);
            //???????????????????????????????????????
            QueryWrapper<SysuserCouponEntity> sysuserCouponEntityQueryWrapper = new QueryWrapper<>();
            sysuserCouponEntityQueryWrapper.eq("coupon_id",coupon.getCouponId()).eq("user_id",userId).eq("get_status",0);
            List<SysuserCouponEntity> sysuserCouponEntities = sysuserCouponService.list(sysuserCouponEntityQueryWrapper);
            SysuserCouponEntity sysuserCouponEntity = sysuserCouponEntities.get(0);
            sysuserCouponEntity.setGetStatus(1);
            sysuserCouponService.updateById(sysuserCouponEntity);
        }

        redisUtils.set(Constants.REDIS_ORDER_PORTAL + ":" + Addrs.getUserId(), "");
        String redisCartJson = redisUtils.get(Constants.REDIS_CART_PORTAL + ":" + userId);//??????redis????????????
        if(StringUtils.isNotBlank(redisCartJson)){
            //???redis????????????json????????????list
            List<SysuserShoppingCartEntity> redisCarts = JsonUtils.jsonToList(redisCartJson, SysuserShoppingCartEntity.class);
            //?????????????????????
            for (int m = 0; m < l.size(); m++) {
                SysuserShoppingCartEntity c = l.get(m);
                for (int n = 0; n < redisCarts.size(); n++) {
                    SysuserShoppingCartEntity cart = redisCarts.get(n);
                    if (cart.getCartId() == null || "".equals(cart.getCartId()) || c.getCartId() == null || "".equals(c.getCartId())) {
                        break;
                    }
                    if (c.getCartId().equals(cart.getCartId())) {//?????????????????????????????????????????????
                        redisCarts.remove(cart);
                        n--;//??????????????????
                    }
                }
                redisUtils.set(Constants.REDIS_CART_PORTAL + ":" + userId, JsonUtils.objectToJson(redisCarts));
                shoppingCartService.removeById(c.getCartId());//??????????????????????????????
            }
        }
        return tradeIds;
    }
    @Override
    public BigDecimal sumBytid(List<String> tids) {
        return systradeTradeDao.sumByTid4portal(tids);
    }
    @Override
    public BigDecimal cumulativeByUser(Integer userId) {
        return systradeTradeDao.cumulativeByUser(userId);
    }
    @Override
    public PageUtils queryPage(QueryWrapper<SystradeTradeEntity> queryWrapper, int currPage) {
        queryWrapper.orderByDesc("created_time");
        Page<SystradeTradeEntity> pageW = new Page();
        pageW.setSize(10);
        pageW.setCurrent(currPage);
        IPage iPage =page(pageW, queryWrapper);
        iPage.setRecords(convertList(iPage.getRecords()));
        return new PageUtils(iPage);
    }



    public List convertList(List<SystradeTradeEntity>  tradelist) {
        List<ModelMap> list = new ArrayList<ModelMap>();
        if ( tradelist != null &&  tradelist.size() > 0) {
            for (SystradeTradeEntity  trade : tradelist) {
                ModelMap map = buildModelList(trade);
                list.add(map);
            }
        }
        return list;
    }
    public ModelMap buildModelList(SystradeTradeEntity  systradeTradeEntity) {
        ModelMap model = new ModelMap();
        model.put("tid",systradeTradeEntity.getTid().toString());
        model.put("statusName",sysDictService.getValueByCode("status",systradeTradeEntity.getStatus()));
        model.put("totalFee",systradeTradeEntity.getTotalFee());
        model.put("paymentFee",systradeTradeEntity.getPayment());
        model.put("buyRate",systradeTradeEntity.getBuyerRate());
        model.put("type",systradeTradeEntity.getType());
        //?????????
        List<Map> orderList = new ArrayList();
        int productNum = 0;
        QueryWrapper<SystradeOrderEntity> orderWrapper = new QueryWrapper<>();
        orderWrapper.eq("tid", systradeTradeEntity.getTid());
        List<SystradeOrderEntity> list1 = systradeOrderService.list(orderWrapper);
        for (SystradeOrderEntity systradeOrderEntity : list1) {
            Map ordermap = new HashMap();
            ordermap.put("title",systradeOrderEntity.getTitle());
            ordermap.put("specInfo",systradeOrderEntity.getSpecInfo());
            ordermap.put("num",systradeOrderEntity.getNum());
            ordermap.put("price",systradeOrderEntity.getPrice());
            ordermap.put("oid",systradeOrderEntity.getOid().toString());
            ordermap.put("imageDefaultId",systradeOrderEntity.getPicPath());
            orderList.add(ordermap);
            productNum = productNum + systradeOrderEntity.getNum();
        }
        model.put("productNum",productNum);
        model.put("orderList",orderList);
        return model;
    }

    @Override
    public Map getTradeNum(Integer userId) {
        Map map=new HashMap<>();

        //?????????
        QueryWrapper<SystradeTradeEntity> tradeWrapper = new QueryWrapper<>();
        tradeWrapper.eq("status", 1);
        tradeWrapper.eq("user_id", userId);
        List<SystradeTradeEntity> systradeTradeEntities = systradeTradeDao.selectList(tradeWrapper);
        map.put("nonPayment",systradeTradeEntities.size());

        //?????????
        QueryWrapper<SystradeTradeEntity> tradeWrapper2 = new QueryWrapper<>();
        tradeWrapper2.eq("status", 2);
        tradeWrapper2.eq("user_id", userId);
        systradeTradeEntities = systradeTradeDao.selectList(tradeWrapper2);
        map.put("dropShipping",systradeTradeEntities.size());

        //?????????
        QueryWrapper<SystradeTradeEntity> tradeWrapper3 = new QueryWrapper<>();
        tradeWrapper3.eq("status", 3);
        tradeWrapper3.eq("user_id", userId);
        systradeTradeEntities = systradeTradeDao.selectList(tradeWrapper3);
        map.put("waitReceiving",systradeTradeEntities.size());

        //?????????
        QueryWrapper<SystradeTradeEntity> tradeWrapper4 = new QueryWrapper<>();
        tradeWrapper4.eq("status", 4);
        tradeWrapper4.eq("user_id", userId);
        tradeWrapper4.eq("buyer_rate",0);
        systradeTradeEntities = systradeTradeDao.selectList(tradeWrapper4);
        map.put("completed",systradeTradeEntities.size());

        //??????
        map.put("aftersales",sysaftersalesRefundsDao.selectAftersales(userId));

        return map;
    }
    @Override
    public PageUtils queryMyPage(QueryWrapper<SystradeTradeEntity> queryWrapper, int currPage,Integer loginUserId) {
        queryWrapper.orderByDesc("created_time");
        Page<SystradeTradeEntity> pageW = new Page();
        pageW.setSize(10);
        pageW.setCurrent(currPage);
        IPage iPage =page(pageW, queryWrapper);
        iPage.setRecords(convertMyList(iPage.getRecords(),loginUserId));
        return new PageUtils(iPage);
    }
    public List convertMyList(List<SystradeTradeEntity>  tradelist,Integer loginUserId) {
        List<ModelMap> list = new ArrayList<ModelMap>();
        if ( tradelist != null &&  tradelist.size() > 0) {
            for (SystradeTradeEntity  trade : tradelist) {
                ModelMap map = buildModelMyList(trade,loginUserId);
                list.add(map);
            }
        }
        return list;
    }
    public ModelMap buildModelMyList(SystradeTradeEntity  systradeTradeEntity,Integer loginUserId) {
        ModelMap model = new ModelMap();
        model.put("tid",systradeTradeEntity.getTid().toString());
        model.put("status",systradeTradeEntity.getStatus());
        model.put("statusName",sysDictService.getValueByCode("status",systradeTradeEntity.getStatus()));
        model.put("totalFee",systradeTradeEntity.getTotalFee());
        model.put("paymentFee",systradeTradeEntity.getPayment());
        //?????????????????????
        try {
            UserEntity userEntity = userService.getById(systradeTradeEntity.getUserId());
            if (userEntity != null && StringUtils.isNotBlank(userEntity.getNickName())) {
                model.put("nickName", URLDecoder.decode(userEntity.getNickName(), "UTF-8"));
            }
        }catch (Exception e){
            logger.info("??????????????????????????????",e);
            e.printStackTrace();
        }
        //??????
        BigDecimal rate = BigDecimal.ZERO;
        List<Map> rateList = JsonUtils.jsonToList(systradeTradeEntity.getRateParam(), Map.class);
        for (Map ratemap : rateList) {
            Integer userId = Integer.valueOf(ratemap.get("userId").toString());//?????????
            if(loginUserId.equals(userId)){
                rate = new BigDecimal(ratemap.get("rate").toString());//????????????
                model.put("type",ratemap.get("type").toString());//1??????????????? 2???????????????  3:????????????
                break;
            }
        }
        //?????????
        List<Map> orderList = new ArrayList();
        int productNum = 0;
        QueryWrapper<SystradeOrderEntity> orderWrapper = new QueryWrapper<>();
        orderWrapper.eq("tid", systradeTradeEntity.getTid());
        List<SystradeOrderEntity> list1 = systradeOrderService.list(orderWrapper);
        for (SystradeOrderEntity systradeOrderEntity : list1) {
            Map ordermap = new HashMap();
            ordermap.put("title",systradeOrderEntity.getTitle());
            ordermap.put("specInfo",systradeOrderEntity.getSpecInfo());
            ordermap.put("num",systradeOrderEntity.getNum());
            ordermap.put("price",systradeOrderEntity.getPrice());
            ordermap.put("oid",systradeOrderEntity.getOid().toString());
            ordermap.put("imageDefaultId",systradeOrderEntity.getPicPath());
            //???????????? * ??????
            BigDecimal num = new BigDecimal(systradeOrderEntity.getNum());
            BigDecimal rebateMoney = (systradeOrderEntity.getPrice().subtract(systradeOrderEntity.getCostPrice())).multiply(num).setScale(BigDecimal.ROUND_HALF_UP, 2);
            if (rebateMoney.compareTo(BigDecimal.ZERO) == 1) {
                //?????????????????????   5:????????????   ???????????????????????????
                if(StringUtils.isNotBlank(systradeOrderEntity.getAftersalesStatus()) && "5".equals(systradeOrderEntity.getAftersalesStatus())){
                    ordermap.put("capitalFee",BigDecimal.ZERO);
                }else{
                    BigDecimal capitalFee = rebateMoney.multiply(rate).setScale(BigDecimal.ROUND_HALF_UP);
                    ordermap.put("capitalFee",capitalFee);
                }
            }else{
                ordermap.put("capitalFee",BigDecimal.ZERO);
            }
            orderList.add(ordermap);
            productNum = productNum + systradeOrderEntity.getNum();
        }
        model.put("productNum",productNum);
        model.put("orderList",orderList);
        return model;
    }

}
