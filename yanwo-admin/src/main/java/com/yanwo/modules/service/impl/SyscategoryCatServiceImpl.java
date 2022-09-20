package com.yanwo.modules.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.dao.SyscategoryCatDao;
import com.yanwo.entity.SyscategoryCatEntity;
import com.yanwo.modules.service.SyscategoryCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("syscategoryCatServiceImpl")
public class SyscategoryCatServiceImpl extends ServiceImpl<SyscategoryCatDao, SyscategoryCatEntity> implements SyscategoryCatService {

    @Autowired
    SyscategoryCatDao syscategoryCatDao;

    @Override
    public List queryCatList() {
        return syscategoryCatDao.queryCatList();
    }

    /**
     * 删除分类
     *
     * @param id
     * @throws Exception
     */
    public int beforeDeleteCheck(Integer id) {
        //与该分类关联的商品Id的数量
        return syscategoryCatDao.beforeDeleteCheck(id);
    }

    /**
     * 获取某级分类的所有父分类（创建时用）
     *
     * @param cateId
     * @param level
     */
    public List getParenCateForCreate(Integer cateId, String level) {
        List resultList = new ArrayList();
        Map resultMap = new HashMap();
        SyscategoryCatEntity testCat = null;
        if("1".equals(level)){
            testCat = getById(cateId);
            resultMap.put("cateName",testCat.getCatName());
            resultMap.put("cateId",testCat.getCatId());
            resultList.add(resultMap);
        }
        if("2".equals(level)){
            testCat = getById(cateId);
            resultMap.put("cateName",testCat.getCatName());
            resultMap.put("cateId",testCat.getCatId());
            resultList.add(resultMap);
            //------------------可爱的分割线
            resultMap = new HashMap();
            testCat = getById(testCat.getParentId());
            resultMap.put("cateName",testCat.getCatName());
            resultMap.put("cateId",testCat.getCatId());
            //resultList.add(resultMap);
        }
        return resultList;
    }

}
