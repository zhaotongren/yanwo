package com.yanwo.modules.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.opensearch.sdk.dependencies.com.google.common.collect.Maps;
import com.yanwo.Constant.Constants;
import com.yanwo.dao.EctoolsPaymentsDao;
import com.yanwo.dao.EctoolsTradePaybillDao;
import com.yanwo.dao.UserDao;
import com.yanwo.entity.*;
import com.yanwo.modules.service.*;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.OpenSearchUtil;
import com.yanwo.utils.R;
import com.yanwo.utils.WxUtils.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.utils.PageUtils;
import com.yanwo.common.utils.Query;

import com.yanwo.dao.SysaftersalesRefundsDao;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;


@Service("sysaftersalesRefundsService")
public class SysaftersalesRefundsServiceImpl extends ServiceImpl<SysaftersalesRefundsDao, SysaftersalesRefundsEntity> implements SysaftersalesRefundsService {
    private static final Logger logger = LoggerFactory.getLogger(SysaftersalesRefundsServiceImpl.class);

    @Autowired
    private SysaftersalesRefundsDao sysaftersalesRefundsDao;
    @Autowired
    private EctoolsTradePaybillDao ectoolsTradePaybillDao;
    @Autowired
    private EctoolsPaymentsDao paymentsDao;
    @Autowired
    private SystradeOrderService systradeOrderService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private SystradeTradeService systradeTradeService;
    @Autowired
    private SyscapitalCapitalService syscapitalCapitalService;
    @Autowired
    private SyscapitalCapitalIntegralService syscapitalCapitalIntegralService;
    @Autowired
    private SysitemItemService sysitemItemService;
    @Autowired
    private SysitemSkuService sysitemSkuService;
    @Autowired
    private SyscapitalCapitalDetailService capitalDetailService;

    @Value("${app_id}")
    private  String app_id;
    @Value("${API_KEY}")
    private  String API_KEY;
    @Value("${MCH_ID}")
    private  String MCH_ID;
    @Value("${CERT_PATH}")
    private String CERT_PATH;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<SysaftersalesRefundsEntity> queryWrapper = new QueryWrapper<>();

        if(params.containsKey("tid") && StringUtils.isNotBlank(params.get("tid").toString())){
            queryWrapper.like("tid",params.get("tid").toString());
        }

        if(params.containsKey("status") && StringUtils.isNotBlank(params.get("status").toString())){
            queryWrapper.eq("status",params.get("status").toString());
        }
        queryWrapper.orderByDesc("created_time");
        IPage<SysaftersalesRefundsEntity> page = this.page(
                new Query<SysaftersalesRefundsEntity>().getPage(params),
                queryWrapper
        );
        page.setRecords(convertList(page.getRecords()));
        return new PageUtils(page);
    }

    public List convertList(List<SysaftersalesRefundsEntity>  refundsList) {
        List<ModelMap> list = new ArrayList<ModelMap>();
        if ( refundsList != null &&  refundsList.size() > 0) {
            for (SysaftersalesRefundsEntity  refunds : refundsList) {
                ModelMap map = buildModelList(refunds);
                list.add(map);
            }
        }
        return list;
    }

    public ModelMap buildModelList(SysaftersalesRefundsEntity  refunds) {
        ModelMap model = new ModelMap();
        model.put("refundsId",refunds.getRefundsId());
        model.put("tid",refunds.getTid());
        model.put("oid",refunds.getOid());
        model.put("status",refunds.getStatus());
        model.put("payment",refunds.getPayment());
        model.put("totalPrice",refunds.getTotalPrice());
        model.put("refundFee",refunds.getRefundFee());
        model.put("postFee",refunds.getPostFee());
        model.put("createdTime",refunds.getCreatedTime());
        model.put("sendbackData",refunds.getSendbackData());
        model.put("refundsType",refunds.getRefundsType());
        model.put("rechargeFee",refunds.getRechargeFee());
        try {
            model.put("userName",URLDecoder.decode(userDao.selectById(refunds.getUserId()).getNickName(),"utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return model;
    }


    @Override
    public R refund(SysaftersalesRefundsEntity refundsEntity, HttpServletRequest request){
        SystradeTradeEntity tradeEntity = systradeTradeService.getById(Long.valueOf(refundsEntity.getTid()));
        if(tradeEntity.getType() == 1){//????????????
            //???????????????
            refundsEntity.setStatus("5");
            refundsEntity.setRefundFinishTime(DateUtils.currentUnixTime());
            refundsEntity.setModifiedTime(DateUtils.currentUnixTime());
            updateById(refundsEntity);
            //??????????????????
            SystradeOrderEntity orderEntity = systradeOrderService.getById(Long.valueOf(refundsEntity.getOid()));
            orderEntity.setAftersalesStatus("5");
            systradeOrderService.updateById(orderEntity);
            //????????????
            SysitemSkuEntity skuEntity = sysitemSkuService.getById(orderEntity.getSkuId());
            skuEntity.setStore(skuEntity.getStore() + orderEntity.getNum());
            sysitemSkuService.updateById(skuEntity);
            //????????????
            SysitemItemEntity itemEntity = sysitemItemService.getById(orderEntity.getItemId());
            itemEntity.setSoldNum(itemEntity.getSoldNum() - orderEntity.getNum());
            sysitemItemService.updateById(itemEntity);
            //??????opensearch??????
            updateItemCount(itemEntity);
            //????????????
            QueryWrapper<SyscapitalCapitalEntity> qw = new QueryWrapper();
            qw.eq("user_id",refundsEntity.getUserId());
            SyscapitalCapitalEntity capitalEntity = syscapitalCapitalService.getOne(qw);
            if(capitalEntity != null) {
                //????????????????????????
                SyscapitalCapitalIntegralEntity integralEntity = new SyscapitalCapitalIntegralEntity();
                integralEntity.setIntegralFee(orderEntity.getTotalFee());
                integralEntity.setIntegralType(3);
                integralEntity.setCapitalId(capitalEntity.getCapitalId());
                integralEntity.setUserId(capitalEntity.getUserId());
                integralEntity.setTid(refundsEntity.getTid());
                integralEntity.setOid(Long.valueOf(refundsEntity.getOid()));
                integralEntity.setCreatedTime(DateUtils.currentUnixTime());
                integralEntity.setStatus(1);
                syscapitalCapitalIntegralService.save(integralEntity);
                //???????????????
                capitalEntity.setTotalIntegral(capitalEntity.getTotalIntegral().add(orderEntity.getTotalFee()));
                syscapitalCapitalService.updateById(capitalEntity);
            }
        }else{//????????????
            //????????????
            String code = "success";
            QueryWrapper<EctoolsTradePaybillEntity> qw = new QueryWrapper();
            qw.eq("tid",refundsEntity.getTid());
            qw.eq("status","succ");
            List<EctoolsTradePaybillEntity> paybills = ectoolsTradePaybillDao.selectList(qw);
            if(paybills != null && paybills.size() > 0) {
                EctoolsTradePaybillEntity paybillEntity = paybills.get(0);
                PayOrder product = new PayOrder();
                product.setSpbillCreateIp(request.getRemoteHost());
                product.setOutTradeNo(paybillEntity.getPaymentId());
                product.setOutRefundNo(refundsEntity.getRefundBn());
                product.setTotalAmount(tradeEntity.getPayment().subtract(tradeEntity.getRechargeFee()).toString());
                product.setRefundFee(refundsEntity.getRefundFee().toString());
                ResEntity resEntity = refund(product);
                code = resEntity.getCode();
            }
            if(Constants.SUCCESS.equals(code)) {//????????????
                //???????????????
                refundsEntity.setStatus("5");
                refundsEntity.setRefundFinishTime(DateUtils.currentUnixTime());
                refundsEntity.setModifiedTime(DateUtils.currentUnixTime());
                updateById(refundsEntity);
                //??????????????????
                SystradeOrderEntity orderEntity = systradeOrderService.getById(Long.valueOf(refundsEntity.getOid()));
                orderEntity.setAftersalesStatus("5");
                systradeOrderService.updateById(orderEntity);
                //????????????
                SysitemSkuEntity skuEntity = sysitemSkuService.getById(orderEntity.getSkuId());
                skuEntity.setStore(skuEntity.getStore() + refundsEntity.getRefundNum());
                sysitemSkuService.updateById(skuEntity);
                //????????????
                SysitemItemEntity itemEntity = sysitemItemService.getById(orderEntity.getItemId());
                itemEntity.setSoldNum(itemEntity.getSoldNum() - refundsEntity.getRefundNum());
                sysitemItemService.updateById(itemEntity);
                //??????opensearch??????
                updateItemCount(itemEntity);
                //??????????????????
                if (refundsEntity.getRechargeFee().compareTo(BigDecimal.ZERO) == 1){
                    //??????????????????
                    QueryWrapper<SyscapitalCapitalEntity> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("user_id",refundsEntity.getUserId());
                    SyscapitalCapitalEntity capital = syscapitalCapitalService.getOne(queryWrapper);
                    //????????????
                    SyscapitalCapitalDetailEntity capitalDetail = new SyscapitalCapitalDetailEntity();
                    capitalDetail.setCapitalId(capital.getCapitalId());
                    capitalDetail.setCapitalType(9);
                    capitalDetail.setUserId(capital.getUserId());
                    capitalDetail.setCapitalFee(refundsEntity.getRechargeFee());
                    capitalDetail.setCreatedTime(DateUtils.currentUnixTime());
                    capitalDetail.setTid(tradeEntity.getTid());
                    capitalDetail.setOid(orderEntity.getOid());
                    capitalDetailService.save(capitalDetail);
                    //????????????
                    capital.setTotalRecharge(capital.getTotalRecharge().add(refundsEntity.getRechargeFee()));
                    capital.setTotalCapital(capital.getTotalCapital().add(refundsEntity.getRechargeFee()));
                    syscapitalCapitalService.updateById(capital);
                }
            }


        }

        return R.ok();
    }
    private void updateItemCount(SysitemItemEntity item){
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
    public List<Map>  find(Map params) {
        return sysaftersalesRefundsDao.find(params);
    }

    private ResEntity refund(PayOrder product) {
        logger.info("????????????{}????????????",product.getOutTradeNo());
        ResEntity resEntity = new ResEntity();
        Map map = null;
        try {

            SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
            //????????????ID	appid  ?????????	mch_id ???????????????	nonce_str
            String nonceStr = PayCommonUtil.createNoncestr();
            packageParams.put("appid", app_id);// ????????????ID
            packageParams.put("mch_id", MCH_ID);// ?????????
            packageParams.put("nonce_str", nonceStr);// ???????????????
            //???????????????	out_trade_no  ??????????????????	out_refund_no
            packageParams.put("out_trade_no", product.getOutTradeNo());// ???????????????
            packageParams.put("out_refund_no", product.getOutRefundNo());//??????????????????
            String totalAmount = product.getTotalAmount();
            totalAmount =  CommonUtil.mul(totalAmount, "100").intValue()+"";
            String refundFee = product.getRefundFee();
            refundFee =  CommonUtil.mul(refundFee, "100").intValue()+"";
            packageParams.put("total_fee", totalAmount);// ?????????
            packageParams.put("refund_fee", refundFee);//????????????
            packageParams.put("op_user_id", MCH_ID);//???????????????, ??????????????????

            String sign = PayCommonUtil.createSign("UTF-8", packageParams, API_KEY);
            packageParams.put("sign", sign);// ??????
            String requestXML = XMLUtil.getRequestXml(packageParams);
            String weixinPost = ClientCustomSSL.doRefund(ConfigUtil.REFUND_URL, requestXML,CERT_PATH,MCH_ID).toString();
            map = XMLUtil.doXMLParse(weixinPost);

            String returnCode = (String) map.get("return_code");
            if("SUCCESS".equals(returnCode)){
                String resultCode = (String) map.get("result_code");
                if("SUCCESS".equals(resultCode)){
                    resEntity.setCode(Constants.SUCCESS);
                    resEntity.setMessage("????????????");
                    resEntity.setContent(JSON.toJSONString(map));
                    logger.info("??????:"+product+"????????????:"+weixinPost);
                }else{
                    resEntity.setCode(Constants.FAIL);
                    resEntity.setContent(JSON.toJSONString(map));
                    logger.info("?????????"+product+"????????????:"+weixinPost);
                }
            }else{
                resEntity.setCode(Constants.FAIL);
                resEntity.setContent(JSON.toJSONString(map));
                logger.info("????????????"+product+"????????????:"+weixinPost);
            }
        } catch (Exception e) {
            resEntity.setCode(Constants.FAIL);
            resEntity.setContent("??????"+e.getMessage());
            logger.error("????????????"+product+"????????????:",e);
        }
        return resEntity;
    }

}
