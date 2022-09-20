package com.yanwo.modules.controller.category;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Constants;
import com.yanwo.entity.SyscategoryCatEntity;
import com.yanwo.modules.service.SyscategoryCatService;
import com.yanwo.utils.ImageUtils;
import com.yanwo.utils.OssUtil;
import com.yanwo.utils.R;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by cccc on 2018/5/28.
 */
@RestController
@RequestMapping("category")
public class SyscategoryCatController  {

    @Autowired
    SyscategoryCatService syscategoryCatService;


    /**
     * 查询类目分类列表
     */
    @RequestMapping(value = "/list")
    @RequiresPermissions("category:syscategory:list")
    public List queryCatList() {
        return syscategoryCatService.queryCatList();
    }

    /**
     * 删除分类
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public R deleteCategory(@PathVariable Integer id) {
        try {
            //此处需要注意，一定不能随意操作...
            //需要判断有没有子分类
            //如果有就不能删除
            QueryWrapper<SyscategoryCatEntity> queryWrapper = new QueryWrapper();
            queryWrapper.eq("parent_id", id);
            List<SyscategoryCatEntity> list = syscategoryCatService.list(queryWrapper);
            if (list != null && list.size() > 0) {
                return R.error(Constants.ERROR, "该类目有子类目在使用，不能删除");
            }
            int i = syscategoryCatService.beforeDeleteCheck(id);
            if(i!=0){
                return R.error(Constants.ERROR,"该分类下有商品，不可删除");
            }
            syscategoryCatService.removeById(id);
        } catch (Exception e) {
            return R.error(Constants.ERROR, "删除分类失败");
        }
        return R.ok();
    }

    /**
     * 添加分类
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public R create(SyscategoryCatEntity syscategoryCatEntity) {
        try {
            if(StringUtils.isBlank(syscategoryCatEntity.getCatLogo())){
                return  R.error(Constants.ERROR,"请选择分类图片");
            }
            syscategoryCatService.save(syscategoryCatEntity);
            return R.ok("分类添加成功");
        } catch (Exception e) {
            return R.error(Constants.ERROR, "创建类目发生异常");
        }
    }

    /**
     * 修改分类
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public R update(SyscategoryCatEntity syscategoryCatEntity) {
        try {
            if(StringUtils.isBlank(syscategoryCatEntity.getCatLogo())){
                return  R.error(Constants.ERROR,"请选择分类图片");
            }
            syscategoryCatService.updateById(syscategoryCatEntity);
            return R.ok("分类修改成功");
        } catch (Exception e) {
            return R.error(Constants.ERROR, "创建类目发生异常");
        }
    }

    /**
     * 获取要添加的分类的父所有父分类(添加时调用)
     *
     * @param cateId
     * @param level
     * @return
     */
    @RequestMapping(value = "getParentCateForCreate", method = RequestMethod.GET)
    public R getParenCateForCreate(Integer cateId, String level) {
        try {
            Map map=new HashMap<>();
            map.put("cate",syscategoryCatService.getParenCateForCreate(cateId, level));
            return R.ok(map);
        } catch (Exception e) {
            return R.error(Constants.ERROR, "获取父分类发生异常");
        }
    }

    /**
     * 获取要添加的分类的父所有父分类(编辑时调用)
     *
     * @param cateId
     * @param level
     * @return
     */
    @RequestMapping(value = "getParentCateForUpdate", method = RequestMethod.GET)
    public R getParentCateForUpdate(Integer cateId, String level) {
        try {
            Map map=new HashMap<>();
            map.put("cate",syscategoryCatService.getParenCateForCreate(cateId, level));
            return R.ok(map);
        } catch (Exception e) {
            return R.error(Constants.ERROR, "获取父分类发生异常");
        }
    }

    //根据cate获取类目信息
    @RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
    public R update(@PathVariable Integer id) {
        Map map=new HashMap<>();
        map.put("cate",syscategoryCatService.getById(id));
        return R.ok(map);
    }

    /**
     * 获取所有一级分类
     * @return
     */
    @RequestMapping(value = "/firstCat")
    public R firstCat() {
        Map map=new HashMap<>();

        QueryWrapper<SyscategoryCatEntity> queryWrapper = new QueryWrapper();
        queryWrapper.eq("parent_id", 0);
        List<SyscategoryCatEntity> list = syscategoryCatService.list(queryWrapper);
        map.put("cate",list);
        return R.ok(map);
    }

    /**
     * 根据一级分类获取子分类
     * @return
     */
    @RequestMapping(value = "/getSecondCat",method = RequestMethod.GET)
    public R getSecondCat(Integer catId) {
        Map map=new HashMap<>();
        QueryWrapper<SyscategoryCatEntity> queryWrapper = new QueryWrapper();
        queryWrapper.eq("parent_id", catId);
        List<SyscategoryCatEntity> list = syscategoryCatService.list(queryWrapper);
        map.put("cate",list);
        return R.ok(map);
    }

}
