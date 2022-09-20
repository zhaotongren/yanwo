package com.yanwo.modules.controller.sys;

import com.yanwo.common.utils.GUtils;
import com.yanwo.entity.SystradeOrderEntity;
import com.yanwo.entity.SystradeTradeEntity;
import com.yanwo.modules.service.SystradeOrderService;
import com.yanwo.modules.service.SystradeTradeService;
import com.yanwo.modules.service.UserService;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/excel")
public class ExcelController {

    @Autowired
    public UserService userService;

    @Autowired
    public SystradeTradeService systradeTradeService;
    @Autowired
    public SystradeOrderService systradeOrderService;

    @RequestMapping("/tradeTreadExcel")
    public void tradeTreadExcel(HttpServletResponse response, String[] tids) throws IOException {

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("商品订单导出");
        String fileName = "商品订单导出";

        String[] headers = {"订单号", "收货人手机号", "订单总金额", "支付金额", "钱包金额","支付时间" , "发货时间","状态","详情"}; //headers表示excel表中第一行的表头
        HSSFRow row = sheet.createRow(0);
        row.setHeight((short)500);

        HSSFFont font=workbook.createFont();
        font.setFontHeightInPoints((short)15);
        HSSFCellStyle hssfCellStyle= workbook.createCellStyle();
        hssfCellStyle.setFont(font);

        hssfCellStyle.setAlignment(HorizontalAlignment.CENTER);
        hssfCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hssfCellStyle.setFillForegroundColor(IndexedColors.TAN.getIndex());
        hssfCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        hssfCellStyle.setBorderBottom(BorderStyle.DOUBLE); //下边框
        hssfCellStyle.setBorderLeft(BorderStyle.DOUBLE);//左边框
        hssfCellStyle.setBorderTop(BorderStyle.DOUBLE);//上边框
        hssfCellStyle.setBorderRight(BorderStyle.DOUBLE);//右边框
        HSSFCellStyle style = workbook.createCellStyle();
        font.setFontHeightInPoints((short)13);
        style.setFont(font);

        //在excel表中添加表头
        for(int i=0;i<headers.length;i++){
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(hssfCellStyle);
            //sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i,256*30+184);//设置列宽
            sheet.setDefaultColumnStyle(i,style);
        }
        int rowNum = 1;

        for (int i=0;i<tids.length;i++){

            HSSFRow row1 = sheet.createRow(rowNum);
            String tid=tids[i];
            List<SystradeOrderEntity> orlist=systradeOrderService.getOrderByTid(tid);
            SystradeTradeEntity entity=systradeTradeService.getById(tid);

            row1.createCell(0).setCellValue(entity.getTid());
            row1.createCell(1).setCellValue(entity.getReceiverMobile());
            row1.createCell(2).setCellValue(entity.getTotalFee().intValue());
            row1.createCell(3).setCellValue(entity.getPayment().intValue());
            row1.createCell(4).setCellValue(entity.getWelfareFee().intValue());
            row1.createCell(5).setCellValue(GUtils.IntegerToDate(entity.getPayTime()));
            row1.createCell(6).setCellValue(GUtils.IntegerToDate(entity.getConsignTime()));
            String status="";
            if (entity.getStatus().equals("1")){ status="代付款";}
            if (entity.getStatus().equals("2")){ status="代发货";}
            if (entity.getStatus().equals("3")){ status="待收货";}
            if (entity.getStatus().equals("4")){ status="已完成";}
            if (entity.getStatus().equals("5")){ status="已完结";}

            row1.createCell(7).setCellValue(status);
            String xq="";
            for (SystradeOrderEntity order:orlist ) {
                xq=xq+"商品标题:"+order.getTitle()+",单价:"+order.getPrice()+",数量："+order.getNum()+"   ";
            }
            row1.createCell(8).setCellValue(xq);
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
