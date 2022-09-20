package com.yanwo.modules.controller.capital;

import java.util.Arrays;
import java.util.Map;

import com.yanwo.common.annotation.SysLog;
import com.yanwo.entity.SyscapitalCapitalWithdrawEntity;
import com.yanwo.modules.service.SyscapitalCapitalWithdrawService;
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
 * 钱包提现记录
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-11 15:38:45
 */
@RestController
@RequestMapping("sys/syscapitalcapitalwithdraw")
public class SyscapitalCapitalWithdrawController {
    @Autowired
    private SyscapitalCapitalWithdrawService syscapitalCapitalWithdrawService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:syscapitalcapitalwithdraw:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = syscapitalCapitalWithdrawService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:syscapitalcapitalwithdraw:info")
    public R info(@PathVariable("id") Integer id){
        SyscapitalCapitalWithdrawEntity syscapitalCapitalWithdraw = syscapitalCapitalWithdrawService.getById(id);

        return R.ok().put("syscapitalCapitalWithdraw", syscapitalCapitalWithdraw);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:syscapitalcapitalwithdraw:save")
    public R save(@RequestBody SyscapitalCapitalWithdrawEntity syscapitalCapitalWithdraw){
        syscapitalCapitalWithdrawService.save(syscapitalCapitalWithdraw);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:syscapitalcapitalwithdraw:update")
    public R update(@RequestBody SyscapitalCapitalWithdrawEntity syscapitalCapitalWithdraw){
        ValidatorUtils.validateEntity(syscapitalCapitalWithdraw);
        syscapitalCapitalWithdrawService.updateById(syscapitalCapitalWithdraw);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:syscapitalcapitalwithdraw:delete")
    public R delete(@RequestBody Integer[] ids){
        syscapitalCapitalWithdrawService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @SysLog("提现审核")
    @RequestMapping("/audit")
    public R audit(@RequestBody Map map){
        try {
            return syscapitalCapitalWithdrawService.audit(map);
        }catch (Exception e){
            return R.error();
        }
    }

}
