package com.yanwo.modules.controller.trade;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.common.utils.GUtils;
import com.yanwo.entity.*;
import com.yanwo.modules.service.*;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.JsonUtils;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;
import com.yanwo.validator.ValidatorUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-04 16:51:45
 */
@RestController
@RequestMapping("sys/systradetrade")
public class SystradeTradeController {
    @Autowired
    private SystradeTradeService systradeTradeService;
    @Autowired
    private UserService userService;
    @Autowired
    private SystradeOrderService systradeOrderService;
    @Autowired
    private SystradeLogisticsService systradeLogisticsService;
    @Autowired
    private SysitemItemService sysitemItemService;
    @Autowired
    private SysitemSkuService sysitemSkuService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:systradetrade:list")
    public R list(@RequestParam Map<String, Object> params,@RequestParam(defaultValue = "10") long pageSize, @RequestParam(defaultValue = "1") long pageCurrent) throws ParseException {
        PageUtils page=systradeTradeService.queryPage(params,pageSize,pageCurrent);
        return R.ok().put("page",page);
}

    /**
     * 信息
     */
    @RequestMapping("/info/{tid}")
    public R info(@PathVariable("tid") String tid){
        SystradeTradeEntity systradeTrade = systradeTradeService.getById(tid);

        return R.ok().put("systradeTrade", systradeTrade);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SystradeTradeEntity systradeTrade){
        systradeTradeService.save(systradeTrade);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SystradeTradeEntity systradeTrade){
        ValidatorUtils.validateEntity(systradeTrade);
        systradeTradeService.updateById(systradeTrade);
        
        return R.ok();
    }

    //发货
    @RequestMapping("/updateDeliverGoods")
    public R updateDeliverGoods(@RequestBody SystradeTradeEntity systradeTrade) {
        QueryWrapper<SystradeLogisticsEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("logistics_name",systradeTrade.getCourierName());
        SystradeLogisticsEntity logistics = systradeLogisticsService.getOne(queryWrapper);

        ValidatorUtils.validateEntity(systradeTrade);
        systradeTrade.setStatus("3");
        systradeTrade.setCourierCode(logistics.getLogisticsCode());
        systradeTrade.setConsignTime(GUtils.getCurrentTimestamp().intValue());
        systradeTrade.setModifiedTime(GUtils.getCurrentTimestamp().intValue());
        systradeTradeService.updateById(systradeTrade);
        return R.ok();
    }
    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] tids){
        systradeTradeService.removeByIds(Arrays.asList(tids));
        return R.ok();
    }

    /**
     * 统计
     */
    @RequestMapping("/statistics")
    public R statistics(@RequestParam Map<String, Object> params){
        try {
            String startTime = GUtils.stringDateToStringTimestamp(params.get("startTime").toString());
            String endTime = GUtils.stringDateToStringTimestamp(params.get("endTime").toString());
            if(StringUtils.isBlank(startTime)){
                params.put("startTime",DateUtils.date2TimeStamp(DateUtils.getLastDayStart(),DateUtils.DATE_TIME_PATTERN));
            }else{
                params.put("startTime",startTime);
            }
            if(StringUtils.isBlank(endTime)){
                params.put("endTime",DateUtils.date2TimeStamp(DateUtils.getLastDayEnd(),DateUtils.DATE_TIME_PATTERN));
            }else{
                params.put("endTime",endTime);
            }
            BigDecimal tradeMoney = systradeTradeService.statistics(params);
            BigDecimal afterSaleMoney = systradeTradeService.afterSale(params);
            return R.ok().put("tradeMoney",tradeMoney).put("afterSaleMoney",afterSaleMoney);
        } catch (ParseException e) {
            e.printStackTrace();
            return R.error("查询统计失败");
        }
    }


    @RequestMapping("/excel")
    public void tradeTreadExcel(HttpServletResponse response, String [] tids) throws IOException {
        if ("all".equals(tids[0])){
            QueryWrapper<SystradeTradeEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("deleted",0);
            if (StringUtils.isNotBlank(tids[1])){
                queryWrapper.eq("status",tids[1]);
            }
            List<SystradeTradeEntity> list = systradeTradeService.list(queryWrapper);
            List<String> tradeIds = new ArrayList<>(list.size());
            for (SystradeTradeEntity systradeTradeEntity : list) {
                tradeIds.add(systradeTradeEntity.getTid().toString());
            }
            tids =  tradeIds.toArray(new String[tradeIds.size()]);
        }
        //创建Excel文件
        HSSFWorkbook workbook = new HSSFWorkbook();
        //创建sheet表单
        HSSFSheet sheet = workbook.createSheet("sheet1");

        sheet.setColumnWidth(0, 3000);
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 3000);
        sheet.setColumnWidth(5, 4000);
        sheet.setColumnWidth(6, 3000);
        sheet.setColumnWidth(7, 3000);
        sheet.setColumnWidth(8, 3000);
        sheet.setColumnWidth(9, 3000);
        sheet.setColumnWidth(10,3000);
        sheet.setColumnWidth(11,3000);
        sheet.setColumnWidth(12,3000);
        sheet.setColumnWidth(13,3000);
        sheet.setColumnWidth(14,3000);
        sheet.setColumnWidth(15,6000);
        sheet.setColumnWidth(16,3500);
        sheet.setColumnWidth(17,5000);
        sheet.setColumnWidth(18,5000);
        sheet.setColumnWidth(19,3000);
        sheet.setColumnWidth(20,4000);
        sheet.setColumnWidth(21,3000);
        sheet.setColumnWidth(22,3000);
        sheet.setColumnWidth(23,3000);

        String fileName = "订单表"+DateUtils.getDateStrByTimestamp(DateUtils.currentUnixTime(),DateUtils.DATE_PATTERN);
        //创建绘图对象（注解）
        //HSSFPatriarch P = sheet.createDrawingPatriarch();
        //创建sheet表的第一行
        HSSFRow row1 = sheet.createRow(0);
        String[] headers = {"订单号","产品条码","订单状态","子订单号","买家昵称","商品名称","产品规格","商品单价","商品数量",
                "商品总价","运费","购买优惠信息","总金额","买家购买附言","收货人姓名","收获地址","收货人电话","订单创建时间",
                "付款时间","物流公司","物流单号","发票抬头","电子邮件"};
        //在excel表中添加表头
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row1.createCell(i);
            cell.setCellValue(headers[i]);
        }
        int rowNum = 1;
        for(int i=0;i<tids.length;i++){

            List<SystradeOrderEntity> orlist=systradeOrderService.getOrderByTid(tids[i]);
            SystradeTradeEntity trade=systradeTradeService.getById(tids[i]);
            if(trade == null){
                System.out.println("订单导出 根据tid查trade为空，tid="+tids[i]);
                continue;
            }
            UserEntity tradeUserEntity = userService.getById(trade.getUserId());
            String status="";
            if (trade.getStatus().equals("1")){ status="待付款";}
            if (trade.getStatus().equals("2")){ status="待发货";}
            if (trade.getStatus().equals("3")){ status="待收货";}
            if (trade.getStatus().equals("4")){ status="已完成";}
            if (trade.getStatus().equals("5")){ status="已完结";}
            if (trade.getStatus().equals("6")){ status="已关闭";}
            if (trade.getStatus().equals("7")){ status="已取消";}
            for (SystradeOrderEntity systradeOrderEntity : orlist) {
                row1 = sheet.createRow(rowNum);
                SysitemSkuEntity skuEntity = sysitemSkuService.getById(systradeOrderEntity.getSkuId());
                row1.createCell(0).setCellValue(trade.getTid());
                row1.createCell(1).setCellValue(skuEntity==null?"":skuEntity.getBarcode());//条形码
                row1.createCell(2).setCellValue(status);
                row1.createCell(3).setCellValue(systradeOrderEntity.getOid());
                row1.createCell(4).setCellValue(tradeUserEntity==null?"":URLDecoder.decode(tradeUserEntity.getNickName(), "utf-8"));
                row1.createCell(5).setCellValue(systradeOrderEntity.getTitle());
                row1.createCell(6).setCellValue(systradeOrderEntity.getSpecInfo());
                row1.createCell(7).setCellValue(systradeOrderEntity.getPrice().doubleValue());
                row1.createCell(8).setCellValue(systradeOrderEntity.getNum());
                row1.createCell(9).setCellValue(systradeOrderEntity.getTotalFee().doubleValue());
                row1.createCell(10).setCellValue(trade.getPostFee().doubleValue());
                row1.createCell(11).setCellValue(systradeOrderEntity.getWelfareFee().doubleValue());
                row1.createCell(12).setCellValue(trade.getTotalFee().doubleValue());
                row1.createCell(13).setCellValue(trade.getRemark());
                row1.createCell(14).setCellValue(trade.getReceiverName());
                row1.createCell(15).setCellValue(trade.getReceiverAddress());
                row1.createCell(16).setCellValue(trade.getReceiverMobile());
                row1.createCell(17).setCellValue(GUtils.IntegerToDate(trade.getCreatedTime()));
                row1.createCell(18).setCellValue(GUtils.IntegerToDate(trade.getPayTime()));
                row1.createCell(19).setCellValue(trade.getCourierName());
                row1.createCell(20).setCellValue(trade.getCourierNumber());
                row1.createCell(21).setCellValue("");
                row1.createCell(22).setCellValue("");

                /*row1.createCell(4).setCellValue(Double.valueOf(trade.getTotalFee().toString()));
                row1.createCell(5).setCellValue(Double.valueOf(trade.getPayment().toString()));
                row1.createCell(6).setCellValue(Double.valueOf(trade.getWelfareFee().toString()));
                row1.createCell(8).setCellValue(GUtils.IntegerToDate(trade.getConsignTime()));
                row1.createCell(9).setCellValue(status);
                StringBuffer xq=new StringBuffer();
                int j=0;
                int size = orlist.size();
                for (SystradeOrderEntity order:orlist ) {
                    xq.append("商品标题:"+order.getTitle()+",单价:"+order.getPrice()+",数量:"+order.getNum());
                    if(size==j+1){

                    }else{
                        xq.append(";");
                    }
                    j++;
                }
                row1.createCell(10).setCellValue(xq.toString());*/
                rowNum++;
            }

        }
        //通过html访问web层方法可以直接下载
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" +toUtf8String(fileName)+".xls");
        response.flushBuffer();
        workbook.write(response.getOutputStream());
        workbook.close();
    }


    public static String toUtf8String(String s){

        StringBuffer sb = new StringBuffer();
        for (int i=0;i<s.length();i++){
            char c = s.charAt(i);
            if (c >= 0 && c <= 255){sb.append(c);}
            else{
                byte[] b;
                try { b = Character.toString(c).getBytes("utf-8");}
                catch (Exception ex) {
                    System.out.println(ex);
                    b = new byte[0];
                }
                for (int j = 0; j < b.length; j++) {
                    int k = b[j];
                    if (k < 0){
                        k += 256;
                    }
                    sb.append("%" + Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return sb.toString();
    }
    /**
     * 返佣列表
     */
    @RequestMapping("/getCommission/{tid}")
    public R getCommission(@PathVariable("tid") String tid){
        try {
            SystradeTradeEntity trade = systradeTradeService.getById(tid);
            List<SystradeOrderEntity> orderList = systradeOrderService.getOrderByTid(tid);
            BigDecimal rebateMoney = BigDecimal.ZERO;
            for(SystradeOrderEntity order : orderList){
                BigDecimal profitMoney = (order.getPrice().subtract(order.getCostPrice())).multiply(new BigDecimal(order.getNum())).setScale(BigDecimal.ROUND_HALF_UP, 2);
                rebateMoney = rebateMoney.add(profitMoney);
            }
            List<ModelMap> list = new ArrayList();
            if(trade != null && StringUtils.isNotBlank(trade.getRateParam())){
                List<Map> rateList = JsonUtils.jsonToList(trade.getRateParam(), Map.class);
                for (Map ratemap : rateList) {
                    ModelMap model = new ModelMap();
                    Integer userId = Integer.valueOf(ratemap.get("userId").toString());//收益人
                    BigDecimal rate = new BigDecimal(ratemap.get("rate").toString());//返利比例
                    Integer type = Integer.valueOf(ratemap.get("type").toString());//1：直级返利 2：间接返利  3：团长返利
                    UserEntity userEntity = userService.getById(userId);
                    if (userEntity != null) {
                        if(StringUtils.isNotBlank(userEntity.getNickName())){
                            model.put("nickName", URLDecoder.decode(userEntity.getNickName(), "UTF-8"));
                        }
                        model.put("mobile",userEntity.getMobile());
                    }
                    BigDecimal money = (rebateMoney.multiply(rate)).setScale(2, BigDecimal.ROUND_HALF_UP);
                    model.put("commissionMoney",trade.getStatus().equals("5") ? "已产生收益："+money : "预计产生收益："+money);
                    model.put("typeName",type == 1 ? "直级返佣":type == 2?"间接返佣":"团长返佣");
                    list.add(model);
                }
            }
            return R.ok().put("list",list);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("查询统计失败");
        }
    }

}
