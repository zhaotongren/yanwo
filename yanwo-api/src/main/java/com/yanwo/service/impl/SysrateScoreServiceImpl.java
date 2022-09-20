package com.yanwo.service.impl;

import com.aliyun.opensearch.sdk.dependencies.com.google.common.collect.Maps;
import com.yanwo.entity.*;
import com.yanwo.service.*;
import com.yanwo.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.yanwo.dao.SysrateScoreDao;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service("sysrateScoreService")
public class SysrateScoreServiceImpl extends ServiceImpl<SysrateScoreDao, SysrateScoreEntity> implements SysrateScoreService {

    @Autowired
    private SystradeOrderService systradeOrderService;
    @Autowired
    private SystradeTradeService systradeTradeService;
    @Autowired
    private SysrateTraderateService sysrateTraderateService;
    @Autowired
    private SysitemItemService sysitemItemService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SysrateScoreEntity> page = this.page(
                new Query<SysrateScoreEntity>().getPage(params),
                new QueryWrapper<SysrateScoreEntity>()
        );

        return new PageUtils(page);
    }
    @Override
    public R tradeEvaluate(SysrateScoreEntity sysrateScore,
                           String[] oids,
                           String[] results,
                           String[] contents,
                           List<String> imgs){
        //订单信息
        SystradeTradeEntity trade = systradeTradeService.getById(sysrateScore.getTid());
        sysrateScore.setCreatedTime(DateUtils.currentUnixTime());
        sysrateScore.setDisabled(1);
        this.save(sysrateScore);
        //小订单评价
        for (int i=0;i<oids.length;i++) {
            SystradeOrderEntity order = systradeOrderService.getById(Long.valueOf(oids[i]));
            SysrateTraderateEntity traderate = new SysrateTraderateEntity();
            traderate.setTid(trade.getTid().toString());
            traderate.setOid(oids[i]);
            traderate.setUserId(order.getUserId());
            traderate.setDescribeScore(Integer.valueOf(results[i]));
            traderate.setItemId(order.getItemId());
            if(imgs != null && imgs.size() > 0 && !",".equals(imgs.get(i))){
                System.out.println(imgs.get(i));
                if(imgs.get(i) != null){
                    traderate.setRatePic(imgs.get(i));
                }
            }
            try {
                if (contents != null && contents.length != 0) {
                    if (StringUtils.isBlank(contents[i])) {
                        traderate.setContent(URLEncoder.encode("系统默认好评", "utf-8"));
                    } else {
                        traderate.setContent(URLEncoder.encode(contents[i], "utf-8"));
                    }
                } else {
                    traderate.setContent(URLEncoder.encode("系统默认好评", "utf-8"));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            traderate.setCreatedTime((int)(System.currentTimeMillis()/1000));
            traderate.setModifiedTime((int)(System.currentTimeMillis()/1000));
            traderate.setDisabled(1);
            sysrateTraderateService.save(traderate);
            //更新好评数量
            if("5".equals(results[i])){
                SysitemItemEntity itemEntity = sysitemItemService.getById(order.getItemId());
                itemEntity.setGrade(itemEntity.getGrade() != null ? (itemEntity.getGrade()+1):1);
                sysitemItemService.updateById(itemEntity);
                //更新opensearch
                updateItemCount(itemEntity);
            }
        }
        trade.setBuyerRate(true);//已评价
        systradeTradeService.updateById(trade);
        return R.ok();
    }
    public void updateItemCount(SysitemItemEntity item){
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
        doc.put("total_sales",item.getSoldNum() + item.getCustomSales());
        OpenSearchUtil.add(doc);

    }

}
