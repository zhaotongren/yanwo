package com.yanwo.modules.controller.sys;

import java.util.Arrays;
import java.util.Map;


import com.yanwo.entity.SysnotifyMessageEntity;
import com.yanwo.modules.service.SysnotifyMessageService;
import com.yanwo.utils.GUtils;
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
 * @date 2019-09-12 15:18:01
 */
@RestController
@RequestMapping("sys/sysnotifymessage")
public class SysnotifyMessageController {
    @Autowired
    private SysnotifyMessageService sysnotifyMessageService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:sysnotifymessage:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysnotifyMessageService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{messageId}")
    @RequiresPermissions("sys:sysnotifymessage:info")
    public R info(@PathVariable("messageId") Long messageId){
        SysnotifyMessageEntity sysnotifyMessage = sysnotifyMessageService.getById(messageId);

        return R.ok().put("sysnotifyMessage", sysnotifyMessage);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:sysnotifymessage:save")
    public R save(@RequestBody SysnotifyMessageEntity sysnotifyMessage){
        sysnotifyMessage.setCreatedTime(GUtils.getCurrentTimestamp().intValue());
        sysnotifyMessageService.save(sysnotifyMessage);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:sysnotifymessage:update")
    public R update(@RequestBody SysnotifyMessageEntity sysnotifyMessage){
        ValidatorUtils.validateEntity(sysnotifyMessage);
        sysnotifyMessageService.updateById(sysnotifyMessage);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:sysnotifymessage:delete")
    public R delete(@RequestBody Long[] messageIds){
        sysnotifyMessageService.removeByIds(Arrays.asList(messageIds));

        return R.ok();
    }

}
