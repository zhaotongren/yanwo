package com.yanwo.controller;

import com.aliyun.opensearch.util.JsonUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.entity.*;
import com.yanwo.service.*;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.GUtils;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/aftersales")
@Api(tags = "订单接口")
public class ApiAftersalesController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(ApiAftersalesController.class);

    @Autowired
    private SystradeTradeService systradeTradeService;
    @Autowired
    private SystradeOrderService systradeOrderService;
    @Autowired
    private SysaftersalesRefundsService sysaftersalesRefundsService;
    @Autowired
    private SysDictService sysDictService;
    @Autowired
    private SystradeLogisticsService systradeLogisticsService;

    @PostMapping("/list")
    @ApiOperation("获取售后列表")
    public R trade(@RequestHeader("token") String token,@RequestParam(defaultValue = "1") Integer currPage) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//查看订单需要先登录
                return R.auth("请登录");
            }
            Integer userId = user.getUserId();
            PageUtils pages = sysaftersalesRefundsService.queryPage(userId,currPage);
            return R.okput(pages);
        } catch (Exception e) {
            log.info("获取订单列表发生异常", e);
            return R.error();
        }
    }

    @PostMapping("/cancelBeforePost")
    @ApiOperation("取消订单")
    public R cancelBeforePost(@RequestHeader("token") String token, String tid, String reason, HttpServletRequest request){
        UserEntity user = getUserFromTokenApp(token);
        if (user == null) {
            return R.auth("请登录");
        }
        //重复操作判断
        if (redisUtils.existsKey("cancelBeforePost" + user.getUserId())) {
            return R.error("操作频繁,请10秒后重新操作");
        } else {
            redisUtils.set("cancelBeforePost" + user.getUserId(), "0", 10);
        }
        if (tid==null){
            log.info("取消订单订单号为空",tid);
            return R.error("数据异常,请联系平台");
        }
        try {
            QueryWrapper<SysaftersalesRefundsEntity> qw = new QueryWrapper();
            qw.eq("tid", tid);
            qw.eq("refunds_type", "0");
            List list = sysaftersalesRefundsService.list(qw);
            if (list != null && list.size() > 0) {
                return R.error("取消订单已申请，请勿重复操作");
            }
            return sysaftersalesRefundsService.tradeCancel(tid,reason,user.getUserId(),request);
        }catch (Exception e){
            log.info("取消订单发生异常", e);
            return R.error();
        }
    }

    @PostMapping(value="/refundgood")
    @ApiOperation("退货退款")
    public R refundgood(@RequestHeader("token") String token, @RequestParam("oid")Long oid,
                                 @RequestParam("reason") String reason,
                                 @RequestParam(value="description",required = false) String description,
                                 @RequestParam("refunds_num") Integer refunds_num,
                                 @RequestParam(value="photos",required = false) String photos){
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {
                return R.auth("请登录");
            }
            SysaftersalesRefundsEntity refundsEntity = new SysaftersalesRefundsEntity();
            refundsEntity.setUserId(user.getUserId());
            refundsEntity.setRefundsReason(reason);
            refundsEntity.setOid(oid.toString());
            refundsEntity.setRefundNum(refunds_num);
            refundsEntity.setDescription(description);
            refundsEntity.setEvidencePic(photos);
            return sysaftersalesRefundsService.refundgood(refundsEntity);
        }catch (Exception e){
            log.info("退货退款发生异常", e);
            return R.error();
        }
    }
    @PostMapping(value="/info")
    @ApiOperation("售后详情")
    public R info(@RequestHeader("token") String token, Integer refundsId){
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {
                return R.auth("请登录");
            }
            SysaftersalesRefundsEntity refundsEntity = sysaftersalesRefundsService.getById(refundsId);
            Map trademap = new HashMap();
            trademap.put("refundsId",refundsEntity.getRefundsId());
            trademap.put("tid",refundsEntity.getTid());
            trademap.put("totalPrice",refundsEntity.getTotalPrice());
            trademap.put("refundFee",refundsEntity.getRefundFee());
            trademap.put("status",refundsEntity.getStatus());
            trademap.put("statusName",sysDictService.getValueByCode("refund_status",refundsEntity.getStatus()));
            trademap.put("refundsReason",refundsEntity.getRefundsReason());
            trademap.put("refundNum",refundsEntity.getRefundNum() != null ? refundsEntity.getRefundNum() : "");
            trademap.put("createdTime", GUtils.IntegerToDate(refundsEntity.getCreatedTime()));
            trademap.put("refundBn", refundsEntity.getRefundBn());
            SystradeTradeEntity tradeEntity = systradeTradeService.getById(Long.valueOf(refundsEntity.getTid()));
            trademap.put("type", tradeEntity.getType());

            List<Map> orderList = new ArrayList();
            if("0".equals(refundsEntity.getRefundsType())){
                //取消订单
                QueryWrapper<SystradeOrderEntity> qw = new QueryWrapper();
                qw.eq("tid",refundsEntity.getTid());
                List<SystradeOrderEntity> orders = systradeOrderService.list(qw);
                for(SystradeOrderEntity orderEntity : orders){
                    Map ordermap = new HashMap();
                    ordermap.put("title",orderEntity.getTitle());
                    ordermap.put("num",orderEntity.getNum());
                    ordermap.put("price",orderEntity.getPrice());
                    ordermap.put("totalFee",orderEntity.getTotalFee());
                    ordermap.put("specInfo",orderEntity.getSpecInfo());
                    ordermap.put("picPath",orderEntity.getPicPath());
                    orderList.add(ordermap);
                }
            }else{//退货退款
                SystradeOrderEntity orderEntity = systradeOrderService.getById(Long.valueOf(refundsEntity.getOid()));
                Map ordermap = new HashMap();
                ordermap.put("title",orderEntity.getTitle());
                ordermap.put("num",orderEntity.getNum());
                ordermap.put("price",orderEntity.getPrice());
                ordermap.put("totalFee",orderEntity.getTotalFee());
                ordermap.put("specInfo",orderEntity.getSpecInfo());
                ordermap.put("picPath",orderEntity.getPicPath());
                orderList.add(ordermap);
            }
            Map resultmap = new HashMap();
            resultmap.put("trade",trademap);
            resultmap.put("orderList",orderList);
            return R.okput(resultmap);
        }catch (Exception e){
            log.info("查看售后详情发生异常", e);
            return R.error();
        }
    }
    @PostMapping(value="/cancellogistics")
    @ApiOperation("取消售后 退货退款")
    public R cancellogistics(@RequestHeader("token") String token,Integer refundsId){
        try{
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {
                return R.auth("请登录");
            }
            return sysaftersalesRefundsService.cancellogistics(refundsId);
        }catch (Exception e){
            log.info("取消售后发生异常", e);
            return R.error();
        }
    }
    @PostMapping(value="/logistics")
    @ApiOperation("退货退款 售后填写物流")
    public R logistics(@RequestHeader("token") String token,Integer refundsId,String logistics_no, String logistics_company,String logistics_code,String receiver_name,String receiver_address,String mobile){
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {
                return R.auth("请登录");
            }
            Map map=new HashMap();
            map.put("logistics_no",logistics_no);//物流单号
            map.put("logistics_company",logistics_company);//物流公司
            map.put("logistics_code",logistics_code);//物流编码
            map.put("receiver_name",receiver_name);//收货人
            map.put("receiver_address",receiver_address);//收货地址
            map.put("mobile",mobile);//收货人电话号码
            SysaftersalesRefundsEntity refundsEntity = sysaftersalesRefundsService.getById(refundsId);
            refundsEntity.setSendbackData(JsonUtil.objectToString(map));
            refundsEntity.setStatus("4");
            refundsEntity.setModifiedTime(DateUtils.currentUnixTime());
            sysaftersalesRefundsService.updateById(refundsEntity);
            SystradeOrderEntity orderEntity = systradeOrderService.getById(Long.valueOf(refundsEntity.getOid()));
            orderEntity.setAftersalesStatus("4");
            systradeOrderService.updateById(orderEntity);

           return R.ok();
        } catch (Exception e) {
            log.info("售后填写物流发生异常", e);
            return R.error();
        }
    }

    @PostMapping(value="/logisticsList")
    @ApiOperation("物流公司列表")
    public R logisticsList(){
        try {
            List<SystradeLogisticsEntity> list = systradeLogisticsService.list();
            return R.okput(list);
        } catch (Exception e) {
            log.info("查询物流公司列表异常", e);
            return R.error();
        }
    }

}
