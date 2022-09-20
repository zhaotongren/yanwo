package com.yanwo.modules.controller.questions;

import java.util.Arrays;
import java.util.Map;

import com.yanwo.modules.service.SysQuestionsService;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yanwo.entity.SysQuestionsEntity;

/**
 * 问答表
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-18 10:32:32
 */
@RestController
@RequestMapping("yanwo/sysquestions")
public class SysQuestionsController {
    @Autowired
    private SysQuestionsService sysQuestionsService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("yanwo:sysquestions:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysQuestionsService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{questionsId}")
    public R info(@PathVariable("questionsId") Integer questionsId){
        SysQuestionsEntity sysQuestions = sysQuestionsService.getById(questionsId);

        return R.ok().put("sysQuestions", sysQuestions);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SysQuestionsEntity sysQuestions){
        sysQuestions.setCreateTime(DateUtils.currentUnixTime());
        sysQuestions.setStatus(1);
        sysQuestionsService.save(sysQuestions);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SysQuestionsEntity sysQuestions){

        sysQuestionsService.updateById(sysQuestions);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] questionsIds){
        sysQuestionsService.removeByIds(Arrays.asList(questionsIds));

        return R.ok();
    }

}
