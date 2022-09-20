package com.yanwo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanwo.entity.SysnotifyMessageEntity;
import com.yanwo.entity.SystradeOrderEntity;
import com.yanwo.service.SysnotifyMessageService;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.Query;
import com.yanwo.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
@Api(tags = "系统消息接口")
public class ApiSysnotifyMessageController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(ApiShoppingCartController.class);
    @Autowired
    private SysnotifyMessageService sysnotifyMessageService;

    @PostMapping("/message")
    @ApiOperation("系统消息列表")
    public R message(@RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "1") Integer pageCurrent) {
        try {

            Page<SysnotifyMessageEntity> pageW = new Page();
            pageW.setSize(pageSize);
            pageW.setCurrent(pageCurrent);
            QueryWrapper<SysnotifyMessageEntity> messageWrapper = new QueryWrapper<>();
            messageWrapper.orderByDesc("message_id");
            IPage iPage = sysnotifyMessageService.page(pageW, messageWrapper);
            return R.okput(iPage);
        } catch (Exception e) {
            log.info("获取系统消息列表发生异常", e);
            return R.error();
        }

    }

    @PostMapping("/info")
    @ApiOperation("系统消息详情")
    public R info(Integer messageId) {
        try {
            QueryWrapper<SysnotifyMessageEntity> messageWrapper = new QueryWrapper<>();
            messageWrapper.eq("message_id", messageId);
            SysnotifyMessageEntity message = sysnotifyMessageService.getOne(messageWrapper);
            return R.okput(message);
        } catch (Exception e) {
            log.info("获取系统消息详情发生异常", e);
            return R.error();
        }
    }

}
