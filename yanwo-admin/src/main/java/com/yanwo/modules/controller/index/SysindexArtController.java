package com.yanwo.modules.controller.index;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.common.annotation.SysLog;
import com.yanwo.entity.SysindexArtEntity;
import com.yanwo.modules.service.SysindexArtService;
import com.yanwo.utils.DateUtils;
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
 * @date 2019-09-05 10:55:05
 */
@RestController
@RequestMapping("sys/sysindexart")
public class SysindexArtController {
    @Autowired
    private SysindexArtService sysindexArtService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:sysindexart:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysindexArtService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:sysindexart:info")
    public R info(@PathVariable("id") Integer id){
        SysindexArtEntity sysindexArt = sysindexArtService.getById(id);

        return R.ok().put("sysindexArt", sysindexArt);
    }

    /**
     * 加盟协议
     */
    @RequestMapping("/agreement")
    @RequiresPermissions("sys:sysindexart:agreement")
    public R agreement(){
        SysindexArtEntity sysindexArt = new SysindexArtEntity();
        QueryWrapper<SysindexArtEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type","3");
        queryWrapper.eq("enable_flag",0);
        List<SysindexArtEntity> list = sysindexArtService.list(queryWrapper);
        if(list != null && list.size() > 0){
            sysindexArt = list.get(0);
        }
        return R.ok().put("sysindexArt", sysindexArt);
    }

    /**
     * 保存
     */
    @SysLog("保存文章")
    @RequestMapping("/save")
    @RequiresPermissions("sys:sysindexart:save")
    public R save(@RequestBody SysindexArtEntity sysindexArt){
        sysindexArt.setUpdateTime(DateUtils.currentUnixTime());
        sysindexArtService.save(sysindexArt);

        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("修改文章")
    @RequestMapping("/update")
    @RequiresPermissions("sys:sysindexart:update")
    public R update(@RequestBody SysindexArtEntity sysindexArt){
        ValidatorUtils.validateEntity(sysindexArt);
        sysindexArt.setUpdateTime(DateUtils.currentUnixTime());
        sysindexArtService.updateById(sysindexArt);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("删除文章")
    @RequestMapping("/delete")
    @RequiresPermissions("sys:sysindexart:delete")
    public R delete(@RequestBody Integer[] ids){
        sysindexArtService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
