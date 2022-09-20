package com.yanwo.modules.controller.sys;

import java.util.Arrays;
import java.util.Map;

import com.yanwo.entity.SystradeOrderEntity;
import com.yanwo.modules.service.SystradeOrderService;
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
 * @date 2019-09-04 16:51:45
 */
@RestController
@RequestMapping("sys/systradeorder")
public class SystradeOrderController {
    @Autowired
    private SystradeOrderService systradeOrderService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:systradeorder:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = systradeOrderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{oid}")
    @RequiresPermissions("sys:systradeorder:info")
    public R info(@PathVariable("oid") Long oid){
        SystradeOrderEntity systradeOrder = systradeOrderService.getById(oid);

        return R.ok().put("systradeOrder", systradeOrder);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:systradeorder:save")
    public R save(@RequestBody SystradeOrderEntity systradeOrder){
        systradeOrderService.save(systradeOrder);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:systradeorder:update")
    public R update(@RequestBody SystradeOrderEntity systradeOrder){
        ValidatorUtils.validateEntity(systradeOrder);
        systradeOrderService.updateById(systradeOrder);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:systradeorder:delete")
    public R delete(@RequestBody Long[] oids){
        systradeOrderService.removeByIds(Arrays.asList(oids));

        return R.ok();
    }

}
