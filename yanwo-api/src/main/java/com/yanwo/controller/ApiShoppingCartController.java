/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yanwo.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.yanwo.Constant.Constants;
import com.yanwo.annotation.Login;
import com.yanwo.dao.SysitemItemDao;
import com.yanwo.entity.SysitemItemEntity;
import com.yanwo.entity.SysitemSkuEntity;
import com.yanwo.entity.SysuserShoppingCartEntity;
import com.yanwo.entity.UserEntity;
import com.yanwo.form.LoginForm;
import com.yanwo.service.*;
import com.yanwo.utils.JsonUtils;
import com.yanwo.utils.R;
import com.yanwo.utils.RedisUtils;
import com.yanwo.validator.ValidatorUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.*;

/**
 * 登录接口
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/cart")
@Api(tags="购物车接口")
public class ApiShoppingCartController extends BaseController{
    private static final Logger log = LoggerFactory.getLogger(ApiShoppingCartController.class);
    @Autowired
    private SysuserShoppingCartService shoppingCartService;
    @Autowired
    private SysitemItemService sysitemItemService;
    @Autowired
    private UserService userService;
    @Autowired
    protected RedisUtils redisUtils;
    @Autowired
    private SysitemSkuService sysitemSkuService;


    @PostMapping("/cart")
    @ApiOperation("查找用户的购物车列表")
    public R cart(@RequestHeader("token")String token) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//加入购物车需要先登录
                return R.auth("请登录");
            }
            Integer userId = user.getUserId();
            //获取redis的购物车
            List<SysuserShoppingCartEntity> l = new ArrayList<SysuserShoppingCartEntity>();
            String redisCartJson = redisUtils.get(Constants.REDIS_CART_PORTAL + ":" + userId);
            if (!StringUtils.isEmpty(redisCartJson)) {
                l = JsonUtils.jsonToList(redisCartJson, SysuserShoppingCartEntity.class);//将redis的购物车json数据转为list
            }
            for (int i = 0; i < l.size(); i++) {
                SysuserShoppingCartEntity cart = l.get(i);
                //已下架的商品 就从购物车移除
                SysitemItemEntity item = sysitemItemService.getById(cart.getItemId());
                //2已上架
                if(item.getApproveStatus() != 2){
                    l.remove(i);
                    i--;
                    //从购物车删除
                    shoppingCartService.removeById(cart.getCartId());
                    continue;
                }
                SysitemSkuEntity sku = sysitemSkuService.getById(cart.getSkuId());
                //规格不存在 就从购物车移除
                if(sku == null){
                    l.remove(i);
                    i--;
                    //从购物车删除
                    shoppingCartService.removeById(cart.getCartId());
                    continue;
                }
                cart.setPrice(sku.getPrice());//销售价
                cart.setTitle(item.getTitle());
                cart.setImageDefaultId(sku.getImg());
            }
            redisUtils.set(Constants.REDIS_CART_PORTAL + ":" + userId, JsonUtils.objectToJson(l));//追加到redis购物车
            return R.okput(l);
        }
        catch (Exception e){
            log.info("获取购物车列表发生异常",e);
            return R.error();
        }
    }

    @ApiOperation("加入购物车")
    @PostMapping("/add")
    public R cart(@RequestHeader("token")String token,int itemId,int skuId,int num) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//加入购物车需要先登录
                return R.auth("请登录");
            }
            Integer userId = user.getUserId();

            //获取redis的购物车
            String redisCartJson = redisUtils.get(Constants.REDIS_CART_PORTAL + ":" + userId);//获取redis的购物车
            if (!StringUtils.isEmpty(redisCartJson)) {//如果购物车数据不是空的
                //将redis的购物车json数据转为list
                List<SysuserShoppingCartEntity> redisCarts = JsonUtils.jsonToList(redisCartJson, SysuserShoppingCartEntity.class);
                boolean r = false;
                for (SysuserShoppingCartEntity redisCart : redisCarts) {
                    if (redisCart.getSkuId().intValue() == skuId) {
                        redisCart.setQuantity(redisCart.getQuantity() + num);//只增加数量
                        shoppingCartService.updateCartNum(userId, skuId, num);//同步更新数据库
                        r = true;
                        break;
                    }
                }
                if (!r) {//说明购物车没有，也需要新增
                    saveCart(redisCarts, userId, num, itemId,skuId);
                }
                redisUtils.set(Constants.REDIS_CART_PORTAL + ":" + userId, JsonUtils.objectToJson(redisCarts));//追加到购物车
            } else {//如果购物车数据是空的，就需要做新增
                List<SysuserShoppingCartEntity> redisCarts = new ArrayList<SysuserShoppingCartEntity>();
                saveCart(redisCarts, userId, num, itemId,skuId);
                redisUtils.set(Constants.REDIS_CART_PORTAL + ":" + userId, JsonUtils.objectToJson(redisCarts));//追加到购物车
            }
            return R.ok();
        }
        catch (Exception e){
            log.info("获取购物车列表发生异常",e);
            return R.error();
        }
    }
    @PostMapping("/update")
    @ApiOperation("更新购物车")
    public R updateCart(@RequestHeader("token")String token,int skuId,int num) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//加入购物车需要先登录
                return R.auth("请登录");
            }
            Integer userId = user.getUserId();
            String redisCartJson = redisUtils.get(Constants.REDIS_CART_PORTAL + ":" + userId);//获取redis的购物车
            //将redis的购物车json数据转为list
            List<SysuserShoppingCartEntity> redisCarts = JsonUtils.jsonToList(redisCartJson, SysuserShoppingCartEntity.class);
            for (SysuserShoppingCartEntity redisCart : redisCarts) {
                if (redisCart.getSkuId().intValue() == skuId) {
                    if (num < 1) {
                        return R.error("数量不能为0");
                    }
                    redisCart.setQuantity(num);//只增加数量
                    shoppingCartService.updateCartNum(userId, skuId, num);//同步更新数据库
                    break;
                }
            }
            redisUtils.set(Constants.REDIS_CART_PORTAL + ":" + userId, JsonUtils.objectToJson(redisCarts));//追加到购物车
            return R.ok();
        }catch (Exception e){
            log.info("更新购物车发生异常",e);
            return R.error();
        }
    }
    @PostMapping("/delete")
    @ApiOperation("删除购物车")
    public R deleteCart(@RequestHeader("token")String token,int cartId) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//加入购物车需要先登录
                return R.auth("请登录");
            }
            Integer userId = user.getUserId();
            String redisCartJson = redisUtils.get(Constants.REDIS_CART_PORTAL + ":" + userId);//获取redis的购物车
            //将redis的购物车json数据转为list
            List<SysuserShoppingCartEntity> redisCarts = JsonUtils.jsonToList(redisCartJson, SysuserShoppingCartEntity.class);
            for (SysuserShoppingCartEntity redisCart : redisCarts) {
                if (redisCart.getCartId().intValue() == cartId) {
                    redisCarts.remove(redisCart);
                    redisUtils.set(Constants.REDIS_CART_PORTAL + ":" + userId, JsonUtils.objectToJson(redisCarts));//追加到购物车
                    shoppingCartService.removeById(cartId);
                    break;
                }
            }
            return R.ok();
        }catch (Exception e){
            log.info("删除购物车发生异常",e);
            return R.error();
        }
    }
    private void saveCart(List redisCarts, int userId, int num, int itemId, int skuId) throws Exception {
        //查出商品信息//追加商品
        SysitemItemEntity item = sysitemItemService.getById(itemId);
        UserEntity user = userService.getById(userId);
        SysitemSkuEntity sku = sysitemSkuService.getById(skuId);
        SysuserShoppingCartEntity cart = new SysuserShoppingCartEntity();
        cart.setUserIdent(user.getWxOpenId());//设置用户令牌
        cart.setUserId(userId);
        cart.setObjType("item");
        cart.setObjIdent("item_" + item.getItemId());
        cart.setItemId(item.getItemId());
        cart.setSkuId(skuId);
        cart.setTitle(item.getTitle());
        cart.setImageDefaultId(sku.getImg());
        cart.setQuantity(num);
        cart.setSpecInfo(sku.getTitle());//规格名称
        cart.setCreatedTime((int) (System.currentTimeMillis() / 1000));
        cart.setModifiedTime((int) (System.currentTimeMillis() / 1000));
        //同时需要保存到数据库，这里不太合适（影响性能），但是客户要求保存，用来分析数据，其实可以从redis获取分析，你懂得
        shoppingCartService.save(cart);
        redisCarts.add(cart);
    }

}
