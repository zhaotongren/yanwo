package com.yanwo.modules.controller.aftersales;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.yanwo.common.utils.GUtils;
import com.yanwo.entity.*;
import com.yanwo.modules.service.*;
import com.yanwo.modules.service.impl.SysaftersalesRefundsServiceImpl;
import com.yanwo.utils.DateUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-17 14:38:50
 */
@RestController
@RequestMapping("sys/sysaftersalesrefunds")
public class SysaftersalesRefundsController {
    private static final Logger logger = LoggerFactory.getLogger(SysaftersalesRefundsController.class);

    @Autowired
    private SysaftersalesRefundsService sysaftersalesRefundsService;
    @Autowired
    private SystradeOrderService systradeOrderService;
    @Autowired
    private SysitemSkuService sysitemSkuService;
    @Autowired
    private SysitemItemService sysitemItemService;
    @Autowired
    private UserService userService;
    @Autowired
    private SysreturnAddressService sysreturnAddressService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:sysaftersalesrefunds:list")
    public PageUtils list(@RequestParam Map<String, Object> params){
        PageUtils page = sysaftersalesRefundsService.queryPage(params);
        return page;
    }

    /**
     * 修改回寄地址
     */
    @RequestMapping(value = "/updateReturn",method = RequestMethod.POST)
    public R updateReturn(Integer returnId,String address){
        SysreturnAddressEntity addressEntity = new SysreturnAddressEntity();
        addressEntity.setAddress(address);
        if(null==returnId){
            sysreturnAddressService.save(addressEntity);
        }else{
            addressEntity.setReturnId(returnId);
            sysreturnAddressService.updateById(addressEntity);
        }
        return R.ok("保存成功");
    }
    @RequestMapping(value = "/getReturn",method = RequestMethod.GET)
    public R getReturn(Integer id){
        List<SysreturnAddressEntity> list = sysreturnAddressService.list();
        if(list != null && list.size() > 0){
            SysreturnAddressEntity sysreturnAddressEntity = list.get(0);
            return R.ok().put("returnAdd",sysreturnAddressEntity);
        }else{
            return R.ok().put("returnAdd",new SysreturnAddressEntity());
        }
    }
    /**
     * 审核通过
     */
    @RequestMapping(value = "/pass",method = RequestMethod.POST)
    public R pass(Integer refundsId,String returnAddress){
        SysaftersalesRefundsEntity refunds = sysaftersalesRefundsService.getById(refundsId);
        refunds.setStatus("2");
        refunds.setModifiedTime(DateUtils.currentUnixTime());
        refunds.setModifiedTime(DateUtils.currentUnixTime());
        refunds.setReturnAddress(returnAddress);
        sysaftersalesRefundsService.updateById(refunds);
        //修改order状态
        SystradeOrderEntity order = systradeOrderService.getById(refunds.getOid());
        order.setAftersalesStatus("2");
        systradeOrderService.updateById(order);
        return R.ok("审核完成");
    }

    /**
     * 审核驳回
     */
    @RequestMapping(value = "/fail",method = RequestMethod.POST)
    public R fail(Integer refundsId,String adminExplanation){
        SysaftersalesRefundsEntity refunds = sysaftersalesRefundsService.getById(refundsId);
        refunds.setAdminExplanation(adminExplanation);
        refunds.setStatus("3");
        refunds.setModifiedTime(DateUtils.currentUnixTime());
        sysaftersalesRefundsService.updateById(refunds);
        //修改order状态
        SystradeOrderEntity order = systradeOrderService.getById(refunds.getOid());
        order.setAftersalesStatus("3");
        systradeOrderService.updateById(order);
        return R.ok("审核完成");
    }

    /**
     * 售后详情
     */
    @RequestMapping(value = "/getInfo",method = RequestMethod.GET)
    public R getInfo(Integer refundsId){
        Map resultMap=new HashMap<>();
        SysaftersalesRefundsEntity refunds = sysaftersalesRefundsService.getById(refundsId);

        if("1".equals(refunds.getRefundsType())){
            List<Map> orderData = new ArrayList();

            SystradeOrderEntity order = systradeOrderService.getById(refunds.getOid());
            SysitemSkuEntity sku = sysitemSkuService.getById(order.getSkuId());
            SysitemItemEntity item = sysitemItemService.getById(sku.getItemId());

            Map ordermap = new HashMap();
            ordermap.put("oid",order.getOid());
            ordermap.put("title",item.getTitle());
            ordermap.put("price",sku.getPrice());
            ordermap.put("num",order.getNum());
            orderData.add(ordermap);

            resultMap.put("refunds",refunds);
            resultMap.put("orderData",orderData);
        }else{
            QueryWrapper<SystradeOrderEntity> queryWrapper = new QueryWrapper();
            queryWrapper.eq("tid",refunds.getTid());
            List<SystradeOrderEntity> orderlist = systradeOrderService.list(queryWrapper);
            List<Map> orderData = new ArrayList();
            for(SystradeOrderEntity order : orderlist){
                Map ordermap = new HashMap();
                SysitemSkuEntity sku = sysitemSkuService.getById(order.getSkuId());
                SysitemItemEntity item = sysitemItemService.getById(sku.getItemId());
                ordermap.put("oid",order.getOid());
                ordermap.put("title",item.getTitle());
                ordermap.put("price",sku.getPrice());
                ordermap.put("num",order.getNum());
                orderData.add(ordermap);
            }
            resultMap.put("refunds",refunds);
            resultMap.put("orderData",orderData);
        }

        return R.ok(resultMap);
    }


    /**
     * 退款
     */
    @RequestMapping("/refund")
    @RequiresPermissions("sys:sysaftersalesrefunds:refund")
    public R refund(Integer refundsId, HttpServletRequest request){
        try{
            SysaftersalesRefundsEntity refundsEntity = sysaftersalesRefundsService.getById(refundsId);
            if(!"4".equals(refundsEntity.getStatus())){
                return R.error("不能退款");
            }
            return sysaftersalesRefundsService.refund(refundsEntity,request);

        }catch (Exception e){
            logger.info("退款异常",e);
            return R.error("操作异常,请联系平台");
        }


    }

    @RequestMapping("/excel")
    public void tradeTreadExcel(HttpServletResponse response, String [] refundsIds) throws IOException {
        //创建Excel文件
        HSSFWorkbook workbook = new HSSFWorkbook();
        //创建sheet表单
        HSSFSheet sheet = workbook.createSheet("sheet1");
        sheet.setColumnWidth(0, 8000);
        sheet.setColumnWidth(1, 8000);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(3, 6000);
        sheet.setColumnWidth(4, 6000);
        sheet.setColumnWidth(5, 6000);
        sheet.setColumnWidth(6, 6000);
        sheet.setColumnWidth(7, 6000);
        sheet.setColumnWidth(8, 10000);
        String fileName = "售后表";
        //创建绘图对象（注解）
        HSSFPatriarch P = sheet.createDrawingPatriarch();
        //创建sheet表的第一行
        HSSFRow row1 = sheet.createRow(0);
        String[] headers = {"会员名", "订单编号", "订单子编号", "状态", "支付金额","商品金额","退款金额","运费金额","申请时间"};
        //在excel表中添加表头
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row1.createCell(i);
            cell.setCellValue(headers[i]);
        }
        int rowNum = 1;
        for(int i=0;i<refundsIds.length;i++){
            row1 = sheet.createRow(rowNum);
            SysaftersalesRefundsEntity refunds = sysaftersalesRefundsService.getById(refundsIds[i]);
            UserEntity user = userService.getById(refunds.getUserId());
            row1.createCell(0).setCellValue(user.getNickName());
            row1.createCell(1).setCellValue(refunds.getTid());
            row1.createCell(2).setCellValue(refunds.getOid());
            String status="";
            if (refunds.getStatus().equals("1")){ status="待平台审核";}
            if (refunds.getStatus().equals("2")){ status="待回寄";}
            if (refunds.getStatus().equals("3")){ status="审核驳回";}
            if (refunds.getStatus().equals("4")){ status="待退款";}
            if (refunds.getStatus().equals("5")){ status="退款完成";}
            if (refunds.getStatus().equals("6")){ status="退款驳回";}
            row1.createCell(3).setCellValue(status);
            row1.createCell(4).setCellValue(Double.valueOf(refunds.getPayment().toString()));
            row1.createCell(5).setCellValue(Double.valueOf(refunds.getTotalPrice().toString()));
            row1.createCell(6).setCellValue(Double.valueOf(refunds.getRefundFee().toString()));
            row1.createCell(7).setCellValue(Double.valueOf(refunds.getPostFee().toString()));
            row1.createCell(8).setCellValue(GUtils.IntegerToDate(refunds.getCreatedTime()));

            rowNum++;
        }
        //通过html访问web层方法可以直接下载
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" +toUtf8String(fileName)+".xls");
        response.flushBuffer();
        workbook.write(response.getOutputStream());
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
                    if (k < 0) k += 256;
                    sb.append("%" + Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return sb.toString();
    }

}
