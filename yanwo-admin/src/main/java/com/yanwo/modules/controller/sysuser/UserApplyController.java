package com.yanwo.modules.controller.sysuser;

import java.util.Arrays;
import java.util.Map;

import com.yanwo.entity.UserEntity;
import com.yanwo.modules.service.UserApplyService;
import com.yanwo.modules.service.UserService;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;
import com.yanwo.validator.ValidatorUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yanwo.entity.UserApplyEntity;



/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-05-08 16:57:42
 */
@RestController
@RequestMapping("yanwo/userapply")
public class UserApplyController {
    @Autowired
    private UserApplyService userApplyService;
    @Autowired
    private UserService userService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("yanwo:userapply:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userApplyService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("yanwo:userapply:info")
    public R info(@PathVariable("id") Integer id){
        UserApplyEntity userApply = userApplyService.getById(id);

        return R.ok().put("userApply", userApply);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("yanwo:userapply:save")
    public R save(@RequestBody UserApplyEntity userApply){
        userApplyService.save(userApply);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("yanwo:userapply:update")
    public R update(@RequestBody UserApplyEntity userApply){
        ValidatorUtils.validateEntity(userApply);
        userApplyService.updateById(userApply);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("yanwo:userapply:delete")
    public R delete(@RequestBody Integer[] ids){
        userApplyService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }
    /**
     * 审核
     */
    @RequestMapping("/audit")
    @RequiresPermissions("yanwo:userapply:audit")
    public R audit(Integer id,String reson,String type){
       if("2".equals(type) && StringUtils.isBlank(reson)){
            return R.error("请填写驳回原因");
       }
        UserApplyEntity userApply = userApplyService.getById(id);
       if(userApply != null){
           UserEntity user = userService.getById(userApply.getUserId());
           if("1".equals(type)){
               //审核通过
               userApply.setStatus("2");
               userApplyService.updateById(userApply);
               user.setMemberShip("2");
               userService.updateById(user);
           }else{
               //审核驳回
               userApply.setStatus("3");
               userApply.setReson(reson);
               userApplyService.updateById(userApply);
               user.setMemberShip("3");
               user.setReson(reson);
               userService.updateById(user);
           }
       }else{
           return R.error("数据异常，请联系平台");
       }
        return R.ok();
    }

}
