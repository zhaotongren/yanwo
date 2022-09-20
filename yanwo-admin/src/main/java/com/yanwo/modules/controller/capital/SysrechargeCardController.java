package com.yanwo.modules.controller.capital;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;

import com.github.pagehelper.util.StringUtil;
import com.yanwo.common.utils.poi.CellRule;
import com.yanwo.common.utils.poi.ExcelConfig;
import com.yanwo.common.utils.poi.ExcelReader;
import com.yanwo.modules.service.SysrechargeCardService;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tomcat.util.http.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.yanwo.entity.SysrechargeCardEntity;
import org.springframework.web.multipart.MultipartFile;


/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-08-31 14:43:44
 */
@RestController
@RequestMapping("sys/sysrechargecard")
public class SysrechargeCardController {
    @Autowired
    private SysrechargeCardService sysrechargeCardService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:sysrechargecard:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysrechargeCardService.queryPage(params);

        return R.ok().put("page", page);
    }

    @RequestMapping("/import")
    public R reader(@RequestPart("file") MultipartFile multipartFile) {
        try{
            InputStream inputStream = multipartFile.getInputStream();
            String fileName = multipartFile.getOriginalFilename();
            if (!StringUtils.hasLength(fileName)) {
                return R.error("文件扩展名不存在");
            }
            boolean is03 = fileName.endsWith("xls");
            Map<String, List<Map<String, String>>> map = ExcelReader.asyncReader(inputStream, is03, config());
            // 解析的正确数据
            List<Map<String, String>> cards = map.get("Sheet1");
            Integer count = 0;
            Integer num = 2;
            List<SysrechargeCardEntity> cardlist = new ArrayList();
            if (!CollectionUtils.isEmpty(cards)) {
                for (Map<String, String> card : cards) {
                    if(StringUtil.isEmpty(card.get("cardNo"))){
                        break;
                    }
                    String card1 = card.get("cardNo").trim();
                    List<SysrechargeCardEntity> zCards = sysrechargeCardService.queryByCardList(card1);
                    if (!zCards.isEmpty()) {
                        return R.error("充值卡号重复,第" + num + "行");
                    }
                    String password = card.get("password").trim();
                    String rechargeMoney = card.get("rechargeMoney").trim();
                    if (StringUtils.hasText(card1) && StringUtils.hasText(password) && StringUtils.hasText(rechargeMoney)) {
                        SysrechargeCardEntity rechargeCard = new SysrechargeCardEntity();
                        rechargeCard.setCardNo(card1);
                        rechargeCard.setPassword(password);
                        rechargeCard.setRechargeMoney(new BigDecimal(rechargeMoney));
                        rechargeCard.setStatus(0);
                        rechargeCard.setCreatedTime(DateUtils.currentUnixTime());

                        cardlist.add(rechargeCard);
                    }else{
                        return R.error("信息不完整,第" + num + "行");
                    }
                    count++;
                    num++;
                }
            }
            sysrechargeCardService.saveBatch(cardlist);

            List<Map<String, String>> errorCards = map.get("errorSheet1");
            return R.ok("成功写入卡号" + count + "条", errorCards);

        }catch (Exception e){

        }
        return  R.ok();

    }

    private ExcelConfig config() {
        ExcelConfig excelConfig = new ExcelConfig();

//            对应excel 中的所有字段 即使数据库没有对应的字段也需要指定  顺序必须跟表左右一致
        String[] cols = {"cardNo","password","rechargeMoney"};
//             字段校验规则  不需要校验 不声明即可
        CellRule cardRule = new CellRule.RuleBuilder().isStr(128).builder();
        CellRule keyRule = new CellRule.RuleBuilder().isStr(128).builder();
        CellRule moneyRule = new CellRule.RuleBuilder().isNumber(11).builder();



        Map<String, CellRule> ruleMap = new HashMap<>();
//             key 为 setColumnNames() 中对应的字段名
        ruleMap.put(cols[0], cardRule);
        ruleMap.put(cols[1], keyRule);
        ruleMap.put(cols[2], moneyRule);


        excelConfig.setRules(ruleMap);
        excelConfig.setColumnNames(cols);
//            excel 数据（非表头）起止行 行号-1
        excelConfig.setRowNum(1);
        return excelConfig;
    }
    @RequestMapping("/setDisabled")
    public R setDisabled(Integer id){
       SysrechargeCardEntity sysrechargeCard = sysrechargeCardService.getById(id);
       if(sysrechargeCard != null){
           sysrechargeCard.setDisabled(sysrechargeCard.getDisabled() == 0 ? 1 : 0);
           sysrechargeCardService.updateById(sysrechargeCard);
       }else {
           return R.error("系统异常,请联系平台!");
       }
       return R.ok();
    }




}
