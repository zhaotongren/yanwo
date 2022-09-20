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
            //收货地址
            SysuserUserAddrsEntity Addrs = sysuserUserAddrsDao.selectById(addrId);
            //大订单
            SystradeTradeEntity trade = new SystradeTradeEntity();
            /*tid生成规则：年月日时分秒+四位随机数*/
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
            trade.setType(3);//0:普通商品 1积分兑换 2优惠券 3秒杀
            //计算运费
            String areaCode = Addrs.getArea().split(":")[1];
            String provinceCode = areaCode.split("/")[0];
            String cityCode = areaCode.split("/")[1];
            SysitemItemEntity item = sysitemItemDao.selectById(sysitemSeckill.getItemId());
            Map postmap = sysFreightService.postFee(item.getFreightId(), provinceCode, cityCode);
            if (postmap == null) {
                logger.info("秒杀创建订单发生错误，FreightId=" + item.getFreightId() + ";Addrs.provinceCode=" + provinceCode + ";Addrs.cityCode=" + cityCode + "--------错误");
            } else {
                BigDecimal postFee = new BigDecimal(postmap.get("postFee").toString());
                trade.setTotalFee(sysitemSeckill.getSeckillPrice());
                trade.setPayment(sysitemSeckill.getSeckillPrice().add(postFee).setScale(2, BigDecimal.ROUND_HALF_UP));
                trade.setPostFee(postFee);
                trade.setSeckillId(sysitemSeckill.getId());
                systradeTradeDao.insert(trade);
                //创建订单信息（该订单属于交易信息下的自定的，其实就是商品信息）
                SysitemSkuEntity sku = sysitemSkuDao.selectById(sysitemSeckill.getSkuId());
                SystradeOrderEntity order = new SystradeOrderEntity();
                order.setTid(trade.getTid());
                order.setUserId(user.getUserId());
                order.setItemId(item.getItemId());
                order.setNum(1);
                order.setPrice(sysitemSeckill.getSeckillPrice());//秒杀价
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
                //创建待支付信息
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
                //秒杀待支付的单子  三分钟自动关闭
                redisUtils.set("seckill_order_" + trade.getTid(), miaoshaOrder, 180);
                return miaoshaOrder;
            }
        }catch (Exception e){
            logger.info("秒杀创建订单异常",e);
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
        //收货地址
        SysuserUserAddrsEntity Addrs = sysuserUserAddrsDao.selectById(Integer.valueOf(addressId));
        //取出redis缓存的订单数据
        String redisOrderJson = redisUtils.get(Constants.REDIS_ORDER_PORTAL + ":" + userId);
        logger.info("用户:"+userId+",保存提交订单:"+redisOrderJson+"...");
        List<SysuserShoppingCartEntity> l = JsonUtils.jsonToList(redisOrderJson, SysuserShoppingCartEntity.class);
        //交易总金额
        BigDecimal amount = BigDecimal.ZERO;
        //优惠券金额
        BigDecimal couponMoney = coupon==null?BigDecimal.ZERO:coupon.getMoney();
        int itemNum = 0;

        //按照运费模板分组
        Map<Integer, List> map = new HashMap<Integer, List>();
        for (int i = 0; i < l.size(); i++) {
            SysuserShoppingCartEntity c = l.get(i);
            SysitemItemEntity item = sysitemItemDao.selectById(c.getItemId());
            SysitemSkuEntity sku = sysitemSkuDao.selectById(c.getSkuId());
            amount = amount.add(sku.getPrice().multiply(new BigDecimal(c.getQuantity())));
            Integer freightId = item.getFreightId();
            List cartList4Shop = map.get(freightId);//先从map中取，list如果是空的，证明还没有初始化
            if (cartList4Shop == null) {
                cartList4Shop = new LinkedList();
                map.put(freightId, cartList4Shop);
            }
            itemNum++;
            //将数据完整的对象放入list中
            cartList4Shop.add(c);
        }
        Set<Map.Entry<Integer, List>> set = map.entrySet();
        Map<String, List> dlytmplmap = new HashMap<String, List>();
        int d = 0;//订单的
        BigDecimal deductionNum = BigDecimal.ZERO;
        for (Map.Entry<Integer, List> entry : set) {
            Integer freightId = entry.getKey();
            List<SysuserShoppingCartEntity> list = entry.getValue();
            //大订单
            SystradeTradeEntity trade = new SystradeTradeEntity();
            /*tid生成规则：年月日时分秒+四位随机数*/
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
            trade.setType(Integer.valueOf(type));//0:普通商品 1积分兑换

            BigDecimal totalmoney = new BigDecimal(0.0);//订单总金额
            //计算运费
            String areaCode = Addrs.getArea().split(":")[1];
            String provinceCode = areaCode.split("/")[0];
            String cityCode = areaCode.split("/")[1];
            Map postmap = sysFreightService.postFee(freightId, provinceCode,cityCode);
            if (postmap == null) {
                logger.info("立即购买发生错误，FreightId=" + freightId+ ";Addrs.provinceCode=" + provinceCode + ";Addrs.cityCode=" + cityCode + "--------错误");
                tradeIds.add("-3");
                return tradeIds;
            }
            BigDecimal postFee = new BigDecimal(postmap.get("postFee").toString());
            //大订单的抵扣金额
            BigDecimal tradeWelfareFee = BigDecimal.ZERO;
            for(SysuserShoppingCartEntity cart : list){
                SysitemItemEntity item = sysitemItemDao.selectById(cart.getItemId());
                SysitemSkuEntity sku = sysitemSkuDao.selectById(cart.getSkuId());
                //库存
                if(sku.getStore() == null || sku.getStore() < cart.getQuantity()){
                    tradeIds.add("-1");
                    return tradeIds;
                }
                //状态
                if(item.getApproveStatus() != 2){
                    tradeIds.add("-2");
                    return tradeIds;
                }
                /**
                //先从redis获取，如果redis没有，那么从数据库取出最大的，放到redis中
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
                //创建订单信息（该订单属于交易信息下的自定的，其实就是商品信息）
                SystradeOrderEntity order = new SystradeOrderEntity();
//                order.setOid(orderId); // 改成自增的
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
                //最后一个商品
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
            //确认订单备注信息
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
        //更新优惠券已使用数量
        if("2".equals(type)){
            coupon.setAlreadyUse(coupon.getAlreadyUse()+1);
            sysactivityCouponService.updateById(coupon);
            //更新用户优惠券状态未已使用
            QueryWrapper<SysuserCouponEntity> sysuserCouponEntityQueryWrapper = new QueryWrapper<>();
            sysuserCouponEntityQueryWrapper.eq("coupon_id",coupon.getCouponId()).eq("user_id",userId).eq("get_status",0);
            List<SysuserCouponEntity> sysuserCouponEntities = sysuserCouponService.list(sysuserCouponEntityQueryWrapper);
            SysuserCouponEntity sysuserCouponEntity = sysuserCouponEntities.get(0);
            sysuserCouponEntity.setGetStatus(1);
            sysuserCouponService.updateById(sysuserCouponEntity);
        }

        redisUtils.set(Constants.REDIS_ORDER_PORTAL + ":" + Addrs.getUserId(), "");
        String redisCartJson = redisUtils.get(Constants.REDIS_CART_PORTAL + ":" + userId);//获取redis的购物车
        if(StringUtils.isNotBlank(redisCartJson)){
            //将redis的购物车json数据转为list
            List<SysuserShoppingCartEntity> redisCarts = JsonUtils.jsonToList(redisCartJson, SysuserShoppingCartEntity.class);
            //删除购物车数据
            for (int m = 0; m < l.size(); m++) {
                SysuserShoppingCartEntity c = l.get(m);
                for (int n = 0; n < redisCarts.size(); n++) {
                    SysuserShoppingCartEntity cart = redisCarts.get(n);
                    if (cart.getCartId() == null || "".equals(cart.getCartId()) || c.getCartId() == null || "".equals(c.getCartId())) {
                        break;
                    }
                    if (c.getCartId().equals(cart.getCartId())) {//找出购物车里面相同数据，移除它
                        redisCarts.remove(cart);
                        n--;//数据复位检查
                    }
                }
                redisUtils.set(Constants.REDIS_CART_PORTAL + ":" + userId, JsonUtils.objectToJson(redisCarts));
                shoppingCartService.removeById(c.getCartId());//删除数据库购物车数据
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
        //小订单
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

        //待支付
        QueryWrapper<SystradeTradeEntity> tradeWrapper = new QueryWrapper<>();
        tradeWrapper.eq("status", 1);
        tradeWrapper.eq("user_id", userId);
        List<SystradeTradeEntity> systradeTradeEntities = systradeTradeDao.selectList(tradeWrapper);
        map.put("nonPayment",systradeTradeEntities.size());

        //代发货
        QueryWrapper<SystradeTradeEntity> tradeWrapper2 = new QueryWrapper<>();
        tradeWrapper2.eq("status", 2);
        tradeWrapper2.eq("user_id", userId);
        systradeTradeEntities = systradeTradeDao.selectList(tradeWrapper2);
        map.put("dropShipping",systradeTradeEntities.size());

        //待收货
        QueryWrapper<SystradeTradeEntity> tradeWrapper3 = new QueryWrapper<>();
        tradeWrapper3.eq("status", 3);
        tradeWrapper3.eq("user_id", userId);
        systradeTradeEntities = systradeTradeDao.selectList(tradeWrapper3);
        map.put("waitReceiving",systradeTradeEntities.size());

        //待评价
        QueryWrapper<SystradeTradeEntity> tradeWrapper4 = new QueryWrapper<>();
        tradeWrapper4.eq("status", 4);
        tradeWrapper4.eq("user_id", userId);
        tradeWrapper4.eq("buyer_rate",0);
        systradeTradeEntities = systradeTradeDao.selectList(tradeWrapper4);
        map.put("completed",systradeTradeEntities.size());

        //售后
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
        //购买者微信昵称
        try {
            UserEntity userEntity = userService.getById(systradeTradeEntity.getUserId());
            if (userEntity != null && StringUtils.isNotBlank(userEntity.getNickName())) {
                model.put("nickName", URLDecoder.decode(userEntity.getNickName(), "UTF-8"));
            }
        }catch (Exception e){
            logger.info("获取我的分销订单异常",e);
            e.printStackTrace();
        }
        //收益
        BigDecimal rate = BigDecimal.ZERO;
        List<Map> rateList = JsonUtils.jsonToList(systradeTradeEntity.getRateParam(), Map.class);
        for (Map ratemap : rateList) {
            Integer userId = Integer.valueOf(ratemap.get("userId").toString());//收益人
            if(loginUserId.equals(userId)){
                rate = new BigDecimal(ratemap.get("rate").toString());//返利比例
                model.put("type",ratemap.get("type").toString());//1：直级返利 2：间接返利  3:团长返佣
                break;
            }
        }
        //小订单
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
            //商品利润 * 比例
            BigDecimal num = new BigDecimal(systradeOrderEntity.getNum());
            BigDecimal rebateMoney = (systradeOrderEntity.getPrice().subtract(systradeOrderEntity.getCostPrice())).multiply(num).setScale(BigDecimal.ROUND_HALF_UP, 2);
            if (rebateMoney.compareTo(BigDecimal.ZERO) == 1) {
                //单子是否有售后   5:退款完成   退款的单子没有收益
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
