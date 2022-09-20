package com.yanwo.controller;

import java.util.Arrays;
import java.util.List;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.entity.SystradeTradeEntity;
import com.yanwo.entity.SysuserUserAddrsEntity;
import com.yanwo.entity.UserEntity;
import com.yanwo.service.SysuserUserAddrsService;
import com.yanwo.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/useraddrs")
@Api(tags = "地址接口")
public class ApiUserAddrsController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(ApiShoppingCartController.class);
    @Autowired
    private SysuserUserAddrsService sysuserUserAddrsService;

    /**
     * 列表
     */
    @PostMapping("/list")
    @ApiOperation("查找用户的地址列表")
    public R list(@RequestHeader("token") String token) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//查看地址需要先登录
                return R.auth("请登录");
            }
            Integer userId = user.getUserId();

            QueryWrapper<SysuserUserAddrsEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            List<SysuserUserAddrsEntity> list = sysuserUserAddrsService.list(queryWrapper);

            return R.okput(list);
        } catch (Exception e) {
            log.info("获取地址列表发生异常", e);
            return R.error();
        }
    }


    /**
     * 信息
     */
    @PostMapping("/info")
    @ApiOperation("查找用户的单个地址详情")
    public R info(@RequestHeader("token") String token, Integer addrId) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//查看地址需要先登录
                return R.auth("请登录");
            }
            SysuserUserAddrsEntity sysuserUserAddrs = sysuserUserAddrsService.getById(addrId);

            return R.ok().put("sysuserUserAddrs", sysuserUserAddrs);
        } catch (Exception e) {
            log.info("获取地址详情发生异常", e);
            return R.error();
        }
    }

    /**
     * 添加
     */
    @PostMapping("/add")
    @ApiOperation("添加地址")
    public R save(@RequestHeader("token") String token, String name,String area,String addr,String mobile,Integer defAddr) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//添加地址需要先登录
                return R.auth("请登录");
            }
            if (!checkIsMobileValid(mobile)) {
                return R.error("请输入正确的手机号码！");
            }
            Integer userId = user.getUserId();
            SysuserUserAddrsEntity sysuserUserAddrs = new SysuserUserAddrsEntity();
            sysuserUserAddrs.setArea(area);
            sysuserUserAddrs.setName(name);
            sysuserUserAddrs.setAddr(addr);
            sysuserUserAddrs.setMobile(mobile);
            sysuserUserAddrs.setDefAddr(defAddr);

            if (sysuserUserAddrs.getDefAddr() == 1) {
                QueryWrapper<SysuserUserAddrsEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_id", userId);
                queryWrapper.eq("def_addr", 1);
                SysuserUserAddrsEntity s = sysuserUserAddrsService.getOne(queryWrapper);
                if (s != null) {
                    s.setDefAddr(0);
                    sysuserUserAddrsService.updateById(s);
                }
            }
            sysuserUserAddrs.setUserId(userId);
            sysuserUserAddrsService.save(sysuserUserAddrs);

            return R.ok();
        } catch (Exception e) {
            log.info("获取保存地址发生异常", e);
            return R.error();
        }
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改地址")
    public R update(@RequestHeader("token") String token,Integer addrId, String name,String area,String addr,String mobile,Integer defAddr) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {
                return R.auth("请登录");
            }
            if (!checkIsMobileValid(mobile)) {
                return R.error("请输入正确的手机号码！");
            }
            SysuserUserAddrsEntity sysuserUserAddrs = sysuserUserAddrsService.getById(addrId);
            sysuserUserAddrs.setArea(area);
            sysuserUserAddrs.setName(name);
            sysuserUserAddrs.setAddr(addr);
            sysuserUserAddrs.setMobile(mobile);
            sysuserUserAddrs.setDefAddr(defAddr);
            Integer userId = user.getUserId();
            if (sysuserUserAddrs.getDefAddr() == 1) {
                QueryWrapper<SysuserUserAddrsEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_id", userId);
                queryWrapper.eq("def_addr", 1);
                SysuserUserAddrsEntity s = sysuserUserAddrsService.getOne(queryWrapper);
                if (s != null) {
                    s.setDefAddr(0);
                    sysuserUserAddrsService.updateById(s);
                }

            }
            sysuserUserAddrsService.updateById(sysuserUserAddrs);

            return R.ok();
        } catch (Exception e) {
            log.info("获取修改地址发生异常", e);
            return R.error();
        }
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除地址")
    public R delete(@RequestHeader("token") String token, Integer addrId) {

        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {
                return R.auth("请登录");
            }
            sysuserUserAddrsService.removeById(addrId);

            return R.ok();
        } catch (Exception e) {
            log.info("获取删除地址发生异常", e);
            return R.error();
        }
    }

    /**
     * 删除
     */
    @PostMapping("/def")
    @ApiOperation("设置默认地址")
    public R def(@RequestHeader("token") String token, Integer addrId) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {
                return R.auth("请登录");
            }
            //先把默认地址改为0
            QueryWrapper<SysuserUserAddrsEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", user.getUserId());
            queryWrapper.eq("def_addr", 1);
            List<SysuserUserAddrsEntity> addrList = sysuserUserAddrsService.list(queryWrapper);
            if(addrList != null && addrList.size() > 0){
                for(SysuserUserAddrsEntity addr : addrList){
                    addr.setDefAddr(0);
                    sysuserUserAddrsService.updateById(addr);
                }
            }
           //设置默认地址
            SysuserUserAddrsEntity sysuserUserAddrsEntity = sysuserUserAddrsService.getById(addrId);
            sysuserUserAddrsEntity.setDefAddr(1);
            sysuserUserAddrsService.updateById(sysuserUserAddrsEntity);
            return R.ok();
        } catch (Exception e) {
            log.info("获取修改默认地址发生异常", e);
            return R.error();
        }
    }
    public boolean checkIsMobileValid(String mobile) {
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9|1]))\\d{8}$";
        return mobile.matches(regex);
    }
}
