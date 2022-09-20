package com.yanwo.modules.service.impl;

import com.yanwo.dao.UserDao;
import com.yanwo.entity.SyscategoryCatEntity;
import com.yanwo.entity.SysitemItemEntity;
import com.yanwo.entity.UserEntity;
import com.yanwo.modules.service.SyscapitalCapitalIntegralService;
import com.yanwo.utils.PageUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.Query;

import com.yanwo.dao.SyscapitalCapitalIntegralDao;
import com.yanwo.entity.SyscapitalCapitalIntegralEntity;
import org.springframework.ui.ModelMap;


@Service("syscapitalCapitalIntegralService")
public class SyscapitalCapitalIntegralServiceImpl extends ServiceImpl<SyscapitalCapitalIntegralDao, SyscapitalCapitalIntegralEntity> implements SyscapitalCapitalIntegralService {

    @Autowired
    UserDao userDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SyscapitalCapitalIntegralEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status",1);

        if(params.containsKey("searchType") && StringUtils.isNotBlank(params.get("searchType").toString())) {
            if(params.get("searchType").toString().equals("2")){
                if(params.containsKey("searchContent") && StringUtils.isNotBlank(params.get("searchContent").toString())){
                    queryWrapper.like("tid",params.get("searchContent").toString());
                }
            }
        }

        queryWrapper.orderByDesc("created_time");
        IPage<SyscapitalCapitalIntegralEntity> page = this.page(
                new Query<SyscapitalCapitalIntegralEntity>().getPage(params),
                queryWrapper
        );
        page.setRecords(convertList(page.getRecords()));
        return new PageUtils(page);
    }

    public List convertList(List<SyscapitalCapitalIntegralEntity>  integrals) {
        List<ModelMap> list = new ArrayList<ModelMap>();
        if ( integrals != null &&  integrals.size() > 0) {
            for (SyscapitalCapitalIntegralEntity  integral : integrals) {
                ModelMap map = buildModelList(integral);
                list.add(map);
            }
        }
        return list;
    }

    public ModelMap buildModelList(SyscapitalCapitalIntegralEntity  integral) {
        ModelMap model = new ModelMap();
        UserEntity userEntity = userDao.selectById(integral.getUserId());
        model.put("id",integral.getId());
        model.put("capitalId",integral.getCapitalId());
        try {
            model.put("userName", URLDecoder.decode(userEntity.getNickName(),"utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        model.put("integralFee",integral.getIntegralFee());
        model.put("integralType",integral.getIntegralType());
        model.put("tid",integral.getTid());
        model.put("oid",integral.getOid());
        model.put("status",integral.getStatus());
        model.put("createdTime",integral.getCreatedTime());
        model.put("modifiedTime",integral.getModifiedTime());

        return model;
    }

}
