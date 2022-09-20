package com.yanwo.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SyscategoryCatEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SyscategoryCatService  extends IService<SyscategoryCatEntity> {


    public List queryCatList();

    public int beforeDeleteCheck(Integer id);

    /**
     * 获取某级分类的所有父分类（创建时用）
     *
     * @param cateId
     * @param level
     */
    public List getParenCateForCreate(Integer cateId, String level);
}
