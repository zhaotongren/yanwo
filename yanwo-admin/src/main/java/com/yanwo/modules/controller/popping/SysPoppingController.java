package com.yanwo.modules.controller.popping;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.modules.service.SysPoppingService;
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

import com.yanwo.entity.SysPoppingEntity;




/**
 * 弹窗
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-06-08 11:06:51
 */
@RestController
@RequestMapping("yanwo/syspopping")
public class SysPoppingController {
    @Autowired
    private SysPoppingService sysPoppingService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("yanwo:syspopping:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysPoppingService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{poppingId}")
    @RequiresPermissions("yanwo:syspopping:info")
    public R info(@PathVariable("poppingId") Integer poppingId){
        SysPoppingEntity sysPopping = sysPoppingService.getById(poppingId);

        return R.ok().put("sysPopping", sysPopping);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("yanwo:syspopping:save")
    public R save(@RequestBody SysPoppingEntity sysPopping){
        QueryWrapper<SysPoppingEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status",0);
        List<SysPoppingEntity> list = sysPoppingService.list(queryWrapper);
        if (!list.isEmpty()){
            return R.error("当前有正在使用的弹窗，请先关闭！");
        }
        sysPopping.setCreateTime(DateUtils.currentUnixTime());
        sysPoppingService.save(sysPopping);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("yanwo:syspopping:update")
    public R update(@RequestBody SysPoppingEntity sysPopping){
        ValidatorUtils.validateEntity(sysPopping);
        SysPoppingEntity byId = sysPoppingService.getById(sysPopping.getPoppingId());
        QueryWrapper<SysPoppingEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status",0).ne("popping_id",sysPopping.getPoppingId());
        List<SysPoppingEntity> list = sysPoppingService.list(queryWrapper);
        if (!list.isEmpty()){
            return R.error("当前有正在使用的弹窗，请先关闭");
        }
        sysPoppingService.updateById(sysPopping);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("yanwo:syspopping:delete")
    public R delete(@RequestBody Integer[] poppingIds){
        sysPoppingService.removeByIds(Arrays.asList(poppingIds));

        return R.ok();
    }

}
