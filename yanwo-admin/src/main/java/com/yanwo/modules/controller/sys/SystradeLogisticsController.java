package com.yanwo.modules.controller.sys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.yanwo.entity.SystradeLogisticsEntity;
import com.yanwo.modules.service.SystradeLogisticsService;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;
import com.yanwo.validator.ValidatorUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-05 18:17:58
 */
@RestController
@RequestMapping("sys/systradelogistics")
public class SystradeLogisticsController {
    @Autowired
    private SystradeLogisticsService systradeLogisticsService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:systradelogistics:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = systradeLogisticsService.queryPage(params);

        return R.ok().put("page", page);
    }
    @RequestMapping("/list2")
    public R getList(){
        List list = systradeLogisticsService.list();

        return R.ok().put("list", list);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{eid}")
    @RequiresPermissions("sys:systradelogistics:info")
    public R info(@PathVariable("eid") Long eid){
        SystradeLogisticsEntity systradeLogistics = systradeLogisticsService.getById(eid);

        return R.ok().put("systradeLogistics", systradeLogistics);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:systradelogistics:save")
    public R save(@RequestBody SystradeLogisticsEntity systradeLogistics){
        systradeLogisticsService.save(systradeLogistics);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:systradelogistics:update")
    public R update(@RequestBody SystradeLogisticsEntity systradeLogistics){
        ValidatorUtils.validateEntity(systradeLogistics);
        systradeLogisticsService.updateById(systradeLogistics);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:systradelogistics:delete")
    public R delete(@RequestBody Long[] eids){
        systradeLogisticsService.removeByIds(Arrays.asList(eids));

        return R.ok();
    }

}
