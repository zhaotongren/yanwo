package com.yanwo.controller;

import com.google.gson.Gson;
import com.yanwo.entity.EctoolsPaymentsEntity;
import com.yanwo.service.EctoolsPaymentsService;
import com.yanwo.service.SystradeTradeService;
import com.yanwo.service.WeixinPayService;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.JsonUtils;
import com.yanwo.utils.WxUtils.PayOrder;
import com.yanwo.utils.WxUtils.ResEntity;
import com.yanwo.utils.WxUtils.WXPayUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/wxpay")
public class WxPayController {

    private Logger log = LoggerFactory.getLogger(WxPayController.class);

    @Autowired
    SystradeTradeService systradeTradeService;
    @Autowired
    EctoolsPaymentsService paymentService;
    @Autowired
    WeixinPayService weixinPayService;

    @Value("${API_KEY}")
    private  String API_KEY;
    @Value("${wexinpay.notify.url}")
    private String notify_url;


    @PostMapping("/jsapipay")
    @ApiOperation("微信支付")
    @ResponseBody
    public String jsapipay(PayOrder product, ModelMap map, HttpServletRequest request, HttpServletResponse response) {
        log.info("微信内部调用微信支付"+product+";ip:"+request.getHeader("X-Real-IP")+"-"+request.getRemoteHost()+":"+request.getRemotePort());
        product.setTradeType("JSAPI");
        product.setSpbillCreateIp(request.getRemoteHost());
        ResEntity resEntity  =  weixinPayService.unifiedOrder(product);
        return JsonUtils.objectToJson(resEntity);
    }

    @RequestMapping("/notify")
    @ApiOperation("微信支付回调")
    public void notify(HttpServletRequest request, HttpServletResponse response){
        try {
            log.info("-------------微信支付回调开始-----------");
            System.out.println("-------------微信支付回调开始-----------");
            InputStream inputStream = request.getInputStream();
            StringBuffer sb = new StringBuffer();
            String s;
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while ((s = in.readLine()) != null) {
                sb.append(s);
            }
            in.close();
            inputStream.close();
            String resXml = "";
            Map<String, String> responseMap= WXPayUtil.xmlToMap(sb.toString());
            log.info("支付结果内容(Map)："+ responseMap.toString());
            String PaymentId=responseMap.get("out_trade_no");//PaymentId
            log.info("PaymentId："+PaymentId);
            if(WXPayUtil.isSignatureValid(responseMap, API_KEY)){
                log.info("微信支付-签名验证成功");
                if (Objects.equals("SUCCESS", responseMap.get("return_code"))) {
                    log.info("微信支付-支付成功");
                    /**** 业务逻辑****/
                    try {
                        String payType=responseMap.get("attach");//标识  1微信支付   2微信+余额支付
                        //判断支付金额与订单金额是否相等
                        BigDecimal b=new BigDecimal(responseMap.get("total_fee")+"");//实际支付金额
                        EctoolsPaymentsEntity ectoolsPayments=paymentService.getById(PaymentId);
                        if("1".equals(payType)){
                            if(b.compareTo(ectoolsPayments.getMoney().multiply(new BigDecimal("100")))!=0){
                                log.info("实际支付金额："+b.toString());
                                log.info("订单金额："+ectoolsPayments.getMoney().toString());
                                log.info("-----------回调支付金额与订单金额不等-----------");
                                return;
                            }
                            else{
                                log.info("订单金额与实际支付金额相等："+ectoolsPayments.getMoney()+"元");
                            }
                        }

                        String timeEnd = (String) responseMap.get("time_end");
                        Date date = null;
                        if(timeEnd==null) {
                            date = new Date();
                        }else {
                            date = DateUtils.getDateByStr(timeEnd);
                        }
                        int i = 0;
                        if("2".equals(payType)){
                            i = paymentService.paymentPaySyntony(PaymentId, date,payType, b.divide(new BigDecimal("100")));
                        }else{
                            i = paymentService.paymentPaySyntony(PaymentId, date);
                        }
                        if(i==1){
                            log.info("-----------支付回调成功-----------");
                            resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
                        }else{
                            log.info("-----------支付回调失败-----------");
                            resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[后台处理异常]]></return_msg>" + "</xml> ";
                        }
                    }
                    catch (Exception e){
                        log.info("微信回调处理业务逻辑发生异常",e);
                        log.info("-----------处理业务逻辑失败-----------");
                        resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[后台处理异常]]></return_msg>" + "</xml> ";
                    }
                }else {
                    log.info("-----------微信支付-支付失败-----------");
                    resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
                }
            }else{
                log.info("-----------微信支付-验证签名失败-----------");
                resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[验签失败]]></return_msg>" + "</xml> ";
            }
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
            out.write(resXml.getBytes());
            out.flush();
            out.close();
        }
        catch (Exception e){
            log.info("微信回调发生异常",e);
            log.info("-----------微信支付-回调发生异常-----------");
        }
    }
}
