package com.yanwo.modules.controller.item;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.common.annotation.SysLog;
import com.yanwo.entity.SysitemItemEntity;
import com.yanwo.entity.SysitemSeckillEntity;
import com.yanwo.entity.SysitemSkuEntity;
import com.yanwo.modules.service.SolrService;
import com.yanwo.modules.service.SysitemItemService;
import com.yanwo.modules.service.SysitemSeckillService;
import com.yanwo.modules.service.SysitemSkuService;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;
import com.yanwo.utils.RedisUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-08-31 14:14:58
 */
@RestController
@RequestMapping("sys/sysitemitem")
public class SysitemItemController {
    @Autowired
    private SysitemItemService sysitemItemService;
    @Autowired
    private SysitemSkuService sysitemSkuService;
    @Autowired
    private SysitemSeckillService sysitemSeckillService;
    @Autowired
    SolrService solrService;
    @Autowired
    protected RedisUtils redisUtils;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:sysitemitem:list")
    public R list(@RequestParam Map<String, Object> params,@RequestParam(defaultValue = "10") long pageSize, @RequestParam(defaultValue = "1") long pageCurrent) {
        PageUtils page = sysitemItemService.queryList(params,pageSize,pageCurrent);
        return R.ok().put("page",page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{itemId}")
    public R info(@PathVariable("itemId") Integer itemId) {
        SysitemItemEntity sysitemItem = sysitemItemService.getById(itemId);
        QueryWrapper<SysitemSkuEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_id",itemId);
        List<SysitemSkuEntity> sysitemSkuEntities = sysitemSkuService.list(queryWrapper);
        return R.ok().put("sysitemItem", sysitemItem).put("sku",sysitemSkuEntities);
    }
    /**
     * 判断字符串是否为数字
     * @param str
     * @return
     */
    private static boolean isStrNum(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 保存
     */
    @SysLog("新增商品")
    @RequestMapping("/save")
    public R save(@RequestBody SysitemItemEntity sysitemItem) {
        R r = valid(sysitemItem);
        if(sysitemItem.getCatId()==null){
            return  R.error(500,"请选择分类");
        }
        if (r != null) {
            return r;
        }
        if (StringUtils.isNotBlank(sysitemItem.getListImage())) {
            sysitemItem.setImageDefault(sysitemItem.getListImage().split(",")[0]);
        }
        if(sysitemItem.getCustomSales() != null && !isStrNum(sysitemItem.getCustomSales().toString())){
            return  R.error(500,"自定义销量格式错误");
        }
        BigDecimal maxPrice = new BigDecimal(0);
        BigDecimal minPrice = new BigDecimal(0);
        List<SysitemSkuEntity> sysitemSkuEntities = sysitemItem.getSysitemSkuEntities();
        for(SysitemSkuEntity sysitemSkuEntity:sysitemSkuEntities) {
            if (sysitemSkuEntity.getPrice() == null || sysitemSkuEntity.getCostPrice() == null || sysitemSkuEntity.getMktPrice() == null) {
                return R.error(500, "商品价格请填写完整...");
            }
            if (sysitemSkuEntity.getPrice().compareTo(sysitemSkuEntity.getCostPrice()) == -1) {
                return R.error(500, "销售价必须大于成本价");
            }
            if(sysitemSkuEntity.getPrice().compareTo(maxPrice)==1){
                maxPrice=sysitemSkuEntity.getPrice();
            }
            if(minPrice.compareTo(new BigDecimal(0))==0){
                minPrice=sysitemSkuEntity.getPrice();
            }

            if(minPrice.compareTo(sysitemSkuEntity.getPrice())==1){
                minPrice=sysitemSkuEntity.getPrice();
            }
        }
        sysitemItem.setMaxPrice(maxPrice);
        sysitemItem.setMinPrice(minPrice);
        sysitemItem.setCreatedTime(DateUtils.currentUnixTime());
        sysitemItem.setModifiedTime(DateUtils.currentUnixTime());
        sysitemItem.setTotalSales(sysitemItem.getCustomSales() != null ? sysitemItem.getCustomSales() : 0);
        sysitemItemService.save(sysitemItem);

       for(SysitemSkuEntity sysitemSkuEntity:sysitemSkuEntities){
            sysitemSkuEntity.setItemId(sysitemItem.getItemId());
            sysitemSkuService.save(sysitemSkuEntity);
        }
        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("修改商品")
    @RequestMapping("/update")
    public R update(@RequestBody SysitemItemEntity sysitemItem) {
        R r = valid(sysitemItem);
        if (r != null) {
            return r;
        }
        if (StringUtils.isNotBlank(sysitemItem.getListImage())) {
            sysitemItem.setImageDefault(sysitemItem.getListImage().split(",")[0]);
        }
        if(sysitemItem.getCustomSales() != null && !isStrNum(sysitemItem.getCustomSales().toString())){
            return  R.error(500,"自定义销量格式错误");
        }
        BigDecimal maxPrice = new BigDecimal(0);
        BigDecimal minPrice = new BigDecimal(0);
        List<SysitemSkuEntity> sysitemSkuEntities = sysitemItem.getSysitemSkuEntities();
        for(SysitemSkuEntity sysitemSkuEntity:sysitemSkuEntities) {
            if(sysitemItem.getItemType()==1){//积分商品
                if(sysitemSkuEntity.getIntegral().compareTo(maxPrice)==1){
                    maxPrice=sysitemSkuEntity.getIntegral();
                }
                if(minPrice.compareTo(new BigDecimal(0))==0){
                    minPrice=sysitemSkuEntity.getIntegral();
                }

                if(minPrice.compareTo(sysitemSkuEntity.getIntegral())==1){
                    minPrice=sysitemSkuEntity.getIntegral();
                }
            }else{
                if (sysitemSkuEntity.getPrice() == null || sysitemSkuEntity.getCostPrice() == null || sysitemSkuEntity.getMktPrice() == null) {
                    return R.error(500, "商品价格请填写完整...");
                }
                if (sysitemSkuEntity.getPrice().compareTo(sysitemSkuEntity.getCostPrice()) == -1) {
                    return R.error(500, "销售价必须大于成本价");
                }
                if(sysitemSkuEntity.getPrice().compareTo(maxPrice)==1){
                    maxPrice=sysitemSkuEntity.getPrice();
                }
                if(minPrice.compareTo(new BigDecimal(0))==0){
                    minPrice=sysitemSkuEntity.getPrice();
                }

                if(minPrice.compareTo(sysitemSkuEntity.getPrice())==1){
                    minPrice=sysitemSkuEntity.getPrice();
                }
            }
        }
        sysitemItem.setMaxPrice(maxPrice);
        sysitemItem.setMinPrice(minPrice);
        sysitemItem.setModifiedTime(DateUtils.currentUnixTime());
        SysitemItemEntity item = sysitemItemService.getById(sysitemItem.getItemId());
        sysitemItem.setTotalSales(sysitemItem.getCustomSales() != null ? (sysitemItem.getCustomSales()+item.getSoldNum()) : item.getSoldNum());
        sysitemItemService.updateById(sysitemItem);

        QueryWrapper<SysitemSkuEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_id",sysitemItem.getItemId());
        List<SysitemSkuEntity> list = sysitemSkuService.list(queryWrapper);
        for(SysitemSkuEntity sysitemSkuEntity:list){
            sysitemSkuService.removeById(sysitemSkuEntity.getSkuId());
        }

        for(SysitemSkuEntity sysitemSkuEntity:sysitemSkuEntities){
            sysitemSkuEntity.setItemId(sysitemItem.getItemId());
            sysitemSkuService.save(sysitemSkuEntity);
        }
        try {
            solrService.del2solr(sysitemItem.getItemId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return R.ok();
    }

    /**
     * 修改商品分类
     */
    @SysLog("修改商品分类")
    @RequestMapping("/updateCate")
    public R updateCate(Integer itemId,Integer catId) {
        SysitemItemEntity sysitemItem = sysitemItemService.getById(itemId);
        sysitemItem.setCatId(catId);
        sysitemItemService.updateById(sysitemItem);
        //发布
        Integer [] ids={itemId};
        sysitemItemService.putAway(ids, 2);
        return R.ok("修改成功");
    }

    /**
     * 添加积分商品
     */
    @SysLog("添加积分商品")
    @RequestMapping(value = "/addIntegralItem",method = RequestMethod.POST)
    public R addIntegralItem(@RequestBody SysitemItemEntity sysitemItem) {
        List<SysitemSkuEntity> sysitemSkuEntities = sysitemItem.getSysitemSkuEntities();
        BigDecimal maxPrice = new BigDecimal(0);
        BigDecimal minPrice = new BigDecimal(0);
        for(SysitemSkuEntity sku:sysitemSkuEntities){
            if(null==sku.getIntegral()){
                return R.error(500, "请填写商品积分价");
            }
            sysitemSkuService.updateById(sku);
            if(sku.getIntegral().compareTo(maxPrice)==1){
                maxPrice=sku.getIntegral();
            }
            if(minPrice.compareTo(new BigDecimal(0))==0){
                minPrice=sku.getIntegral();
            }

            if(minPrice.compareTo(sku.getIntegral())==1){
                minPrice=sku.getIntegral();
            }
        }
        SysitemItemEntity item = sysitemItemService.getById(sysitemItem.getItemId());
        item.setMaxIntegral(maxPrice);
        item.setMinIntegral(minPrice);
        item.setItemType(1);
        item.setApproveStatus(1);
        sysitemItemService.updateById(item);
        Integer [] ids={item.getItemId()};
        sysitemItemService.putAway(ids, 1);
        return R.ok("添加成功");
    }

    /**
     * 修改积分商品
     */
    @SysLog("修改积分商品")
    @RequestMapping(value = "/cancelIntegralItem",method = RequestMethod.POST)
    public R cancelIntegralItem(@RequestBody SysitemItemEntity sysitemItem) {
        List<SysitemSkuEntity> sysitemSkuEntities = sysitemItem.getSysitemSkuEntities();
        BigDecimal maxPrice = new BigDecimal(0);
        BigDecimal minPrice = new BigDecimal(0);
        for(SysitemSkuEntity sku:sysitemSkuEntities){
            sysitemSkuService.updateById(sku);
            if(sku.getPrice().compareTo(maxPrice)==1){
                maxPrice=sku.getPrice();
            }
            if(minPrice.compareTo(new BigDecimal(0))==0){
                minPrice=sku.getPrice();
            }

            if(minPrice.compareTo(sku.getPrice())==1){
                minPrice=sku.getPrice();
            }
        }
        SysitemItemEntity item = sysitemItemService.getById(sysitemItem.getItemId());
        item.setMaxPrice(maxPrice);
        item.setMinPrice(minPrice);
        item.setItemType(0);
        item.setApproveStatus(1);
        sysitemItemService.updateById(item);
        Integer [] ids={item.getItemId()};
        sysitemItemService.putAway(ids, 1);
        return R.ok("修改成功");
    }


    /**
     * 删除
     */
    @SysLog("删除商品")
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] itemIds) {
        sysitemItemService.putAway(itemIds, 0);
        return R.ok();
    }

    /**
     * 下架
     */
    @SysLog("商品下架")
    @RequestMapping("/downAway")
    public R downAway(@RequestBody Integer[] ids) {
        sysitemItemService.putAway(ids, 1);
        return R.ok();
    }

    /**
     * 上架
     */
    @SysLog("商品上架")
    @RequestMapping("/putAway")
    public R putAway(@RequestBody Integer[] ids) {
        sysitemItemService.putAway(ids, 2);
        return R.ok();
    }

    R valid(SysitemItemEntity itemEntity) {
        if (itemEntity == null) {
            return R.error(500, "请完善商品信息");
        }
        if (StringUtils.isBlank(itemEntity.getListImage())) {
            return R.error(500, "请选择图片");
        }
        if(itemEntity.getUnit()==null){
            return  R.error(500,"请选择单位");
        }
       if(itemEntity.getApproveStatus()==null){
           return  R.error(500,"请选择商品状态");
       }
        if (StringUtils.isBlank(itemEntity.getTitle())) {
            return R.error(500, "请填写标题");
        }
        if (StringUtils.isBlank(itemEntity.getSubTitle())) {
            return R.error(500, "请填写副标题");
        }
        if(itemEntity.getFreightId()==null){
            return  R.error(500,"请选择运费模板");
        }
        if (StringUtils.isBlank(itemEntity.getDescription())) {
            return R.error(500, "请填写详情");
        }

        return null;
    }
    @SysLog("修改积分商品")
    @RequestMapping(value = "/setRecommend",method = RequestMethod.POST)
    public R setRecommend(@RequestBody SysitemItemEntity sysitemItem) {
        try{
            if(sysitemItem.getItemId() != null){
                SysitemItemEntity itemEntity = sysitemItemService.getById(sysitemItem.getItemId());
                if(itemEntity != null){
                    if(itemEntity.getIsRecommend() == 0){
                        //加入推荐商品
                        itemEntity.setIsRecommend(1);
                        itemEntity.setItemSort(sysitemItem.getItemSort());
                    }else{
                        //取消推荐商品
                        itemEntity.setIsRecommend(0);
                        itemEntity.setItemSort(0);
                    }
                    sysitemItemService.updateById(itemEntity);
                    return R.ok();
                }else {
                    return R.error("数据异常，请联系平台！");
                }
            }else {
                return R.error("数据异常，请联系平台！");
            }

        }catch (Exception e){
            return R.error("操作失败");
        }

    }

    @SysLog("加入秒杀专区")
    @RequestMapping(value = "/addSeckillItem",method = RequestMethod.POST)
    public R addSeckillItem(@RequestBody SysitemItemEntity sysitemItem) {
        List<SysitemSeckillEntity> sysitemSeckills = sysitemItem.getSysitemSeckills();
        for(SysitemSeckillEntity seckill:sysitemSeckills){
            SysitemItemEntity item = sysitemItemService.getById(seckill.getItemId());
            SysitemSkuEntity skuEntity = sysitemSkuService.getById(seckill.getSkuId());
            //秒杀限制
            if(seckill.getSeckillPrice() == null || seckill.getSeckillPrice().compareTo(BigDecimal.ZERO) == -1){
                return R.error("请填正确的秒杀价");
            }
            if(seckill.getSeckillPrice().compareTo(skuEntity.getPrice()) == 1){
                return R.error("秒杀价不能大于销售价:"+skuEntity.getPrice());
            }
            if(seckill.getSeckillStock() == null || seckill.getSeckillStock() <= 0){
                return R.error("请填正确的秒杀库存");
            }
            if(seckill.getSeckillStock() > skuEntity.getStore()){
                return R.error("秒杀库存不能大于总库存量:"+skuEntity.getStore());
            }
            if(seckill.getEndTime() ==  null || seckill.getStartTime() == null){
                return R.error("请填写完整的秒杀时间");
            }
            if(seckill.getEndTime() <= DateUtils.currentUnixTime()){
                return R.error("请填写正确的结束时间");
            }
            if(seckill.getEndTime() <= seckill.getStartTime()){
                return R.error("请填写正确秒杀时间");
            }
            if(seckill.getStartTime() <= DateUtils.currentUnixTime()){
                return R.error("开始时间要大于当前时间");
            }
            seckill.setCreateTime(DateUtils.currentUnixTime());
            seckill.setGoodsPrice(skuEntity.getPrice());
            seckill.setGoodsImg(skuEntity.getImg());
            seckill.setGoodsSpec(skuEntity.getTitle());
            seckill.setGoodsTitle(item.getTitle());
            seckill.setGoodsStock(skuEntity.getStore());
            sysitemSeckillService.save(seckill);
            //更新item
            item.setIsSeckill(1);
            sysitemItemService.updateById(item);
            //距离开始时长
            redisUtils.set("seckill_start_"+seckill.getId(),seckill,seckill.getStartTime() - seckill.getCreateTime());
            //距离结束时长
            redisUtils.set("seckill_end_"+seckill.getId(),seckill,seckill.getEndTime() - seckill.getCreateTime());
        }
        return R.ok("添加成功");
    }

}
