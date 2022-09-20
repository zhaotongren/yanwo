package com.yanwo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.entity.SysQuestionsEntity;
import com.yanwo.entity.SystradeTradeEntity;
import com.yanwo.entity.UserEntity;
import com.yanwo.service.SysQuestionsService;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/questions")
@Api(tags = "问答接口")
public class ApiQuestionsController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ApiQuestionsController.class);

    @Autowired
    SysQuestionsService sysQuestionsService;

    @PostMapping("/questionsList")
    @ApiOperation("问答列表")
    public R questionsList(@RequestParam(defaultValue = "1") Integer page) {
        try {
            QueryWrapper<SysQuestionsEntity> queryWrapper=new QueryWrapper<>();
            PageUtils pages = sysQuestionsService.queryPage(queryWrapper,page);
            return R.okput(pages);
        } catch (Exception e) {
            logger.info("获取问答列表发生异常", e);
            return R.error();
        }
    }


}
