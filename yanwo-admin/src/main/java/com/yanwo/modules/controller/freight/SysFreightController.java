package com.yanwo.modules.controller.freight;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.entity.SysFreightEntity;
import com.yanwo.entity.SysitemItemEntity;
import com.yanwo.modules.service.SysFreightService;
import com.yanwo.modules.service.SysitemItemService;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 运费模板
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-14 11:49:07
 */
@RestController
@RequestMapping("sys/sysfreight")
public class SysFreightController {

    @Autowired
    private SysFreightService sysFreightService;
    @Autowired
    private SysitemItemService sysitemItemService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:sysfreight:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysFreightService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{freightId}")
    public R info(@PathVariable("freightId") Integer freightId){
        SysFreightEntity sysFreight = sysFreightService.getById(freightId);

        return R.ok().put("sysFreight", sysFreight);
    }

    /**
     * 保存
     */
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public R save(@RequestBody SysFreightEntity sysFreight){
        sysFreight.setStatus(1);
        sysFreightService.save(sysFreight);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public R update(@RequestBody SysFreightEntity sysFreight){
        sysFreight.setStatus(1);
        sysFreightService.updateById(sysFreight);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] freightIds){
        for(Integer id:freightIds){
            QueryWrapper<SysitemItemEntity> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("freight_id",id);
            List<SysitemItemEntity> list = sysitemItemService.list(queryWrapper);
            if(!list.isEmpty()){
                SysFreightEntity freight = sysFreightService.getById(id);
                return R.error("此运费模板:"+freight.getName()+",已关联商品不可删除");
            }
            sysFreightService.removeById(id);
        }

        return R.ok();
    }

    /**
     * 运费模板列表（添加商品）
     */
    @RequestMapping(value = "/freightList",method = RequestMethod.GET)
    public R freightList(){
        List<SysFreightEntity> list = sysFreightService.list();
        return R.ok().put("freights",list);
    }

}
