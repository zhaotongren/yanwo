package com.yanwo.modules.controller.capital;

import com.yanwo.modules.service.SyscapitalCapitalDetailService;
import com.yanwo.modules.service.SyscapitalCapitalIntegralService;
import com.yanwo.modules.service.SyscapitalCapitalService;
import com.yanwo.utils.PageUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-17 10:10:25
 */
@RestController
@RequestMapping("sys/syscapitalcapitalintegral")
public class SyscapitalCapitalntegralController {
    @Autowired
    private SyscapitalCapitalIntegralService syscapitalCapitalIntegralService;

    @RequestMapping("/list")
    @RequiresPermissions("sys:syscapitalcapitalintegral:list")
    public PageUtils list(@RequestParam Map<String, Object> params){
        PageUtils page=syscapitalCapitalIntegralService.queryPage(params);
        return page;
    }

}
