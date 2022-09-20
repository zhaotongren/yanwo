/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yanwo.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.Constant.Constants;
import com.yanwo.encrypt.BCrypt;
import com.yanwo.entity.*;
import com.yanwo.service.*;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.JsonUtils;
import com.yanwo.utils.R;
import com.yanwo.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * 购买接口
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/order")
@Api(tags="购买接口")
public class ApiOrderController extends BaseController{
    private static final Logger log = LoggerFactory.getLogger(ApiOrderController.class);

    @Autowired
    private SysitemItemService sysitemItemService;
    @Autowired
    private SysuserUserAddrsService sysuserUserAddrsService;
    @Autowired
    private SyscapitalCapitalService syscapitalCapitalService;
    @Autowired
    private SystradeTradeService systradeTradeService;
    @Autowired
    protected RedisUtils redisUtils;
    @Autowired
    private EctoolsPaymentsService ectoolsPaymentsService;
    @Autowired
    private SysitemSkuService sysitemSkuService;
    @Autowired
    private SysFreightService sysFreightService;
    @Autowired
    SysuserCouponService sysuserCouponService;
    @Autowired
    SysactivityCouponService sysactivityCouponService;

    @PostMapping("/orderbuynow")
    @ApiOperation("立即购买")
    public R cart(@RequestHeader("token")String token,Integer itemId,Integer skuId,int num,int itemType) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//加入购物车需要先登录
                return R.auth("请登录");
            }
            if (itemId == null || skuId == null) {
                return R.error("没有选择商品");
            }
            if (num <= 0) {
                return R.error("商品数量格式错误");
            }
            Integer userId = user.getUserId();
            /**
             * 收货地址
             * 1、查询默认收货地址
             * 2、没有默认地址 查询第一条收货地址
             */
            SysuserUserAddrsEntity addrs = null;
            QueryWrapper<SysuserUserAddrsEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id",userId);
            queryWrapper.eq("def_addr",1);
            List<SysuserUserAddrsEntity> addrList = sysuserUserAddrsService.list(queryWrapper);
            if (addrList != null && addrList.size() > 0) {
                addrs = addrList.get(0);
            } else {
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_id",userId);
                addrList = sysuserUserAddrsService.list(queryWrapper);
                if (addrList != null && addrList.size() > 0) {
                    addrs = addrList.get(0);
                }
            }
            List result = new LinkedList();
            SysitemItemEntity item = sysitemItemService.getById(itemId);
            SysitemSkuEntity sku = sysitemSkuService.getById(skuId);
            List<Map> cartList = new ArrayList();//商品列表
            Map cart = new HashMap();
            cart.put("itemId", item.getItemId());//商品ID
            cart.put("skuId", skuId);//商品ID
            cart.put("title", item.getTitle());//商品名称
            cart.put("num", num);//购买数量
            cart.put("imageDefaultId", sku.getImg());//规格图片
            BigDecimal price = sku.getPrice();
            if(itemType == 1){
                price = sku.getIntegral();
            }
            cart.put("price",price);
            cart.put("specInfo",sku.getTitle());
            cartList.add(cart);
            Map resmap = new HashMap();
            Map resultMap = new HashMap();
            resultMap.put("Addrs", addrs);
            resmap.put("cart", cartList);
            resultMap.put("productNum", num);//总件数
            BigDecimal totalFee = price.multiply(new BigDecimal(num)).setScale(2, RoundingMode.HALF_DOWN);
            resultMap.put("totalFee", totalFee);//商品总金额
            //没有收货地址
            BigDecimal postFee = new BigDecimal(0);
            BigDecimal paymentFee = new BigDecimal(0);
            if (addrs == null || addrs.getAddrId() == null) {
                resmap.put("postFee", 0);
                resmap.put("postName", "");
                paymentFee = totalFee;
            }else{
                String areaCode = addrs.getArea().split(":")[1];
                String provinceCode = areaCode.split("/")[0];
                String cityCode = areaCode.split("/")[1];
                Map postmap = sysFreightService.postFee(item.getFreightId(), provinceCode,cityCode);
                if (postmap == null) {
                    log.info("立即购买发生错误，FreightId=" + item.getFreightId()+ ";Addrs.provinceCode=" + provinceCode + ";Addrs.cityCode=" + cityCode + "--------错误");
                    return R.error("数据异常，请联系客服处理！");
                }
                postFee = new BigDecimal(postmap.get("postFee").toString());
                resmap.put("postFee", postFee);
                resmap.put("postName", postmap.get("postName"));
                paymentFee = totalFee.add(postFee);
            }
            resultMap.put("paymentFee", paymentFee);//应支付总金额
            result.add(resmap);
            resultMap.put("orders",result);
            //加入redis
            SysuserShoppingCartEntity shoppingCart = new SysuserShoppingCartEntity();
            shoppingCart.setUserIdent(user.getWxOpenId());//设置用户令牌
            shoppingCart.setUserId(userId);
            shoppingCart.setObjType("item");
            shoppingCart.setObjIdent("item_" + item.getItemId());
            shoppingCart.setItemId(item.getItemId());
            shoppingCart.setSkuId(skuId);
            shoppingCart.setTitle(item.getTitle());
            shoppingCart.setImageDefaultId(sku.getImg());//规格图片
            shoppingCart.setQuantity(num);
            shoppingCart.setSpecInfo(sku.getTitle());//规格名称
            shoppingCart.setCreatedTime((int) (System.currentTimeMillis() / 1000));
            shoppingCart.setModifiedTime((int) (System.currentTimeMillis() / 1000));
            List<SysuserShoppingCartEntity> carts = new ArrayList<SysuserShoppingCartEntity>();
            carts.add(shoppingCart);
            redisUtils.set(Constants.REDIS_ORDER_PORTAL + ":" + user.getUserId(), JsonUtils.objectToJson(carts));

            //是否是从积分兑换专区过来的
            resultMap.put("itemType", itemType);//0普通商品1积分商品
            resultMap.put("isExchange", false);//是否可用积分兑换
            resultMap.put("totalIntegral", 0);//总积分
            //可用优惠券
            resultMap.put("coupons",new Arrays[0]);
            resultMap.put("couponNumber",0);
            //资产信息
            QueryWrapper<SyscapitalCapitalEntity> queryWrapper1 = new QueryWrapper<SyscapitalCapitalEntity> ();
            queryWrapper1.eq("user_id",userId);
            SyscapitalCapitalEntity capital = syscapitalCapitalService.getOne(queryWrapper1);
            resultMap.put("rechargeFee",capital != null ? capital.getTotalRecharge() : "0");//充值余额
            if(itemType == 1){
                if(capital != null){
                    resultMap.put("totalIntegral", capital.getTotalIntegral());//总积分
                    if(capital.getTotalIntegral().compareTo(paymentFee) >= 0){
                        resultMap.put("isExchange", true);//是否可用积分兑换
                    }

                }
            }else {
                List<SysactivityCouponEntity> couponEntityList = sysuserCouponService.userCouponList(userId,0);
                Iterator<SysactivityCouponEntity> iterator = couponEntityList.iterator();
                while (iterator.hasNext()){
                    SysactivityCouponEntity next = iterator.next();
                    if (next.getCouponStatus()!=0||new BigDecimal(next.getRestrictMoney().toString()).compareTo(totalFee)==1){
                        iterator.remove();
                    }
                }
                resultMap.put("coupons",couponEntityList);
                resultMap.put("couponNumber",couponEntityList.size());
            }

            return R.okput(resultMap);
        }
        catch (Exception e){
            log.info("立即购买发生异常",e);
            return R.error();
        }
    }
    @PostMapping("/order-cart")
    @ApiOperation("购物车去结算")
    public R orderCart(@RequestHeader("token")String token,
                                @RequestParam("cartIds") String[] cartIds) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//加入购物车需要先登录
                return R.auth("请登录");
            }
            if (cartIds == null) {
                return R.error("没有选择商品");
            }
            Integer userId = user.getUserId();
            /**
             * 收货地址
             * 1、查询默认收货地址
             * 2、没有默认地址 查询第一条收货地址
             */
            SysuserUserAddrsEntity addrs = null;
            QueryWrapper<SysuserUserAddrsEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id",userId);
            queryWrapper.eq("def_addr",1);
            List<SysuserUserAddrsEntity> addrList = sysuserUserAddrsService.list(queryWrapper);
            if (addrList != null && addrList.size() > 0) {
                addrs = addrList.get(0);
            } else {
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_id",userId);
                addrList = sysuserUserAddrsService.list(queryWrapper);
                if (addrList != null && addrList.size() > 0) {
                    addrs = addrList.get(0);
                }
            }
            List result = new LinkedList();
            BigDecimal totalFee = new BigDecimal(0);//商品总金额
            BigDecimal postFee = new BigDecimal(0);//总运费
            Integer productNum = 0;//总数量
            //这里需要订单确认信息存储到redis中
            List<SysuserShoppingCartEntity> orderlist = getOrderCart(userId, cartIds);//得到用户购买的商品
            //存储到redis中
            redisUtils.set(Constants.REDIS_ORDER_PORTAL + ":" + userId, JsonUtils.objectToJson(orderlist));//存储到redis
            //按照运费模板分组
            Map<Integer, List> map = new HashMap<Integer, List>();
            for (int i = 0; i < orderlist.size(); i++) {
                SysuserShoppingCartEntity c = orderlist.get(i);
                SysitemItemEntity item = sysitemItemService.getById(c.getItemId());
                Integer freightId = item.getFreightId();
                List cartList4Shop = map.get(freightId);//先从map中取，list如果是空的，证明还没有初始化
                if (cartList4Shop == null) {
                    cartList4Shop = new LinkedList();
                    map.put(freightId, cartList4Shop);
                }
                //将数据完整的对象放入list中
                cartList4Shop.add(c);
            }
            Set<Map.Entry<Integer, List>> set = map.entrySet();
            Map<String, List> dlytmplmap = new HashMap<String, List>();
            for (Map.Entry<Integer, List> entry : set) {
                Integer freightId = entry.getKey();
                List<SysuserShoppingCartEntity> list = entry.getValue();
                Map resmap = new HashMap();
                List<Map> cartList = new ArrayList();//商品列表
                for(SysuserShoppingCartEntity shoppingCart : list){
                    SysitemItemEntity item = sysitemItemService.getById(shoppingCart.getItemId());
                    SysitemSkuEntity sku = sysitemSkuService.getById(shoppingCart.getSkuId());
                    Map cart = new HashMap();
                    cart.put("itemId", item.getItemId());//商品ID
                    cart.put("skuId", sku.getSkuId());//商品ID
                    cart.put("title", item.getTitle());//商品名称
                    cart.put("num", shoppingCart.getQuantity());//购买数量
                    cart.put("imageDefaultId", sku.getImg());//规格图片
                    cart.put("price",sku.getPrice());
                    cart.put("specInfo",sku.getTitle());
                    cartList.add(cart);
                    totalFee = totalFee.add(sku.getPrice().multiply(new BigDecimal(shoppingCart.getQuantity())));
                    productNum += shoppingCart.getQuantity();
                }
                resmap.put("cart", cartList);
                //计算运费
                if (addrs == null || addrs.getAddrId() == null) {
                    resmap.put("postFee", 0);
                    resmap.put("postName", "");
                }else{
                    String areaCode = addrs.getArea().split(":")[1];
                    String provinceCode = areaCode.split("/")[0];
                    String cityCode = areaCode.split("/")[1];
                    Map postmap = sysFreightService.postFee(freightId, provinceCode,cityCode);
                    if (postmap == null) {
                        log.info("立即购买发生错误，FreightId=" + freightId+ ";Addrs.provinceCode=" + provinceCode + ";Addrs.cityCode=" + cityCode + "--------错误");
                        return R.error("数据异常，请联系客服处理！");
                    }
                    resmap.put("postFee", new BigDecimal(postmap.get("postFee").toString()));
                    resmap.put("postName", postmap.get("postName"));
                    postFee = postFee.add(new BigDecimal(postmap.get("postFee").toString()));
                }
                result.add(resmap);
            }
            Map resultMap = new HashMap();
            resultMap.put("Addrs", addrs);
            resultMap.put("orders",result);
            resultMap.put("productNum", productNum);//总件数
            resultMap.put("totalFee", totalFee.setScale(2, RoundingMode.HALF_DOWN));//商品总金额
            resultMap.put("paymentFee", totalFee.add(postFee).setScale(2, RoundingMode.HALF_DOWN));//支付金额
            //查询可用优惠券
            List<SysactivityCouponEntity> couponEntityList = sysuserCouponService.userCouponList(userId,0);
            Iterator<SysactivityCouponEntity> iterator = couponEntityList.iterator();
            while (iterator.hasNext()){
                SysactivityCouponEntity next = iterator.next();
                if (next.getCouponStatus()!=0||new BigDecimal(next.getRestrictMoney().toString()).compareTo(totalFee)==1){
                    iterator.remove();
                }
            }
            resultMap.put("coupons",couponEntityList);
            resultMap.put("couponNumber",couponEntityList.size());
            //资产信息
            QueryWrapper<SyscapitalCapitalEntity> queryWrapper1 = new QueryWrapper<SyscapitalCapitalEntity> ();
            queryWrapper1.eq("user_id",userId);
            SyscapitalCapitalEntity capital = syscapitalCapitalService.getOne(queryWrapper1);
            resultMap.put("rechargeFee",capital != null ? capital.getTotalRecharge() : "0");//充值余额
            return R.okput(resultMap);
        }
        catch (Exception e){
                log.info("获取购物车列表发生异常",e);
                return R.error();
        }
     }
    @PostMapping("/create")
    @ApiOperation("确认订单")
    public R createOrder(@RequestHeader("token")String token,String addrId,String[] buyerMessages,String type,String couponId,Integer payType) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//加入购物车需要先登录
                return R.auth("请登录");
            }
            if (StringUtils.isBlank(addrId)) {
                return R.error("收货地址不能为空");
            }
            Pattern pattern = compile("[0-9]*");
            if(!pattern.matcher(addrId).matches()){
                return R.error("确认订单异常,请联系平台");
            }
            SysactivityCouponEntity coupon = null;
            if ("2".equals(type)){
                if (couponId.isEmpty()){
                    return R.error("请选择优惠券");
                }
                SysactivityCouponEntity byId = sysactivityCouponService.getById(Integer.valueOf(couponId));
                if (byId.getExpireTime()< DateUtils.currentUnixTime()){
                    byId.setCouponStatus(2);
                    sysactivityCouponService.updateById(byId);
                    return R.error("优惠券已过期！");
                }
                coupon = byId;
            }
            Integer userId = user.getUserId();
            //获取redis确认要买的商品
            String redisCartJson = redisUtils.get(Constants.REDIS_ORDER_PORTAL + ":" + userId);
            if (!StringUtils.isBlank(redisCartJson)) {
                //将redis的购物车json数据转为list
                List<String> tradeIds = systradeTradeService.saveTradeAndSubOrder(userId,addrId,buyerMessages,type,coupon);
                for (int i = 0; i < tradeIds.size(); i++) {
                    String tid = tradeIds.get(i);
                    if (tid.equals("-1")) {
                        return R.error("库存不足");
                    }
                    else if (tid.equals("-2")) {
                        return R.error("有商品未上架");
                    }
                    else if (tid.equals("-3")) {
                        return R.error("数据异常,请联系平台");
                    }
                }
                Map resultmap = new HashMap();
                resultmap.put("payType",payType);
                BigDecimal totalFee = systradeTradeService.sumBytid(tradeIds);
                //是否使用余额支付 1微信支付，2余额+微信支付 3余额支付
                if(payType == 3){
                    QueryWrapper<SyscapitalCapitalEntity> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("user_id",userId);
                    SyscapitalCapitalEntity capital = syscapitalCapitalService.getOne(queryWrapper);
                    if(capital == null || capital.getTotalRecharge().compareTo(totalFee) == -1){
                        return R.error("充值余额不足，请选择微信支付");
                    }
                    syscapitalCapitalService.saveTradeOrderRecharge(tradeIds, user.getUserId(),totalFee);
                }else if(payType == 2){
                    QueryWrapper<SyscapitalCapitalEntity> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("user_id",userId);
                    SyscapitalCapitalEntity capital = syscapitalCapitalService.getOne(queryWrapper);
                    if(capital != null){
                        String paymentId = ectoolsPaymentsService.paymentPayready(Constants.TRADE_PAY_ID_WE_CHAT, Constants.TRADE_PAY_NAME_WE_CHAT, tradeIds, userId);
                        resultmap.put("paymentId", paymentId);
                        resultmap.put("totalFee", JsonUtils.objectToJson(totalFee.subtract(capital.getTotalRecharge()).setScale(2, BigDecimal.ROUND_HALF_UP)));
                    }else{
                        return R.error("充值余额不足，请选择微信支付");
                    }
                }else if(payType ==1){
                    String paymentId = ectoolsPaymentsService.paymentPayready(Constants.TRADE_PAY_ID_WE_CHAT, Constants.TRADE_PAY_NAME_WE_CHAT, tradeIds, userId);
                    resultmap.put("paymentId", paymentId);
                    resultmap.put("totalFee", JsonUtils.objectToJson(totalFee.setScale(2, BigDecimal.ROUND_HALF_UP)));
                }
                resultmap.put("title",Constants.PAY_TITLE);
                return R.okput(resultmap);
            }
            return R.error("确认订单异常,请联系平台");
        }
        catch (Exception e){
            log.info("确认订单发生异常",e);
            return R.error();
        }
    }
    @PostMapping("/pointCreate")
    @ApiOperation("积分兑换")
    public R pointCreate(@RequestHeader("token")String token,String addrId,String[] buyerMessages) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//加入购物车需要先登录
                return R.auth("请登录");
            }
            if (StringUtils.isBlank(addrId)) {
                return R.error("收货地址不能为空");
            }
            Pattern pattern = compile("[0-9]*");
            if(!pattern.matcher(addrId).matches()){
                return R.error("确认订单异常,请联系平台");
            }
            Integer userId = user.getUserId();
            //获取redis确认要买的商品
            String redisCartJson = redisUtils.get(Constants.REDIS_ORDER_PORTAL + ":" + userId);
            if (!StringUtils.isBlank(redisCartJson)) {
                //将redis的购物车json数据转为list
                List<String> tradeIds = systradeTradeService.saveTradeAndSubOrder(userId,addrId,buyerMessages,"1",null);
                for (int i = 0; i < tradeIds.size(); i++) {
                    String tid = tradeIds.get(i);
                    if (tid.equals("-1")) {
                        return R.error("库存不足");
                    }
                    else if (tid.equals("-2")) {
                        return R.error("有商品未上架");
                    }
                    else if (tid.equals("-3")) {
                        return R.error("数据异常,请联系平台");
                    }
                }
                String paymentId = ectoolsPaymentsService.paymentPayready(Constants.TRADE_PAY_ID_POINT, Constants.TRADE_PAY_NAME_POINT, tradeIds, userId);
                int i = ectoolsPaymentsService.noPayTrade("NO_PAY", paymentId, new Date());
                return R.ok();
            }
            return R.error("积分兑换异常,请联系平台");
        }
        catch (Exception e){
            log.info("积分兑换发生异常",e);
            return R.error();
        }
    }
    private List<SysuserShoppingCartEntity> getOrderCart(Integer userId, String[] cartIds) {
        List<SysuserShoppingCartEntity> orderCartList = new ArrayList<SysuserShoppingCartEntity>();
        //获取redis的购物车
        String redisCartJson = redisUtils.get(Constants.REDIS_CART_PORTAL + ":" + userId);
        if (!StringUtils.isBlank(redisCartJson)) {
            //将redis的购物车json数据转为list
            List<SysuserShoppingCartEntity> redisCartItems = JsonUtils.jsonToList(redisCartJson, SysuserShoppingCartEntity.class);
            //再将list转为map，key是id，value是CartItem
            Map<Integer, SysuserShoppingCartEntity> map = new HashMap<Integer, SysuserShoppingCartEntity>();
            for (SysuserShoppingCartEntity redisCart : redisCartItems) {
                map.put(redisCart.getCartId(), redisCart);
            }
            //遍历购买商品的id列表
            for (String id : cartIds) {
                //取出要购买的商品信息
                SysuserShoppingCartEntity cart = map.get(Integer.parseInt(id));
                //将商品信息添加到列表中
                orderCartList.add(cart);
            }
        }
        return orderCartList;
    }
    @PostMapping("/topayment")
    @ApiOperation("去支付")
    public R topayment(@RequestHeader("token")String token, String paymentId) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {
                return R.auth("请登录");
            }
            EctoolsPaymentsEntity payments = ectoolsPaymentsService.getById(paymentId);
            Map resultmap = new HashMap();
            resultmap.put("paymentId", paymentId);
            resultmap.put("totalFee", JsonUtils.objectToJson(payments.getMoney().setScale(2, BigDecimal.ROUND_HALF_UP)));
            resultmap.put("title",Constants.PAY_TITLE);
            return R.okput(resultmap);
        }catch (Exception e){
            log.info("去支付发生异常",e);
            return R.error();
        }
    }
    @RequestMapping("/topay/{tid}")
    public R topay(@RequestHeader("token") String token, @PathVariable String tid,Integer payType) {
        UserEntity user = getUserFromTokenApp(token);
        if (user == null) {
            return R.auth("请登录");
        }//需要先登录
        SystradeTradeEntity trade = systradeTradeService.getById(tid);
        if (trade != null && trade.getStatus().equals("1")){
            BigDecimal totalFee = trade.getPayment();
            List<String> tids = new ArrayList<>();
            tids.add(tid);
            Map resultmap = new HashMap();

            //是否使用余额支付 1微信支付，2余额+微信支付 3余额支付
            if(payType == 3){
                QueryWrapper<SyscapitalCapitalEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_id",user.getUserId());
                SyscapitalCapitalEntity capital = syscapitalCapitalService.getOne(queryWrapper);
                if(capital == null || capital.getTotalRecharge().compareTo(totalFee) == -1){
                    return R.error("充值余额不足，请选择微信支付");
                }
                syscapitalCapitalService.saveTradeOrderRecharge(tids, user.getUserId(),totalFee);
            }else if(payType == 2){
                QueryWrapper<SyscapitalCapitalEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_id",user.getUserId());
                SyscapitalCapitalEntity capital = syscapitalCapitalService.getOne(queryWrapper);
                if(capital != null){
                    String paymentId = ectoolsPaymentsService.paymentPayready(Constants.TRADE_PAY_ID_WE_CHAT, Constants.TRADE_PAY_NAME_WE_CHAT, tids, user.getUserId());
                    resultmap.put("paymentId", paymentId);
                    resultmap.put("totalFee", JsonUtils.objectToJson(totalFee.subtract(capital.getTotalRecharge()).setScale(2, BigDecimal.ROUND_HALF_UP)));
                }else{
                    return R.error("充值余额不足，请选择微信支付");
                }
            }else if(payType ==1){
                String paymentId = ectoolsPaymentsService.paymentPayready(Constants.TRADE_PAY_ID_WE_CHAT, Constants.TRADE_PAY_NAME_WE_CHAT, tids, user.getUserId());
                resultmap.put("paymentId", paymentId);
                resultmap.put("totalFee", JsonUtils.objectToJson(totalFee.setScale(2, BigDecimal.ROUND_HALF_UP)));
            }
            resultmap.put("title", Constants.PAY_TITLE);
            return R.okput(resultmap);
        }else{
            return R.error("订单已经超出付款时间！");
        }

    }
    @PostMapping("payByWalletPwd")
    @ApiOperation("福利钱支付")
//    public R payByWalletPwd(@RequestHeader("token") String token,@RequestParam String paymentId,@RequestParam String payPwd) {
    public R payByWalletPwd(@RequestHeader("token") String token,@RequestParam String paymentId) {
        UserEntity user = getUserFromTokenApp(token);
        if (user == null) {//加入购物车需要先登录
            return R.auth("请登录");
        }//需要先登录
        EctoolsPaymentsEntity payments = ectoolsPaymentsService.getById(paymentId);
        if("succ".equals(payments.getStatus())){
            return R.error("该订单已支付，请勿重复支付");
        }
        //重复操作判断
        if (redisUtils.existsKey(Constants.USER_PAY_BY_WELFARE+user.getUserId())){
            return R.error("操作频繁,请10秒后重新操作");
        }else {
            redisUtils.set(Constants.USER_PAY_BY_WELFARE+user.getUserId(),"0",10);
        }
       /** //查询是否设置钱包密码
        QueryWrapper<SyscapitalCapitalEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",user.getUserId());
        SyscapitalCapitalEntity capital = syscapitalCapitalService.getOne(queryWrapper);
        //开始验证密码
        if(!BCrypt.checkpw(payPwd, capital.getWalletPwd())){
            return R.error("支付密码错误！");
        }**/
        int i = ectoolsPaymentsService.paymentPaySyntony(paymentId,new Date());
        if(i==1){
            log.info("-----------支付回调成功-----------");
        }else{
            log.info("-----------支付回调失败-----------");
        }
        return R.ok();
    }

}
