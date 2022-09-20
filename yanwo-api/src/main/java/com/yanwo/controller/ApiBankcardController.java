package com.yanwo.controller;

import java.util.List;
import com.aliyun.opensearch.sdk.dependencies.org.apache.http.HttpResponse;
import com.aliyun.opensearch.sdk.dependencies.org.apache.http.client.HttpClient;
import com.aliyun.opensearch.sdk.dependencies.org.apache.http.client.methods.HttpGet;
import com.aliyun.opensearch.sdk.dependencies.org.apache.http.impl.client.DefaultHttpClient;
import com.aliyun.opensearch.sdk.dependencies.org.apache.http.util.EntityUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.entity.UserEntity;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.R;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.yanwo.entity.SysbankCardEntity;
import com.yanwo.service.SysbankCardService;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-05-21 15:04:16
 */
@RestController
@RequestMapping("card")
public class ApiBankcardController extends BaseController{
    private static final Logger log = LoggerFactory.getLogger(ApiBankcardController.class);

    @Autowired
    private SysbankCardService sysbankCardService;

    /**
     * 信息
     */
    @PostMapping("/list")
    @ApiOperation("获取用户银行卡列表")
    public R list(@RequestHeader("token") String token){
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {
                return R.auth("请登录");
            }
            QueryWrapper<SysbankCardEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id",user.getUserId());
            List<SysbankCardEntity> list = sysbankCardService.list(queryWrapper);
            return R.okput(list);
        } catch (Exception e) {
            log.info("获取用户银行卡列表发生异常", e);
            return R.error();
        }
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @ApiOperation("用户添加银行卡")
    public R save(@RequestHeader("token") String token,String bankCard,String realName){
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {
                return R.auth("请登录");
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

            SysbankCardEntity sysbankCardEntity = new SysbankCardEntity();
            sysbankCardEntity.setUserId(user.getUserId());
            sysbankCardEntity.setRealName(realName);
            sysbankCardEntity.setBankCard(bankCard);
            sysbankCardEntity.setCreateTime(DateUtils.currentUnixTime());
            sysbankCardService.save(sysbankCardEntity);
            return R.ok("添加成功");
        } catch (Exception e) {
            log.info("添加失败", e);
            return R.error();
        }
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("用户修改银行卡")
    public R update(@RequestHeader("token") String token,Integer cardId,String bankCard,String realName){
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {
                return R.auth("请登录");
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

            SysbankCardEntity sysbankCardEntity = sysbankCardService.getById(cardId);
            sysbankCardEntity.setUserId(user.getUserId());
            sysbankCardEntity.setRealName(realName);
            sysbankCardEntity.setBankCard(bankCard);
            sysbankCardEntity.setCreateTime(DateUtils.currentUnixTime());
            sysbankCardService.updateById(sysbankCardEntity);
            return R.ok("添加成功");
        } catch (Exception e) {
            log.info("添加失败", e);
            return R.error();
        }
    }

    /**
     * 详情
     */
    @PostMapping("/info")
    @ApiOperation("银行卡详情")
    public R update(Integer cardId){
        try {
            SysbankCardEntity sysbankCardEntity = sysbankCardService.getById(cardId);
            return R.ok().put("card",sysbankCardEntity);
        } catch (Exception e) {
            log.info("获取失败", e);
            return R.error();
        }
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(Integer cardId){
        try {
            sysbankCardService.removeById(cardId);
            return R.ok("删除成功");
        } catch (Exception e) {
            log.info("删除失败", e);
            return R.error();
        }
    }

}
