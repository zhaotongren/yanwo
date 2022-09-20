package com.yanwo.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanwo.entity.*;
import com.yanwo.service.SysitemItemService;
import com.yanwo.service.SysitemSkuService;
import com.yanwo.service.SysrateTraderateService;
import com.yanwo.service.UserService;
import com.yanwo.utils.GUtils;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/item")
@Api(tags="商品接口")
public class ApiItemController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(ApiShoppingCartController.class);
    @Autowired
    private SysitemItemService sysitemItemService;
    @Autowired
    private SysitemSkuService sysitemSkuService;
    @Autowired
    private SysrateTraderateService sysrateTraderateService;

    @Autowired
    private UserService userService;


    @PostMapping("/info")
    @ApiOperation("商品详情")
    public R info(Integer itemId) {
        Map map=new HashMap<>();
        try {
            //商品基本信息
            SysitemItemEntity sysitemItemEntity=sysitemItemService.getById(itemId);
            if (sysitemItemEntity==null||sysitemItemEntity.getApproveStatus()!=2){
                return R.error("该商品已下架！");
            }
            if(sysitemItemEntity.getMaxPrice().compareTo(sysitemItemEntity.getMinPrice())==0){
                sysitemItemEntity.setPrice(sysitemItemEntity.getMinPrice().toString());
            }else{
                sysitemItemEntity.setPrice(String.format("%.2f", sysitemItemEntity.getMinPrice())+"~"+String.format("%.2f",sysitemItemEntity.getMaxPrice()));
            }
            sysitemItemEntity = setIntgerToDate(sysitemItemEntity);//解码
            //规格信息
            QueryWrapper<SysitemSkuEntity> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("item_id",itemId);
            List<SysitemSkuEntity> list=sysitemSkuService.list(queryWrapper);
            sysitemItemEntity.setSysitemSkuEntities(list);
            //前台展示销售量 = 实际销售量 + 自定义销售量
            sysitemItemEntity.setSoldNum(sysitemItemEntity.getTotalSales());

            map.put("item",sysitemItemEntity);

            List rateList=new ArrayList<>();
            List<SysrateTraderateEntity> rates=sysrateTraderateService.selectRate(itemId);
            Integer rateNum=sysrateTraderateService.countRate(itemId);
            for(SysrateTraderateEntity rate:rates){
                Map rateMap=new HashMap<>();
                UserEntity user = userService.getById(rate.getUserId());
                rateMap.put("nickName",URLDecoder.decode(user.getNickName(), "utf-8"));
                rateMap.put("headImg",user.getCerHandPic());
                rateMap.put("createTime",rate.getCreatedTime());
                rateMap.put("score",rate.getDescribeScore());
                rateMap.put("content",URLDecoder.decode(rate.getContent(),"utf-8"));
                rateMap.put("rateImg",rate.getRatePic()!=null?rate.getRatePic():"");
                rateList.add(rateMap);
            }
            System.out.println(rateList);
            map.put("itemRate",rateList);
            map.put("rateNum",rateNum);
            QueryWrapper<SysitemSkuEntity> wrapper=new QueryWrapper<>();
            wrapper.eq("item_id",sysitemItemEntity.getItemId());
            List<SysitemSkuEntity> list1 = sysitemSkuService.list(wrapper);
            BigDecimal b = new BigDecimal(0);
            for(SysitemSkuEntity skuEntity:list1){
                if(b.compareTo(new BigDecimal(0))==0){
                    b=skuEntity.getMktPrice();
                }
                if(skuEntity.getMktPrice().compareTo(b)==-1){
                    b=skuEntity.getMktPrice();
                }
            }
            map.put("mktPrice",b);

            return  R.okput(map);
        } catch (Exception e) {
            log.info("获取商品详情发生异常", e);
            return R.error();
        }
    }

    @PostMapping("/rateList")
    @ApiOperation("商品评价列表")
    public R rateList(@RequestParam Integer itemId,@RequestParam(defaultValue = "1") Integer currPage) {
        try {
            QueryWrapper<SysrateTraderateEntity> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("item_id",itemId);
            queryWrapper.orderByDesc("created_time");

            PageUtils pages = sysrateTraderateService.queryPage(queryWrapper,currPage);
            return R.okput(pages);
        } catch (Exception e) {
            log.info("获取订单列表发生异常", e);
            return R.error();
        }
    }
    @PostMapping("/recommendList")
    @ApiOperation("商品推荐列表")
    public R recommendList() {
        try {
            QueryWrapper<SysitemItemEntity> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("is_recommend",1);
            queryWrapper.eq("approve_status",2);
            queryWrapper.orderByAsc("item_sort");
            List<SysitemItemEntity> itemList = sysitemItemService.list(queryWrapper);
            List<ModelMap> list = new ArrayList<ModelMap>();
            for(SysitemItemEntity itemEntity : itemList){
                ModelMap model = new ModelMap();
                model.put("itemId",itemEntity.getItemId());
                model.put("title",itemEntity.getTitle());
                model.put("subTitle",itemEntity.getSubTitle());
                model.put("imageDefault",itemEntity.getImageDefault());
                model.put("minPrice",itemEntity.getMinPrice());

                QueryWrapper<SysitemSkuEntity> wrapper=new QueryWrapper<>();
                wrapper.eq("item_id",itemEntity.getItemId());
                List<SysitemSkuEntity> list1 = sysitemSkuService.list(wrapper);
                BigDecimal b = new BigDecimal(0);
                for(SysitemSkuEntity skuEntity:list1){
                    if(b.compareTo(new BigDecimal(0))==0){
                        b=skuEntity.getMktPrice();
                    }
                    if(skuEntity.getMktPrice().compareTo(b)==-1){
                        b=skuEntity.getMktPrice();
                    }
                }
                model.put("mktPrice",b);
                if(1==itemEntity.getItemType()){
                    model.put("minIntegral",itemEntity.getMinIntegral());
                }
                model.put("soldNum",itemEntity.getTotalSales());
                model.put("grade",itemEntity.getGrade());
                model.put("itemType",itemEntity.getItemType());
                list.add(model);
            }
            return R.okput(list);
        } catch (Exception e) {
            log.info("获取商品推荐列表发生异常", e);
            return R.error();
        }
    }

    SysitemItemEntity setIntgerToDate(SysitemItemEntity s) throws UnsupportedEncodingException {

        if (s.getListImage()!=null){
            s.setListImage(URLDecoder.decode(s.getListImage(), "UTF-8"));
        }
        if (s.getDescription()!=null){
            s.setDescription(URLDecoder.decode(s.getDescription(), "UTF-8"));
        }
        return  s;
    }

}
