package com.yanwo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.entity.SyscategoryCatEntity;
import com.yanwo.entity.SysitemItemEntity;
import com.yanwo.entity.SysitemSkuEntity;
import com.yanwo.service.SysCatService;
import com.yanwo.service.SysitemItemService;
import com.yanwo.service.SysitemSkuService;
import com.yanwo.utils.R;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cat")
@Api(tags="商品接口")
public class ApiCatController extends BaseController {

    @Autowired
    private SysCatService sysCatService;
    @Autowired
    private SysitemItemService sysitemItemService;
    @Autowired
    private SysitemSkuService sysitemSkuService;

    /**
     * 分类列表
     *
     * @return
     */
    @RequestMapping("/catList")
    public R catList(@RequestParam Map queryMap) {
        Map map= new HashMap<>();

        QueryWrapper<SyscategoryCatEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",0);
        queryWrapper.orderByAsc("order_sort");
        List<SyscategoryCatEntity> parentList = sysCatService.list(queryWrapper);

        QueryWrapper<SyscategoryCatEntity> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.orderByAsc("order_sort");
        List<SyscategoryCatEntity> catList = sysCatService.list(queryWrapper2);

        List listcat1 = new ArrayList();
        for(SyscategoryCatEntity cat:parentList){
            Map mapcat1 = new HashMap();
            mapcat1.put("catId", cat.getCatId());
            mapcat1.put("catName", cat.getCatName());
            mapcat1.put("catImg", cat.getCatLogo());
            List listcat2 = new ArrayList();
            for(SyscategoryCatEntity syscategoryCat:catList){
                Map mapcat2 = new HashMap();
                if(syscategoryCat.getParentId().equals(cat.getCatId())){
                    QueryWrapper<SysitemItemEntity> itemWrapper = new QueryWrapper<>();
                    itemWrapper.eq("cat_id",syscategoryCat.getCatId());
                    if(queryMap.containsKey("price")){
                        if("0".equals(queryMap.get("price").toString())){//倒序
                            itemWrapper.orderByDesc("max_price");
                        }else{
                            itemWrapper.orderByAsc("min_price");
                        }
                    }
                    if(queryMap.containsKey("soldNum")){
                        if("0".equals(queryMap.get("soldNum").toString())){//倒序
                            itemWrapper.orderByDesc("total_sales");
                        }else{
                            itemWrapper.orderByAsc("total_sales");
                        }
                    }
                    itemWrapper.eq("approve_status",2);
                    List<SysitemItemEntity> list = sysitemItemService.list(itemWrapper);
                    List itemList=new ArrayList();
                    for(SysitemItemEntity sysitemItemEntity:list){
                        QueryWrapper<SysitemSkuEntity> skuWrapper = new QueryWrapper<>();
                        skuWrapper.eq("item_id",sysitemItemEntity.getItemId());
                        List<SysitemSkuEntity> skus = sysitemSkuService.list(skuWrapper);
                        Map iMap=new HashMap<>();

                        iMap.put("itemId",sysitemItemEntity.getItemId());
                        iMap.put("store",sysitemItemService.itemStore(sysitemItemEntity.getItemId()));
                        iMap.put("title",sysitemItemEntity.getTitle());
                        iMap.put("imageDefault",sysitemItemEntity.getImageDefault());
                        iMap.put("maxPrice",sysitemItemEntity.getMaxPrice());
                        iMap.put("minPrice",sysitemItemEntity.getMinPrice());
                        if(1==sysitemItemEntity.getItemType()){
                            iMap.put("maxIntegral",sysitemItemEntity.getMaxIntegral());
                            iMap.put("minIntegral",sysitemItemEntity.getMinIntegral());
                        }
                        BigDecimal b = new BigDecimal(0);
                        for(SysitemSkuEntity skuEntity:skus){
                            if(b.compareTo(new BigDecimal(0))==0){
                                b=skuEntity.getMktPrice();
                            }
                            if(skuEntity.getMktPrice().compareTo(b)==-1){
                                b=skuEntity.getMktPrice();
                            }
                        }
                        iMap.put("mktPrice",b);
                        iMap.put("itemType",sysitemItemEntity.getItemType());
                        iMap.put("soldNum",sysitemItemEntity.getTotalSales());
                        iMap.put("createdTime",sysitemItemEntity.getCreatedTime());
                        iMap.put("grade",sysitemItemService.itemRate(sysitemItemEntity.getItemId()));


                        List skuList=new ArrayList();
                        for(SysitemSkuEntity sysitemSkuEntity:skus){
                            if(null!=sysitemSkuEntity){
                                Map sMap=new HashMap<>();
                                sMap.put("itemId",sysitemSkuEntity.getItemId());
                                sMap.put("skuId",sysitemSkuEntity.getSkuId());
                                sMap.put("title",sysitemSkuEntity.getTitle());
                                sMap.put("img",sysitemSkuEntity.getImg());
                                sMap.put("price",sysitemSkuEntity.getPrice());
                                sMap.put("costPrice",sysitemSkuEntity.getCostPrice());
                                sMap.put("mktPrice",sysitemSkuEntity.getMktPrice());
                                sMap.put("store",sysitemSkuEntity.getStore());
                                sMap.put("integral",sysitemSkuEntity.getIntegral());
                                skuList.add(sMap);
                            }
                            iMap.put("skus",skuList);
                        }
                        itemList.add(iMap);
                    }

                    mapcat2.put("catId", syscategoryCat.getCatId());
                    mapcat2.put("catName", syscategoryCat.getCatName());
                    mapcat2.put("catImg", syscategoryCat.getCatLogo());
                    mapcat2.put("items",itemList);

                    listcat2.add(mapcat2);
                }else{
                    mapcat1.put("cat",listcat2);
                    continue;
                }
                mapcat1.put("cat",listcat2);
            }
            listcat1.add(mapcat1);
        }
        map.put("cateData",listcat1);

        return R.okput(map);
    }

}
