package com.yanwo.modules.controller.sysuser;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.yanwo.entity.UserGradeEntity;
import com.yanwo.modules.service.UserGradeService;
import com.yanwo.utils.ParamsUtils;
import com.yanwo.validator.ValidatorUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yanwo.entity.UserEntity;
import com.yanwo.modules.service.UserService;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;


/**
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-08-31 11:00:11
 */
@RestController
@RequestMapping("tb/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserGradeService userGradeService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("tb:user:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = userService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{userId}")
    @RequiresPermissions("tb:user:info")
    public R info(@PathVariable("userId") Integer userId) {
        UserEntity user = userService.getById(userId);

        return R.ok().put("user", user);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("tb:user:save")
    public R save(@RequestBody UserEntity user) {
        userService.save(user);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("tb:user:update")
    public R update(@RequestBody UserEntity user) {
        ValidatorUtils.validateEntity(user);
        userService.updateById(user);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("tb:user:delete")
    public R delete(@RequestBody Integer[] userIds) {
        userService.removeByIds(Arrays.asList(userIds));

        return R.ok();
    }


    @RequestMapping("/cancel")
    @RequiresPermissions("tb:user:cancel")
    public R cancel(Integer userId, String reson) {
        try {
            UserEntity user = userService.getById(userId);
            if (user != null) {
                if ("2".equals(user.getMemberShip())) {
                    //删除相应的推广关系
                    userGradeService.cancelByParIdOne(userId);
                    userGradeService.cancelByParIdTwo(userId);
                    //更新状态
                    user.setMemberShip("4");
                    user.setReson(reson);
                    userService.updateById(user);
                } else {
                    return R.error("当前状态不支持取消,请刷新页面！");
                }
            } else {
                return R.error("系统错误，请联系平台！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("操作失败,请联系平台!");
        }
        return R.ok();
    }

    @RequestMapping(value = "/colonel", method = RequestMethod.POST)
    @RequiresPermissions("tb:user:colonel")
    public R colonel(Integer userId) {
        try {
            UserEntity user = userService.getById(userId);

            if (user.getColonel() == 0) {
                user.setColonel(1);
            } else {
                user.setColonel(0);
            }
            userService.updateById(user);
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("操作失败,请联系平台!");
        }
    }

    /**
     * 用户推荐关系
     */
    @RequestMapping("/relation")
    public R relation(Integer userId) {
        List resultList = new ArrayList<>();

        QueryWrapper<UserGradeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("par_id_two", userId);
        List<UserGradeEntity> list = userGradeService.list(queryWrapper);
        for (UserGradeEntity userGradeEntity : list) {
            UserEntity userEntity = userService.getById(userGradeEntity.getUserId());
            try {
                if (null != userEntity.getNickName()) {
                    userEntity.setNickName(URLDecoder.decode(userEntity.getNickName(), "utf-8"));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            resultList.add(userEntity);
        }

        return R.ok().put("users", resultList);
    }


}
