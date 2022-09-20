package com.yanwo.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.opensearch.sdk.dependencies.com.google.common.collect.Maps;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanwo.Constant.Constants;
import com.yanwo.entity.*;
import com.yanwo.service.*;
import com.yanwo.utils.*;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.WxUtils.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yanwo.dao.SysaftersalesRefundsDao;
import org.springframework.ui.ModelMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@Service("sysaftersalesRefundsService")
public class SysaftersalesRefundsServiceImpl extends ServiceImpl<SysaftersalesRefundsDao, SysaftersalesRefundsEntity> implements SysaftersalesRefundsService {
    private static final Logger logger = LoggerFactory.getLogger(SysaftersalesRefundsServiceImpl.class);
    @Autowired
    private SystradeTradeService systradeTradeService;
    @Autowired
    protected RedisUtils redisUtils;
    @Autowired
    private SysaftersalesRefundsService sysaftersalesRefundsService;
    @Resource
    private SysaftersalesRefundsDao sysaftersalesRefundsDao;
    @Autowired
    private SystradeOrderService systradeOrderService;
    @Autowired
    private SysDictService sysDictService;
    @Autowired
    private EctoolsTradePaybillService ectoolsTradePaybillService;
    @Autowired
    private EctoolsPaymentsService paymentsService;
    @Autowired
    private SysitemItemService sysitemItemService;
    @Autowired
    private SysitemSkuService sysitemSkuService;
    @Autowired
    private SyscapitalCapitalService syscapitalCapitalService;
    @Autowired
    private SyscapitalCapitalIntegralService syscapitalCapitalIntegralService;
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
    public PageUtils queryPage(int userId, int currPage) {
        QueryWrapper<SysaftersalesRefundsEntity> queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id",userId);
        queryWrapper.orderByDesc("created_time");
        Page<SysaftersalesRefundsEntity> pageW = new Page();
        pageW.setSize(10);
        pageW.setCurrent(currPage);
        IPage iPage =page(pageW, queryWrapper);
        iPage.setRecords(convertList(iPage.getRecords()));
        return new PageUtils(iPage);
    }
    public List convertList(List<SysaftersalesRefundsEntity>  refundsList) {
        List<ModelMap> list = new ArrayList<ModelMap>();
        if ( refundsList != null &&  refundsList.size() > 0) {
            for (SysaftersalesRefundsEntity refundsEntity : refundsList) {
                ModelMap map = buildModelList(refundsEntity);
                list.add(map);
            }
        }
        return list;
    }
    public ModelMap buildModelList(SysaftersalesRefundsEntity refundsEntity) {
        SystradeTradeEntity tradeEntity = systradeTradeService.getById(refundsEntity.getTid());
        ModelMap model = new ModelMap();
        model.put("tid",refundsEntity.getTid());
        model.put("status",refundsEntity.getStatus());
        model.put("statusName",sysDictService.getValueByCode("refund_status",refundsEntity.getStatus()));
        model.put("payment",refundsEntity.getPayment());
        model.put("totalPrice",refundsEntity.getTotalPrice());
        model.put("refundFee",refundsEntity.getRefundFee());
        model.put("refundsId",refundsEntity.getRefundsId());
        model.put("refundNum",refundsEntity.getRefundNum() != null ?refundsEntity.getRefundNum() : "");
        model.put("type",tradeEntity.getType());
        List<Map> orders = new ArrayList();
        if("0".equals(refundsEntity.getRefundsType())){
            QueryWrapper<SystradeOrderEntity> queryWrapper = new QueryWrapper();
            queryWrapper.eq("tid",refundsEntity.getTid());
            List<SystradeOrderEntity> orderlist = systradeOrderService.list(queryWrapper);
            for(SystradeOrderEntity order : orderlist){
                Map ordermap = new HashMap();
                ordermap.put("title",order.getTitle());
                ordermap.put("imageDefaultId",order.getPicPath());
                ordermap.put("specInfo",order.getSpecInfo());
                ordermap.put("num",order.getNum());
                ordermap.put("price",order.getPrice());
                orders.add(ordermap);
            }
        }else{
            Map ordermap = new HashMap();
            SystradeOrderEntity order = systradeOrderService.getById(Long.valueOf(refundsEntity.getOid()));
            ordermap.put("title",order.getTitle());
            ordermap.put("imageDefaultId",order.getPicPath());
            ordermap.put("specInfo",order.getSpecInfo());
            ordermap.put("num",order.getNum());
            ordermap.put("price",order.getPrice());
            orders.add(ordermap);
        }
        model.put("orderlist",orders);
        return model;
    }
    @Override
    public R tradeCancel(String tid, String reason, Integer userId, HttpServletRequest request){
        SystradeTradeEntity trade  = systradeTradeService.getById(tid);
        /**if(!"2".equals(trade.getStatus())){
         return R.error("不能取消订单");
         }
         **/
        //先从redis获取，如果redis没有，那么从数据库取出最大的，放到redis中
        String RetundBn = redisUtils.get(Constants.REDIS_REFUND_BN);
        if (StringUtils.isEmpty(RetundBn)) {
            redisUtils.set(Constants.REDIS_REFUND_BN, sysaftersalesRefundsDao.selectMaxRetundBn() + "");
        } else {
            QueryWrapper<SysaftersalesRefundsEntity> qw = new QueryWrapper();
            qw.eq("refund_bn",RetundBn);
            SysaftersalesRefundsEntity refunds = sysaftersalesRefundsService.getOne(qw);
            if (refunds != null) {
                redisUtils.set(Constants.REDIS_REFUND_BN, sysaftersalesRefundsDao.selectMaxRetundBn() + "");
            }
        }
        Long retundbn= redisUtils.incr(Constants.REDIS_REFUND_BN);
        redisUtils.set(Constants.REDIS_REFUND_BN,retundbn + "");
        if(trade.getType() == 1){//积分订单
            //退款表
            SysaftersalesRefundsEntity refundsEntity = new SysaftersalesRefundsEntity();
            refundsEntity.setTid(trade.getTid());
            refundsEntity.setUserId(trade.getUserId());
            refundsEntity.setRefundsType("0");//取消订单
            refundsEntity.setTotalPrice(trade.getTotalFee());
            refundsEntity.setPayment(trade.getPayment());
            refundsEntity.setRefundFee(trade.getPayment());
            refundsEntity.setRefundsReason(reason);
            refundsEntity.setCreatedTime((int)(System.currentTimeMillis()/1000));
            refundsEntity.setPostFee(trade.getPostFee());
            refundsEntity.setModifiedTime((int) (System.currentTimeMillis() / 1000));
            refundsEntity.setStatus("5");//完成
            refundsEntity.setRefundBn(retundbn.toString());
            refundsEntity.setRefundFinishTime(DateUtils.currentUnixTime());
            sysaftersalesRefundsService.save(refundsEntity);
            //更新trade状态
            trade.setStatus("7");//已取消
            trade.setModifiedTime(DateUtils.currentUnixTime());
            systradeTradeService.updateById(trade);
            //更新order状态
            QueryWrapper<SystradeOrderEntity> queryWrapper = new QueryWrapper();
            queryWrapper.eq("tid",trade.getTid());
            List<SystradeOrderEntity> orderlist = systradeOrderService.list(queryWrapper);
            for(SystradeOrderEntity order : orderlist){
                order.setStatus("7");
                order.setAftersalesStatus("5");//退款完成
                systradeOrderService.updateById(order);
                //更新库存
                SysitemSkuEntity skuEntity = sysitemSkuService.getById(order.getSkuId());
                skuEntity.setStore(skuEntity.getStore() + order.getNum());
                sysitemSkuService.updateById(skuEntity);
                //更新销量
                SysitemItemEntity itemEntity = sysitemItemService.getById(order.getItemId());
                itemEntity.setSoldNum(itemEntity.getSoldNum() - order.getNum());
                sysitemItemService.updateById(itemEntity);
                //修改opensearch销量
                updateItemCount(itemEntity);
            }
            //更新积分
            QueryWrapper<SyscapitalCapitalEntity> qw = new QueryWrapper();
            qw.eq("user_id",trade.getUserId());
            SyscapitalCapitalEntity capitalEntity = syscapitalCapitalService.getOne(qw);
            if(capitalEntity != null){
                //新增积分流水记录
                SyscapitalCapitalIntegralEntity integralEntity = new SyscapitalCapitalIntegralEntity();
                integralEntity.setIntegralFee(trade.getPayment());
                integralEntity.setIntegralType(3);
                integralEntity.setCapitalId(capitalEntity.getCapitalId());
                integralEntity.setUserId(capitalEntity.getUserId());
                integralEntity.setTid(trade.getTid());
                integralEntity.setCreatedTime(DateUtils.currentUnixTime());
                integralEntity.setStatus(1);
                syscapitalCapitalIntegralService.save(integralEntity);
                //更新总积分
                capitalEntity.setTotalIntegral(capitalEntity.getTotalIntegral().add(trade.getPayment()));
                syscapitalCapitalService.updateById(capitalEntity);
            }
        }else{//普通订单
            //微信退款
            String code = "success";
            QueryWrapper<EctoolsTradePaybillEntity> qw = new QueryWrapper();
            qw.eq("tid",tid);
            qw.eq("status","succ");
            List<EctoolsTradePaybillEntity> paybills = ectoolsTradePaybillService.list(qw);
            if(paybills != null && paybills.size() > 0){
                EctoolsTradePaybillEntity paybillEntity = paybills.get(0);
                PayOrder product = new PayOrder();
                product.setSpbillCreateIp(request.getRemoteHost());
                product.setOutTradeNo(paybillEntity.getPaymentId());
                product.setOutRefundNo(retundbn.toString());
                product.setTotalAmount(trade.getPayment().subtract(trade.getRechargeFee()).toString());
                product.setRefundFee(trade.getPayment().subtract(trade.getRechargeFee()).toString());
                ResEntity resEntity = refund(product);
                code = resEntity.getCode();
            }
            if(Constants.SUCCESS.equals(code)){//退款成功
                //退款表
                SysaftersalesRefundsEntity refundsEntity = new SysaftersalesRefundsEntity();
                refundsEntity.setTid(trade.getTid().toString());
                refundsEntity.setUserId(trade.getUserId());
                refundsEntity.setRefundsType("0");//取消订单
                refundsEntity.setTotalPrice(trade.getTotalFee());
                refundsEntity.setPayment(trade.getPayment());
                refundsEntity.setRefundFee(trade.getPayment().subtract(trade.getRechargeFee()));
                refundsEntity.setRechargeFee(trade.getRechargeFee());
                refundsEntity.setRefundsReason(reason);
                refundsEntity.setCreatedTime((int)(System.currentTimeMillis()/1000));
                refundsEntity.setPostFee(trade.getPostFee());
                refundsEntity.setModifiedTime((int) (System.currentTimeMillis() / 1000));
                refundsEntity.setStatus("5");//完成
                refundsEntity.setRefundBn(retundbn.toString());
                refundsEntity.setRefundFinishTime(DateUtils.currentUnixTime());
                sysaftersalesRefundsService.save(refundsEntity);
                //退还余额支付
                if (trade.getRechargeFee().compareTo(BigDecimal.ZERO) == 1){
                    //用户资产信息
                    QueryWrapper<SyscapitalCapitalEntity> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("user_id",userId);
                    SyscapitalCapitalEntity capital = syscapitalCapitalService.getOne(queryWrapper);
                    //增加流水
                    SyscapitalCapitalDetailEntity capitalDetail = new SyscapitalCapitalDetailEntity();
                    capitalDetail.setCapitalId(capital.getCapitalId());
                    capitalDetail.setCapitalType(9);
                    capitalDetail.setUserId(capital.getUserId());
                    capitalDetail.setCapitalFee(trade.getRechargeFee());
                    capitalDetail.setCreatedTime(DateUtils.currentUnixTime());
                    capitalDetailService.save(capitalDetail);
                    //充值余额
                    capital.setTotalRecharge(capital.getTotalRecharge().add(trade.getRechargeFee()));
                    capital.setTotalCapital(capital.getTotalCapital().add(trade.getRechargeFee()));
                    syscapitalCapitalService.updateById(capital);
                }
                //更新trade状态
                trade.setStatus("7");//已取消
                trade.setModifiedTime(DateUtils.currentUnixTime());
                systradeTradeService.updateById(trade);
                //更新order状态
                QueryWrapper<SystradeOrderEntity> queryWrapper = new QueryWrapper();
                queryWrapper.eq("tid",trade.getTid());
                List<SystradeOrderEntity> orderlist = systradeOrderService.list(queryWrapper);
                for(SystradeOrderEntity order : orderlist){
                    order.setStatus("7");
                    order.setAftersalesStatus("5");//退款完成
                    systradeOrderService.updateById(order);
                    //更新库存
                    SysitemSkuEntity skuEntity = sysitemSkuService.getById(order.getSkuId());
                    if(skuEntity != null){
                        skuEntity.setStore(skuEntity.getStore() + order.getNum());
                        sysitemSkuService.updateById(skuEntity);
                    }
                    //更新销量
                    SysitemItemEntity itemEntity = sysitemItemService.getById(order.getItemId());
                    itemEntity.setSoldNum(itemEntity.getSoldNum() - order.getNum());
                    sysitemItemService.updateById(itemEntity);
                    //修改opensearch销量
                    updateItemCount(itemEntity);
                }
            }else{
                return R.error("操作失败,请联系平台");
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
            logger.info("商品"+item.getItemId()+"成功添加到搜索服务器");
        }
        else{
            logger.info("商品"+item.getItemId()+"添加到搜索服务器失败");
        }

    }
    public ResEntity refund(PayOrder product) {
        logger.info("订单号：{}微信退款",product.getOutTradeNo());
        ResEntity resEntity = new ResEntity();
        Map map = null;
        try {

            SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
            //公众账号ID	appid  商户号	mch_id 随机字符串	nonce_str
            String nonceStr = PayCommonUtil.createNoncestr();
            packageParams.put("appid", app_id);// 公众账号ID
            packageParams.put("mch_id", MCH_ID);// 商户号
            packageParams.put("nonce_str", nonceStr);// 随机字符串
            //商户订单号	out_trade_no  商户退款单号	out_refund_no
            packageParams.put("out_trade_no", product.getOutTradeNo());// 商户订单号
            packageParams.put("out_refund_no", product.getOutRefundNo());//商户退款单号
            String totalAmount = product.getTotalAmount();
            totalAmount =  CommonUtil.mul(totalAmount, "100").intValue()+"";
            String refundFee = product.getRefundFee();
            refundFee =  CommonUtil.mul(refundFee, "100").intValue()+"";
            packageParams.put("total_fee", totalAmount);// 总金额
            packageParams.put("refund_fee", refundFee);//退款金额
            packageParams.put("op_user_id", MCH_ID);//操作员帐号, 默认为商户号

            String sign = PayCommonUtil.createSign("UTF-8", packageParams, API_KEY);
            packageParams.put("sign", sign);// 签名
            String requestXML = XMLUtil.getRequestXml(packageParams);
            logger.info("微信退款请求参数requestXML:"+requestXML);
            String weixinPost = ClientCustomSSL.doRefund(ConfigUtil.REFUND_URL, requestXML,CERT_PATH,MCH_ID).toString();
            map = XMLUtil.doXMLParse(weixinPost);

            String returnCode = (String) map.get("return_code");
            if("SUCCESS".equals(returnCode)){
                String resultCode = (String) map.get("result_code");
                if("SUCCESS".equals(resultCode)){
                    resEntity.setCode(Constants.SUCCESS);
                    resEntity.setMessage("退款成功");
                    resEntity.setContent(JSON.toJSONString(map));
                    logger.info("订单:"+product+"退款成功:"+weixinPost);
                }else{
                    resEntity.setCode(Constants.FAIL);
                    resEntity.setContent(JSON.toJSONString(map));
                    logger.info("订单号"+product+"退款失败:"+weixinPost);
                }
            }else{
                resEntity.setCode(Constants.FAIL);
                resEntity.setContent(JSON.toJSONString(map));
                logger.info("订单信息"+product+"退款失败:"+weixinPost);
            }
        } catch (Exception e) {
            resEntity.setCode(Constants.FAIL);
            resEntity.setContent("异常"+e.getMessage());
            logger.error("订单信息"+product+"退款异常:",e);
        }
        return resEntity;
    }

    public static void main(String[] args) {
        BigDecimal refundFee =  new BigDecimal(1).divide(new BigDecimal(2)).multiply(new BigDecimal(50)).setScale(2,BigDecimal.ROUND_HALF_UP);
        System.out.println(refundFee);
    }
    @Override
    public R refundgood(SysaftersalesRefundsEntity refundsEntity){
        SystradeOrderEntity order = systradeOrderService.getById(Long.valueOf(refundsEntity.getOid()));
        SystradeTradeEntity trade  = systradeTradeService.getById(order.getTid());
        //退款表
        refundsEntity.setTid(trade.getTid().toString());
        refundsEntity.setUserId(trade.getUserId());
        refundsEntity.setRefundsType("1");//退货退款
        refundsEntity.setTotalPrice(order.getTotalFee());
        refundsEntity.setPayment(order.getPayment());//支付金额
        BigDecimal refundFee = BigDecimal.ZERO;
        BigDecimal rechargeFee = BigDecimal.ZERO;
        if(refundsEntity.getRefundNum().equals(order.getNum())){
            refundFee = order.getPayment();
            rechargeFee = order.getRechargeFee();
        }else if(refundsEntity.getRefundNum() < order.getNum()){
            BigDecimal refundNum = new BigDecimal(refundsEntity.getRefundNum());
            BigDecimal orderNum = new BigDecimal(order.getNum());
            refundFee =  refundNum.divide(orderNum).multiply(order.getPayment()).setScale(2,BigDecimal.ROUND_HALF_UP);
            if(order.getRechargeFee().compareTo(BigDecimal.ZERO) == 1){
                rechargeFee = refundNum.divide(orderNum).multiply(order.getRechargeFee()).setScale(2,BigDecimal.ROUND_HALF_UP);
            }
        }else {
            return R.error("请选择正确的退款数量！");
        }
        refundsEntity.setRefundFee(refundFee.subtract(rechargeFee));//退款金额
        refundsEntity.setRechargeFee(rechargeFee);//钱包余额支付金额
        refundsEntity.setCreatedTime((int)(System.currentTimeMillis()/1000));
        refundsEntity.setPostFee(trade.getPostFee());
        refundsEntity.setModifiedTime((int) (System.currentTimeMillis() / 1000));
        refundsEntity.setStatus("1");//待审核
        //先从redis获取，如果redis没有，那么从数据库取出最大的，放到redis中
        String RetundBn = redisUtils.get(Constants.REDIS_REFUND_BN);
        if (StringUtils.isEmpty(RetundBn)) {
            redisUtils.set(Constants.REDIS_REFUND_BN, sysaftersalesRefundsDao.selectMaxRetundBn() + "");
        } else {
            QueryWrapper<SysaftersalesRefundsEntity> qw = new QueryWrapper();
            qw.eq("refund_bn",RetundBn);
            SysaftersalesRefundsEntity refunds = sysaftersalesRefundsService.getOne(qw);
            if (refunds != null) {
                redisUtils.set(Constants.REDIS_REFUND_BN, sysaftersalesRefundsDao.selectMaxRetundBn() + "");
            }
        }
        Long retundbn= redisUtils.incr(Constants.REDIS_REFUND_BN);
        redisUtils.set(Constants.REDIS_REFUND_BN,retundbn + "");
        refundsEntity.setRefundBn(retundbn.toString());
        sysaftersalesRefundsService.save(refundsEntity);
        //更新小订单状态
        order.setAftersalesStatus("1");//待审核
        systradeOrderService.updateById(order);
        return R.ok();
    }
    @Override
    public R cancellogistics(Integer refundsId){
        SysaftersalesRefundsEntity refundsEntity = getById(refundsId);
        if(refundsEntity != null){
            if("1".equals(refundsEntity.getStatus()) || "2".equals(refundsEntity.getStatus())){
                //更新小订单状态
                SystradeOrderEntity orderEntity = systradeOrderService.getById(Long.valueOf(refundsEntity.getOid()));
                orderEntity.setAftersalesStatus("");
                systradeOrderService.updateById(orderEntity);
                //删除售后表
                removeById(refundsId);
                return R.ok();
            }else{
                return R.error("不能取消售后");
            }
        }else{
            return R.error("数据错误,请联系平台");
        }
    }

}
