package com.yanwo.modules.controller.index;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.yanwo.common.annotation.SysLog;
import com.yanwo.entity.SysindexAdEntity;
import com.yanwo.modules.service.SysindexAdService;
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
 * @date 2019-08-31 15:23:48
 */
@RestController
@RequestMapping("sys/sysindexad")
public class SysindexAdController {
    @Autowired
    private SysindexAdService sysindexAdService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:sysindexad:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysindexAdService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:sysindexad:info")
    public R info(@PathVariable("id") Integer id){
        SysindexAdEntity sysindexAd = sysindexAdService.getById(id);

        return R.ok().put("sysindexAd", sysindexAd);
    }

    /**
     * 保存
     */
    @SysLog("保存轮播图")
    @RequestMapping("/save")
    @RequiresPermissions("sys:sysindexad:save")
    public R save(@RequestBody SysindexAdEntity sysindexAd){
        sysindexAdService.save(sysindexAd);

        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("修改轮播图")
    @RequestMapping("/update")
    @RequiresPermissions("sys:sysindexad:update")
    public R update(@RequestBody SysindexAdEntity sysindexAd){
        ValidatorUtils.validateEntity(sysindexAd);
        sysindexAdService.updateById(sysindexAd);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("删除轮播图")
    @RequestMapping("/delete")
    @RequiresPermissions("sys:sysindexad:delete")
    public R delete(@RequestBody Integer[] ids){
        for(Integer id:ids){
            List<SysindexAdEntity> list = sysindexAdService.list();
            if(list.size()<=2){
                return R.error("数据不能少于两条");
            }
            sysindexAdService.removeById(id);
        }

        return R.ok();
    }

}
