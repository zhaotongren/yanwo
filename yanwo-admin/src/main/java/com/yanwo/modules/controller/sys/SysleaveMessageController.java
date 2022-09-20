package com.yanwo.modules.controller.sys;

import java.util.Arrays;
import java.util.Map;

import com.yanwo.entity.SysleaveMessageEntity;
import com.yanwo.modules.service.SysleaveMessageService;
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
 * @date 2019-09-05 14:57:16
 */
@RestController
@RequestMapping("sys/sysleavemessage")
public class SysleaveMessageController {
    @Autowired
    private SysleaveMessageService sysleaveMessageService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:sysleavemessage:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysleaveMessageService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:sysleavemessage:info")
    public R info(@PathVariable("id") Integer id){
        SysleaveMessageEntity sysleaveMessage = sysleaveMessageService.getById(id);

        return R.ok().put("sysleaveMessage", sysleaveMessage);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:sysleavemessage:save")
    public R save(@RequestBody SysleaveMessageEntity sysleaveMessage){
        sysleaveMessageService.save(sysleaveMessage);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:sysleavemessage:update")
    public R update(@RequestBody SysleaveMessageEntity sysleaveMessage){
        ValidatorUtils.validateEntity(sysleaveMessage);
        sysleaveMessageService.updateById(sysleaveMessage);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:sysleavemessage:delete")
    public R delete(@RequestBody Integer[] ids){
        sysleaveMessageService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
