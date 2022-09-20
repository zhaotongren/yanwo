/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.yanwo.controller;


import com.aliyun.opensearch.sdk.dependencies.org.apache.http.HttpResponse;
import com.aliyun.opensearch.sdk.dependencies.org.apache.http.client.HttpClient;
import com.aliyun.opensearch.sdk.dependencies.org.apache.http.client.methods.HttpGet;
import com.aliyun.opensearch.sdk.dependencies.org.apache.http.impl.client.DefaultHttpClient;
import com.aliyun.opensearch.sdk.dependencies.org.apache.http.util.EntityUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.util.StringUtil;
import com.yanwo.Constant.Constants;
import com.yanwo.dao.SyscapitalCapitalDetailDao;
import com.yanwo.encrypt.BCrypt;
import com.yanwo.entity.SyscapitalCapitalEntity;
import com.yanwo.entity.SyscapitalCapitalWithdrawEntity;
import com.yanwo.entity.SysrechargeCardEntity;
import com.yanwo.entity.UserEntity;
import com.yanwo.service.*;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.bytebuddy.asm.Advice;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资产接口
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/capital")
@Api(tags = "资产接口")
public class ApiCapitalController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ApiCapitalController.class);
    @Autowired
    private SyscapitalCapitalService syscapitalCapitalService;
    @Autowired
    private SyscapitalCapitalDetailService syscapitalCapitalDetailService;
    @Autowired
    private SyscapitalCapitalWithdrawService syscapitalCapitalWithdrawService;
    @Autowired
    private SyscapitalCapitalIntegralService syscapitalCapitalIntegralService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private SysrechargeCardService sysrechargeCardService;

    @PostMapping("capital")
    @ApiOperation("钱包金额")
    public R capital(@RequestHeader("token") String token) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//加入购物车需要先登录
                return R.auth("请登录");
            }
            Integer userId=user.getUserId();
            Map map = new HashMap();
            //钱包金额
            QueryWrapper<SyscapitalCapitalEntity> queryWrapper1 = new QueryWrapper<SyscapitalCapitalEntity>();
            queryWrapper1.eq("user_id", userId);
            SyscapitalCapitalEntity capital = syscapitalCapitalService.getOne(queryWrapper1);
            String withdraw_rate = sysConfigService.getValue("withdraw_rate");
            String withdraw_lower = sysConfigService.getValue("withdraw_lower");
            if (StringUtils.isBlank(withdraw_rate)) {
                return R.error("提现手续费比率未找到!");
            }
            if (StringUtils.isBlank(withdraw_lower)) {
                return R.error("最低提现额度未找到!");
            }
            map.put("withdrawRate", withdraw_rate);
            map.put("withdrawLower", withdraw_lower);

            if (capital != null) {
                map.put("walletFee", capital.getTotalCapital());//钱包余额
                BigDecimal userWalletFee = syscapitalCapitalService.getUserWalletFee(userId);
                map.put("userWalletFee", userWalletFee);//钱包可用余额
                map.put("integralFee", capital.getTotalIntegral());//积分
                map.put("rechargeFee",capital.getTotalRecharge());//充值余额
                map.put("withdrawFee",userWalletFee.subtract(capital.getTotalRecharge()));//可提现金额=钱包余额-充值金额-提现中金额
            } else {
                map.put("walletFee", 0);
                map.put("userWalletFee", 0);
                map.put("integralFee", 0);
                map.put("rechargeFee", 0);
                map.put("withdrawFee",0);
            }
            return R.okput(map);
        } catch (Exception e) {
            logger.info("查询用户累计消费和钱包余额异常", e);
            return R.error();
        }
    }

    @PostMapping("detail")
    @ApiOperation("体现流水记录")
    public R detail(@RequestHeader("token") String token, @RequestParam(defaultValue = "1") Integer page) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//加入购物车需要先登录
                return R.auth("请登录");
            }
            PageUtils pages = syscapitalCapitalWithdrawService.queryPage(user.getUserId(), page);

            return R.okput(pages);

        } catch (Exception e) {
            logger.info("查询用户钱包流水记录异常", e);
            return R.error();
        }
    }
    @PostMapping("integralDetail")
    @ApiOperation("返佣/充值流水记录")
    public R integralDetail(@RequestHeader("token") String token, @RequestParam(defaultValue = "1") Integer page,Integer type) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//加入购物车需要先登录
                return R.auth("请登录");
            }
            PageUtils pages = syscapitalCapitalDetailService.queryPage(user.getUserId(), page,type);

            return R.okput(pages);

        } catch (Exception e) {
            logger.info("查询用户钱包流水记录异常", e);
            return R.error();
        }
    }
    @PostMapping("pointDetail")
    @ApiOperation("积分流水记录")
    public R pointDetail(@RequestHeader("token") String token, @RequestParam(defaultValue = "1") Integer page) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//加入购物车需要先登录
                return R.auth("请登录");
            }
            PageUtils pages = syscapitalCapitalIntegralService.queryPage(user.getUserId(), page);

            return R.okput(pages);

        } catch (Exception e) {
            logger.info("查询用户钱包流水记录异常", e);
            return R.error();
        }
    }

    @PostMapping("withdraw")
    @ApiOperation("提现")
    public R withdraw(@RequestHeader("token") String token,BigDecimal withdrawFee, String reason,String bankCard,String realName) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//加入购物车需要先登录
                return R.auth("请登录");
            }
            Integer userId=user.getUserId();
            QueryWrapper<SyscapitalCapitalEntity> queryWrapper1 = new QueryWrapper<SyscapitalCapitalEntity>();
            queryWrapper1.eq("user_id", userId);
            SyscapitalCapitalEntity capital = syscapitalCapitalService.getOne(queryWrapper1);
            if (capital == null){
                return R.error("提现余额不足");
            }
            BigDecimal userWalletFee = syscapitalCapitalService.getUserWalletFee(userId);
            userWalletFee = userWalletFee.subtract(capital.getTotalRecharge());//充值金额不可提现
            // 计算手续费
            String withdrawRate = sysConfigService.getValue("withdraw_rate");
            String withdrawLower = sysConfigService.getValue("withdraw_lower");
            if (StringUtils.isBlank(withdrawRate)) {
                return R.error("提现手续费比率未找到!");
            }
            if (StringUtils.isBlank(withdrawLower)) {
                return R.error("最低提现额度未找到!");
            }
            if (withdrawFee.compareTo(new BigDecimal(withdrawLower)) < 0) {
                return R.error("不能低于最低提现额度【" + withdrawLower + "】");
            }

            String host = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardNo="
                    + bankCard + "&cardBinCheck=true";
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet(host);
            HttpResponse response = httpClient.execute(request);
            String result = EntityUtils.toString(response.getEntity());
            System.out.println("检验银行卡号是否合法返回结果:" + result);
            JSONObject jsonObject = JSONObject.fromObject(result);

            if(!(Boolean) jsonObject.get("validated")){
                return R.error("银行卡信息错误");
            }

            if (capital != null && userWalletFee.compareTo(withdrawFee) >= 0) {
                SyscapitalCapitalWithdrawEntity capitalWithdrawEntity = new SyscapitalCapitalWithdrawEntity();
                capitalWithdrawEntity.setCapitalId(capital.getCapitalId());
                capitalWithdrawEntity.setCreatedTime(DateUtils.currentUnixTime());
                capitalWithdrawEntity.setWithdrawFee(withdrawFee);

                BigDecimal poundageFee = new BigDecimal(withdrawRate).multiply(withdrawFee);//提现手续费
                if (poundageFee.compareTo(withdrawFee) > 0) {
                    return R.error("提现手续费过高");
                }
                capitalWithdrawEntity.setPoundageFee(poundageFee);

                capitalWithdrawEntity.setActualFee(withdrawFee.subtract(poundageFee));
                capitalWithdrawEntity.setStatus(0);//待审核
                capitalWithdrawEntity.setUserId(userId);
                capitalWithdrawEntity.setWithdrawReason(reason);
                capitalWithdrawEntity.setBankCard(bankCard);
                capitalWithdrawEntity.setRealName(realName);
                syscapitalCapitalWithdrawService.save(capitalWithdrawEntity);
            } else {
                return R.error("提现余额不足");
            }
            return R.ok();
        } catch (Exception e) {
            logger.info("查询用户钱包流水记录异常", e);
            return R.error();
        }
    }

    @PostMapping("setPayPwd")
    @ApiOperation("设置支付密码")
    public R setPayPwd(@RequestHeader("token") String token, String payPwd, String mcode) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//加入购物车需要先登录
                return R.auth("请登录");
            }
            if (!mcode.equalsIgnoreCase(redisUtils.get(Constants.PAY_M_CODE + ":" + user.getMobile()))) {
                return R.error("验证码错误");
            }
            if (payPwd == null || !isPwdValid(payPwd)) {
                return R.error("密码格式错误！");
            }
            QueryWrapper<SyscapitalCapitalEntity> queryWrapper1 = new QueryWrapper<SyscapitalCapitalEntity>();
            queryWrapper1.eq("user_id", user.getUserId());
            SyscapitalCapitalEntity capital = syscapitalCapitalService.getOne(queryWrapper1);
            if (capital != null) {
                capital.setWalletPwd(BCrypt.hashpw(payPwd, BCrypt.gensalt()));
                syscapitalCapitalService.updateById(capital);
            }
            return R.ok();
        } catch (Exception e) {
            logger.info("查询用户钱包流水记录异常", e);
            return R.error();
        }
    }

    public boolean isPwdValid(String pwd) {
        String regex = "^([0-9]){6}$";
        return pwd.matches(regex);
    }
    @PostMapping("activateCard")
    @ApiOperation("激活充值卡")
    public R activateCard(@RequestHeader("token") String token, String cardNo, String password) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//加入购物车需要先登录
                return R.auth("请登录");
            }
            if(StringUtils.isBlank(cardNo)){
                return R.error("请输入卡号");
            }
            if(StringUtils.isBlank(password)){
                return R.error("请输入密码");
            }
            return sysrechargeCardService.recharge(user.getUserId(),cardNo,password);

        } catch (Exception e) {
            logger.info("激活充值卡异常", e);
            return R.error();
        }
    }

}
