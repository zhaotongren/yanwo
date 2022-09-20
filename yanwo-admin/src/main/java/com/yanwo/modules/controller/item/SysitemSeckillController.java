package com.yanwo.modules.controller.item;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import com.yanwo.entity.SysitemItemEntity;
import com.yanwo.entity.SysitemSkuEntity;
import com.yanwo.modules.service.SysitemItemService;
import com.yanwo.modules.service.SysitemSeckillService;
import com.yanwo.modules.service.SysitemSkuService;
import com.yanwo.utils.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yanwo.entity.SysitemSeckillEntity;



/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-06-04 09:41:54
 */
@RestController
@RequestMapping("sys/sysitemseckill")
public class SysitemSeckillController {
    @Autowired
    private SysitemSeckillService sysitemSeckillService;

    @Autowired
    private SysitemItemService sysitemItemService;

    @Autowired
    protected RedisUtils redisUtils;

    @Autowired
    private SysitemSkuService sysitemSkuService;


    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:sysitemseckill:list")
    public R list(@RequestParam Map<String, Object> params,@RequestParam(defaultValue = "10") long pageSize, @RequestParam(defaultValue = "1") long pageCurrent) {
        PageUtils page = sysitemSeckillService.queryList(params,pageSize,pageCurrent);
        return R.ok().put("page",page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:sysitemseckill:info")
    public R info(@PathVariable("id") Long id){
        SysitemSeckillEntity seckill = sysitemSeckillService.getById(id);
        ModelMap model = new ModelMap();
        model.put("id",seckill.getId());
        model.put("goodsTitle",seckill.getGoodsTitle());
        model.put("goodsSpec",seckill.getGoodsSpec());
        model.put("seckillPrice",seckill.getSeckillPrice());
        model.put("seckillStock",seckill.getSeckillStock());
        model.put("goodsPrice",seckill.getGoodsPrice());
        model.put("goodsImg",seckill.getGoodsImg());
        model.put("startTime", GUtils.IntegerToDate(seckill.getStartTime()));
        model.put("endTime",GUtils.IntegerToDate(seckill.getEndTime()));
        model.put("status",seckill.getStatus());
        SysitemSkuEntity skuEntity = sysitemSkuService.getById(seckill.getSkuId());
        model.put("goodsStock",skuEntity.getStore());
        return R.ok().put("sysitemSeckill", model);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:sysitemseckill:save")
    public R save(@RequestBody SysitemSeckillEntity sysitemSeckill){
        sysitemSeckillService.save(sysitemSeckill);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:sysitemseckill:update")
    public R update(@RequestBody SysitemSeckillEntity sysitemSeckill){
        SysitemSeckillEntity seckill = sysitemSeckillService.getById(sysitemSeckill.getId());
        if(seckill != null){
            SysitemSkuEntity skuEntity = sysitemSkuService.getById(seckill.getSkuId());
            //判断秒杀时间
            if(sysitemSeckill.getSeckillPrice() == null || sysitemSeckill.getSeckillPrice().compareTo(BigDecimal.ZERO) == -1){
                return R.error("请填正确的秒杀价");
            }
            if(sysitemSeckill.getSeckillPrice().compareTo(skuEntity.getPrice()) == 1){
                return R.error("秒杀价不能大于销售价:"+skuEntity.getPrice());
            }
            if(sysitemSeckill.getSeckillStock() == null || sysitemSeckill.getSeckillStock() <= 0){
                return R.error("请填正确的秒杀库存");
            }
            if(sysitemSeckill.getSeckillStock() > skuEntity.getStore()){
                return R.error("秒杀库存不能大于总库存量:"+skuEntity.getStore());
            }
            if(sysitemSeckill.getEndTime() ==  null || sysitemSeckill.getStartTime() == null){
                return R.error("请填写完整的秒杀时间");
            }
            if(sysitemSeckill.getEndTime() <= DateUtils.currentUnixTime()){
                return R.error("请填写正确的结束时间");
            }
            if(sysitemSeckill.getEndTime() <= sysitemSeckill.getStartTime()){
                return R.error("请填写正确秒杀时间");
            }
            sysitemSeckillService.updateById(sysitemSeckill);
            //数据添加到redis
            //距离开始时长
            redisUtils.set("seckill_start_"+seckill.getId(),sysitemSeckill,sysitemSeckill.getStartTime() - DateUtils.currentUnixTime());
            //距离结束时长
            redisUtils.set("seckill_end_"+seckill.getId(),sysitemSeckill,sysitemSeckill.getEndTime() - DateUtils.currentUnixTime());
        }else{
            return R.error("数据异常，请联系管理员");
        }
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:sysitemseckill:delete")
    public R delete(@RequestBody Long[] ids){
        for(Long id : ids){
            SysitemSeckillEntity seckillEntity = sysitemSeckillService.getById(id);
            //只有未开始的商品 才能移除
            if(seckillEntity.getStatus() == 0){
                SysitemItemEntity itemEntity = sysitemItemService.getById(seckillEntity.getItemId());
                itemEntity.setIsSeckill(0);
                sysitemItemService.updateById(itemEntity);
                //删除秒杀数据  还未开始  留着没啥用
                sysitemSeckillService.removeById(id);
            }else{
                return R.error("只有未开始的状态才能移除");
            }

        }
        return R.ok();
    }

    /**
     * 关闭
     */
    @RequestMapping("/close")
    @RequiresPermissions("sys:sysitemseckill:close")
    public R close(@RequestBody Long[] ids){
        for(Long id : ids){
            SysitemSeckillEntity seckillEntity = sysitemSeckillService.getById(id);
            if(seckillEntity.getStatus() == 1){
                seckillEntity.setStatus(3);
                sysitemSeckillService.updateById(seckillEntity);

                SysitemItemEntity itemEntity = sysitemItemService.getById(seckillEntity.getItemId());
                itemEntity.setIsSeckill(0);
                sysitemItemService.updateById(itemEntity);

                //删除redis数据
                redisUtils.delete("miaosha_" + seckillEntity.getId());
                redisUtils.delete("seckill_end_" + seckillEntity.getId());
            }else{
                return R.error("只有秒杀中的状态才能关闭");
            }
        }
        return R.ok();
    }

}
