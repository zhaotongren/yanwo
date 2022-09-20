package com.yanwo.modules.controller.capital;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.yanwo.entity.SyscapitalCapitalDetailEntity;
import com.yanwo.entity.SyscapitalCapitalEntity;
import com.yanwo.modules.service.SyscapitalCapitalDetailService;
import com.yanwo.modules.service.SyscapitalCapitalService;
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
import org.springframework.web.servlet.ModelAndView;


/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-17 10:10:25
 */
@RestController
@RequestMapping("sys/syscapitalcapitaldetail")
public class SyscapitalCapitalDetailController {
    @Autowired
    private SyscapitalCapitalDetailService syscapitalCapitalDetailService;

    @Autowired
    private SyscapitalCapitalService syscapitalCapitalService;

    @RequestMapping("/list")
    @RequiresPermissions("sys:syscapitalcapitaldetail:list")
    public PageUtils list(@RequestParam Map<String, Object> params){
        PageUtils page=syscapitalCapitalDetailService.findDetailList(params);
        return page;
    }

}
