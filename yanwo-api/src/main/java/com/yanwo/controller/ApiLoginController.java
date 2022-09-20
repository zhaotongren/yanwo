/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.yanwo.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.Constant.Constants;
import com.yanwo.annotation.Login;
import com.yanwo.dao.SysopenIdDao;
import com.yanwo.entity.SysopenIdEntity;
import com.yanwo.entity.UserEntity;
import com.yanwo.entity.UserGradeEntity;
import com.yanwo.service.UserApplyService;
import com.yanwo.service.UserGradeService;
import com.yanwo.utils.*;
import com.yanwo.validator.ValidatorUtils;
import com.yanwo.form.LoginForm;
import com.yanwo.service.TokenService;
import com.yanwo.service.UserService;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.HSSFAnchor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.security.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 登录接口
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/api")
@Api(tags = "登录接口")
public class ApiLoginController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ApiLoginController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserGradeService userGradeService;
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    private SysopenIdDao sysopenIdDao;


    @Value("${app_secret}")
    private String app_secret;
    @Value("${app_id}")
    private String appid;

    /**
     * 小程序授权登录s
     *
     * @param code
     * @return
     */
    //获取openid
    @ApiOperation("登录")
    @RequestMapping("/wxLogin")
    public R getWxUserOpenid(String token, String code, String nickName, String headPic, String sex,String encryptedData, String iv, Integer parid) {
        try {
            Map map = new HashMap();
            UserEntity user = getUserFromTokenApp(token);
            //已经授权登录了
            if (user != null) {
                map.put("openId", user.getWxOpenId());
                map.put("userid", user.getUserId());
                map.put("phone", user.getMobile());
                return R.okput(map);
            }
            //拼接url 获取openid
            StringBuilder url = new StringBuilder("https://api.weixin.qq.com/sns/jscode2session?");
            url.append("appid=");//appid设置
            url.append(appid);
            url.append("&secret=");//secret设置
            url.append(app_secret);
            url.append("&js_code=");//code设置
            url.append(code);
            url.append("&grant_type=authorization_code");
            String openId = null;
            String sessionKey = null;
            String unionid = null;

            HttpClient client = HttpClientBuilder.create().build();//构建一个Client
            HttpGet get = new HttpGet(url.toString());    //构建一个GET请求
            HttpResponse response = client.execute(get);//提交GET请求
            HttpEntity result = response.getEntity();//拿到返回的HttpResponse的"实体"
            String content = EntityUtils.toString(result);
            JSONObject res = JSONObject.parseObject(content);//把信息封装为json
            System.out.println("小程序授权获取用户唯一标识结果======>>" + res);
            openId = res.getString("openid");
            sessionKey = res.getString("session_key");
            unionid = res.getString("unionid");
            map.put("openId", openId);
            map.put("sessionKey", sessionKey);
            map.put("unionid", unionid!=null?unionid:"-1");
            //查询数据是否存在
            QueryWrapper<UserEntity> queryWrapper = new QueryWrapper();
            queryWrapper.eq("wx_open_id", openId);
            UserEntity userEntity = userService.getOne(queryWrapper);
/*
            if(null!=unionid){
                if(null!=userEntity){
                    userEntity.setUnionid(unionid);

                    SysopenIdEntity sysopenIdEntity = sysopenIdDao.selectById(unionid);
                    if(sysopenIdEntity!=null){
                        userEntity.setPublicOpenId(sysopenIdEntity.getOpenId());
                    }
                    userService.updateById(userEntity);
                }
            }
*/


            //用户不存在
            if (userEntity == null) {
                if (openId != null) {
                    //获取电话号码
                    String phoneNumber = getPhoneNumber(encryptedData, sessionKey, iv);
                    UserEntity userEntity1 = new UserEntity();
                    userEntity1.setNickName(URLEncoder.encode(nickName, "utf-8"));
                    userEntity1.setCerHandPic(headPic);
                    userEntity1.setWxOpenId(openId);
                    userEntity1.setMobile(phoneNumber);
                    userEntity1.setMemberShip("0");
                    userEntity1.setSex(sex);
                    userEntity1.setCreateTime(DateUtils.currentUnixTime());
                    userEntity1.setUnionid(unionid);
/*
                    SysopenIdEntity sysopenIdEntity = sysopenIdDao.selectById(unionid);
                    if(sysopenIdEntity!=null){
                        userEntity.setPublicOpenId(sysopenIdEntity.getOpenId());
                    }
*/
                    userService.save(userEntity1);

                    UserGradeEntity userGradeEntity = new UserGradeEntity();
                    userGradeEntity.setUserId(userEntity1.getUserId());
                    userGradeEntity.setCreateTime(DateUtils.currentUnixTime());
                    map.put("userid", userEntity1.getUserId());
                    if (StringUtils.isNotBlank(userEntity1.getMobile())) {
                        map.put("phone", userEntity1.getMobile());
                    }
                    userEntity1.setNickName(URLDecoder.decode(userEntity1.getNickName(), "utf-8"));
                    redisUtils.set(Constants.REDIS_TOKEN_APP_SESSION_PREFIX + openId, JsonUtils.objectToJson(userEntity1),-1);
                    if (parid != null) {
                        //推荐人身份
                        UserEntity parUser = userService.getById(parid);
                        if(parUser != null && "2".equals(parUser.getMemberShip())){
                            //直接推荐人
                            userGradeEntity.setParIdTwo(parid);
                            userGradeEntity.setTwoUserId(parid);
                            //间接推荐人
                            UserGradeEntity userGradeEntity1 = userGradeService.getById(parid);
                            if (userGradeEntity1.getTwoUserId() != null && !userGradeEntity1.getTwoUserId().equals("")) {
                                userGradeEntity.setParIdOne(userGradeEntity1.getTwoUserId());
                                userGradeEntity.setFirstUserId(userGradeEntity1.getTwoUserId());
                            }
                        }
                    }
                    userGradeService.save(userGradeEntity);
                }
            } else {
                map.put("userid", userEntity.getUserId());
                if (StringUtils.isNotBlank(userEntity.getMobile())) {
                    map.put("phone", userEntity.getMobile());
                }
                userEntity.setNickName(URLDecoder.decode(userEntity.getNickName(), "utf-8"));

                redisUtils.set(Constants.REDIS_TOKEN_APP_SESSION_PREFIX + userEntity.getWxOpenId(), JsonUtils.objectToJson(userEntity),-1);
                up(nickName,  userEntity);
            }
            System.out.println(R.okput(map));
            return R.okput(map);
        } catch (Exception e) {
            logger.info("授权登录异常",e);
            return R.error("授权失败！");
        }
    }

    private void up(String nickName, UserEntity userEntity) throws UnsupportedEncodingException {
        if (!userEntity.getNickName().equals(nickName)){
            userEntity.setNickName(URLEncoder.encode(nickName,"utf-8"));
            userService.updateById(userEntity);
        }
    }

    private String getPhoneNumber(String encryptedData, String session_key, String iv) {
        System.out.println("encryptedData==========================="+encryptedData);
        System.out.println("session_key==========================="+session_key);
        System.out.println("iv==========================="+iv);
//        try {
//            encryptedData = URLEncoder.encode(encryptedData, "UTF-8").replace("%3D", "=").replace("%2F", "/");
//            encryptedData = URLDecoder.decode(encryptedData, "UTF-8");
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        // 被加密的数据
        byte[] dataByte = Base64.decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decode(session_key);
        // 偏移量
        byte[] ivByte = Base64.decode(iv);
        try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                System.out.println("3==========================="+result);
                Map resJson = JsonUtils.jsonToMap(result);
                if(resJson.get("phoneNumber") != null){
                    return resJson.get("phoneNumber").toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Login
    @PostMapping("logout")
    @ApiOperation("退出")
    public R logout(@ApiIgnore @RequestAttribute("userId") long userId) {
        tokenService.expireToken(userId);
        return R.ok();
    }

    @PostMapping("getCode")
    @ApiOperation("发送验证码")
    public R getmcodeapp(@RequestParam String mobile) {
        try {
            if (!checkIsMobileValid(mobile)) {
                return R.error("请输入正确的手机号码！");
            }
            //先查询验证的次数
            String verificationnum = redisUtils.get(Constants.VERIFICATION_NUM + mobile);
            String smsCode = "";
            if (verificationnum == null || verificationnum == "") {
                logger.info("手机号:" + mobile + ";验证码使用次数：" + 1);
                redisUtils.set(Constants.VERIFICATION_NUM + mobile, "1", 60 * 60 * 5);
            } else {
                int num = Integer.parseInt(verificationnum);
                if (num == -1) {
                    logger.info("手机号:" + mobile + "超出限制次数..");
                    return R.error("超出验证码次数限制！");
                } else {
                    smsCode = SendMessageUtil.generateSmsCode() + "";
                    SendMessageUtil.SendMessage(mobile, smsCode);
                    num++;
                    logger.info("手机号:" + mobile + ";验证码使用次数：" + num);
                    redisUtils.set(Constants.VERIFICATION_NUM + mobile, num + "", 60 * 60 * 5);
                }
            }
            logger.info("手机号:" + mobile + "注册发送验证码:" + smsCode);
            redisUtils.set(Constants.VERIFY_M_CODE + mobile, smsCode, 15 * 60);
            return R.ok("手机验证码已发送");
        } catch (Exception e) {
            logger.info("获取验证码发生异常", e);
            return R.error("系统异常，请联系客服处理！");
        }
    }

    @PostMapping("toSetMobile")
    @ApiOperation("绑定/修改手机号")
    public R toSetMobile(@RequestHeader("token") String token, @RequestParam String mobile, String code) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//加入购物车需要先登录
                return R.auth("请登录");
            }
            if (StringUtils.isBlank(mobile)) {
                return R.error("系统异常，请联系客服处理");
            }
            if (StringUtils.isBlank(code)) {
                return R.error("验证码不能为空");
            }
            String mcode = redisUtils.get(Constants.VERIFY_M_CODE + mobile);
            if (!code.equals(mcode)) {
                return R.error("验证码错误");
            } else {
                user.setMobile(mobile);
                userService.updateById(user);
                redisUtils.set(Constants.REDIS_TOKEN_APP_SESSION_PREFIX + user.getWxOpenId(), JsonUtils.objectToJson(user));
            }
            return R.ok();
        } catch (Exception e) {
            return R.error("系统异常，请联系客服处理！");
        }
    }

    public boolean checkIsMobileValid(String mobile) {
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9|1]))\\d{8}$";
        return mobile.matches(regex);
    }

    @PostMapping("sendPaycode")
    @ApiOperation("设置支付密码-发送验证码")
    public R getmcode(@RequestHeader String token) {
        UserEntity user = getUserFromTokenApp(token);
        if (user == null) {//加入购物车需要先登录
            return R.auth("请登录");
        }
        String smsCode = SendMessageUtil.generateSmsCode() + "";
        SendMessageUtil.SendMessage(user.getMobile(), smsCode);
        redisUtils.set(Constants.PAY_M_CODE + ":" + user.getMobile(), smsCode, 15 * 60);
        return R.ok();
    }

    /**
     * 获取unionid
     *
     * @param code
     * @return
     */
    @ApiOperation("获取unionid")
    @RequestMapping("/getUnionid")
    public R getUnionid(String code) {
        try {
            Map map = new HashMap();
            //拼接url 获取openid
            StringBuilder url = new StringBuilder("https://api.weixin.qq.com/sns/jscode2session?");
            url.append("appid=");//appid设置
            url.append(appid);
            url.append("&secret=");//secret设置
            url.append(app_secret);
            url.append("&js_code=");//code设置
            url.append(code);
            url.append("&grant_type=authorization_code");
            String openId = null;
            String sessionKey = null;
            String unionid = null;

            HttpClient client = HttpClientBuilder.create().build();//构建一个Client
            HttpGet get = new HttpGet(url.toString());    //构建一个GET请求
            HttpResponse response = client.execute(get);//提交GET请求
            HttpEntity result = response.getEntity();//拿到返回的HttpResponse的"实体"
            String content = EntityUtils.toString(result);
            JSONObject res = JSONObject.parseObject(content);//把信息封装为json
            System.out.println("小程序授权获取用户唯一标识结果======>>" + res);
            openId = res.getString("openid");
            sessionKey = res.getString("session_key");
            unionid = res.getString("unionid");
            map.put("openId", openId);
            map.put("sessionKey", sessionKey);
            map.put("unionid", unionid!=null?unionid:"-1");
            //查询数据是否存在
            QueryWrapper<UserEntity> queryWrapper = new QueryWrapper();
            queryWrapper.eq("wx_open_id", openId);
            UserEntity userEntity = userService.getOne(queryWrapper);
            if(null==userEntity.getUnionid()){
                userEntity.setUnionid(unionid);

                SysopenIdEntity sysopenIdEntity = sysopenIdDao.selectById(unionid);
                if(sysopenIdEntity!=null){
                    userEntity.setPublicOpenId(sysopenIdEntity.getOpenId());
                }
                userService.updateById(userEntity);
            }
            return R.okput(map);
        } catch (Exception e) {
            logger.info("授权登录异常",e);
            return R.error("授权失败！");
        }
    }

}
