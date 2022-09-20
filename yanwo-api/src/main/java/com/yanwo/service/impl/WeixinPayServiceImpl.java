/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yanwo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.yanwo.Constant.Constants;
import com.yanwo.dao.EctoolsPaymentsDao;
import com.yanwo.dao.SystradeTradeDao;
import com.yanwo.entity.EctoolsPaymentsEntity;
import com.yanwo.entity.SyscapitalCapitalEntity;
import com.yanwo.entity.SystradeTradeEntity;
import com.yanwo.service.SyscapitalCapitalService;
import com.yanwo.service.WeixinPayService;
import com.yanwo.utils.Constant;
import com.yanwo.utils.JsonUtils;
import com.yanwo.utils.R;
import com.yanwo.utils.WxUtils.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Service("weixinPayService")
public class WeixinPayServiceImpl implements WeixinPayService {

	private static final Logger logger = LoggerFactory.getLogger(WeixinPayServiceImpl.class);

	@Value("${wexinpay.notify.url}")
	private String notify_url;
	@Value("${app_id}")
	private  String app_id;
	@Value("${API_KEY}")
	private  String API_KEY;
	@Value("${MCH_ID}")
	private  String MCH_ID;

	@Autowired
	private EctoolsPaymentsDao ectoolsPaymentsDao;
	@Autowired
	private SyscapitalCapitalService syscapitalCapitalService;
	/******************************************************************
	 *描述:统一下单
	 *说明:
	 *    除被扫支付场景以外，商户系统先调用该接口在微信支付服务后台生成预支付交易单，返回正确的预支付交易会话标识后再按扫码、
	 *    JSAPI、APP等不同场景生成交易串调起支付。
	 *参见:
	 *   https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_1
	 *****************************************************************/
	@Override
	public ResEntity unifiedOrder(PayOrder product){
//		EctoolsPaymentsEntity payment = ectoolsPaymentsDao.selectById(product.getOutTradeNo());

		int timeStamp=(int)((new Date()).getTime()/1000);
		ResEntity resEntity = new ResEntity();
		//获取用户openID(JSAPI支付必须传openid) TODO
		SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
		packageParams.put("mch_id", MCH_ID);// 商户号
		packageParams.put("body", Constants.PAY_TITLE);// 商品描述
		packageParams.put("out_trade_no", product.getOutTradeNo());// 商户订单号
//		String totalAmount = payment.getCurMoney().toString();
//		if("2".equals(product.getAttach())){
//			//余额+微信支付（说明余额不足）
//			QueryWrapper<SyscapitalCapitalEntity> queryWrapper = new QueryWrapper<>();
//			queryWrapper.eq("user_id",payment.getUserId());
//			SyscapitalCapitalEntity capital = syscapitalCapitalService.getOne(queryWrapper);
//			if(capital != null){
//				totalAmount = payment.getCurMoney().subtract(capital.getTotalRecharge()).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
//			}
//		}
		String totalAmount = product.getTotalAmount();
		totalAmount =  CommonUtil.mul(totalAmount, "100").intValue()+"";
		packageParams.put("total_fee", totalAmount);// 总金额(单位为分 整数)
		packageParams.put("spbill_create_ip", product.getSpbillCreateIp());// 发起人IP地址
		packageParams.put("notify_url", notify_url);// 回调地址
		packageParams.put("trade_type", "JSAPI");// 交易类型
		String nonceStr = PayCommonUtil.createNoncestr();
		packageParams.put("appid",app_id);
		packageParams.put("nonce_str",nonceStr);
		packageParams.put("openid", product.getOpenid());

		packageParams.put("attach",product.getAttach());
		String sign = PayCommonUtil.createSign("UTF-8", packageParams,API_KEY);
		packageParams.put("sign", sign);// 签名

		String requestXML = XMLUtil.getRequestXml(packageParams);
		String resXml = HttpUtil.postData(ConfigUtil.UNIFIED_ORDER_URL, requestXML);
		logger.info("resXml-----:"+resXml);
		Map map = null;
		try {
			map = XMLUtil.doXMLParse(resXml);
			logger.info("map-----:"+ JsonUtils.objectToJson(map));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String returnCode = (String) map.get("return_code");
		String returnMsg = (String) map.get("return_msg");
		StringBuffer url = new StringBuffer();

		if("SUCCESS".equals(returnCode)){
			String resultCode = (String) map.get("result_code");
			String errCodeDes = (String) map.get("err_code_des");
			if("SUCCESS".equals(resultCode)){

				resEntity.setCode("success");
				resEntity.setMessage("微信统一下单成功");

				SortedMap<Object, Object> finalpackage = genJSAPIReturnMap(map,timeStamp);
				String appId=(String)packageParams.get("appid");

				finalpackage.put("appId",appId);
				finalpackage.put("timeStamp",timeStamp);
				finalpackage.put("signType","MD5");
				resEntity.setContent(JsonUtils.objectToJson(finalpackage));
			}else{
				resEntity.setCode("fail");
				resEntity.setMessage("请联系客服");
				resEntity.setMessage(returnMsg);
				logger.info("订单号:{}错误信息:{}",product.getOutTradeNo(),errCodeDes);
			}
		}else{
			resEntity.setCode("fail");
			resEntity.setMessage("请联系客服");
			resEntity.setMessage(returnMsg);
			logger.info("订单号:{}错误信息:{}",product.getOutTradeNo(),returnMsg);
		}
		logger.info("resEntity支付结果"+ new Gson().toJson(resEntity));
		return resEntity;
	}
	private  SortedMap<Object, Object> genJSAPIReturnMap(Map<String, String> map,Integer temp){
		try {
			SortedMap<Object, Object> parameterMap = new TreeMap<Object, Object>();
			parameterMap.put("tradetype", "JSAPI");
			logger.info("prepayid="+map.get("prepay_id"));
			parameterMap.put("prepayid", map.get("prepay_id"));
			parameterMap.put("codeurl", map.get("code_url"));

			SortedMap<Object, Object> parameterMap2 = new TreeMap<Object, Object>();
			parameterMap2.put("appId",app_id);
			parameterMap2.put("timeStamp", temp+"");
			String noncestr=PayCommonUtil.createNoncestr()+"";
			parameterMap2.put("nonceStr", noncestr);
			System.out.println(noncestr);
			parameterMap2.put("package", "prepay_id="+ map.get("prepay_id"));
			parameterMap2.put("signType", "MD5");
			String sign = PayCommonUtil.createSign("UTF-8", parameterMap2,API_KEY);
			parameterMap.put("package","prepay_id="+ map.get("prepay_id"));
			parameterMap.put("nonceStr",noncestr);
			parameterMap.put("sign", sign);
			return parameterMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
