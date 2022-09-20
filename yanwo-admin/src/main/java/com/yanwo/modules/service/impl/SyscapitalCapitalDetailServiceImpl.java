package com.yanwo.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.Query;
import com.yanwo.dao.SyscapitalCapitalDetailDao;
import com.yanwo.dao.UserDao;
import com.yanwo.entity.SyscapitalCapitalDetailEntity;
import com.yanwo.entity.UserEntity;
import com.yanwo.entity.UserGradeEntity;
import com.yanwo.modules.service.SyscapitalCapitalDetailService;
import com.yanwo.utils.PageUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("syscapitalCapitalDetailService")
public class SyscapitalCapitalDetailServiceImpl extends ServiceImpl<SyscapitalCapitalDetailDao, SyscapitalCapitalDetailEntity> implements SyscapitalCapitalDetailService {


    @Autowired
    SyscapitalCapitalDetailDao syscapitalCapitalDetailDao;
    @Autowired
    UserDao userDao;

    @Override
    public PageUtils findDetailList(Map params) {
        QueryWrapper<SyscapitalCapitalDetailEntity> queryWrapper = new QueryWrapper<>();
        if (params.containsKey("searchType") && StringUtils.isNotBlank(params.get("searchType").toString())) {
            if ("2".equals(params.get("searchType").toString())) {
                if (params.containsKey("searchContent") && StringUtils.isNotBlank(params.get("searchContent").toString())) {
                    queryWrapper.like("tid",params.get("searchContent").toString());
                }
            }
        }
        queryWrapper.in("capital_type",1,2,3);//1：直级返利 2：间接返利 3：团长返利
        queryWrapper.orderByDesc("created_time");
        IPage iPage = page(new Query<SyscapitalCapitalDetailEntity>().getPage(params), queryWrapper);
        iPage.setRecords(convertList(iPage.getRecords()));
        return new PageUtils(iPage);
    }

    public List convertList(List<SyscapitalCapitalDetailEntity> detailList) {
        List<ModelMap> list = new ArrayList<ModelMap>();
        if (detailList != null && detailList.size() > 0) {
            for (SyscapitalCapitalDetailEntity detail : detailList) {
                ModelMap map = buildModelList(detail);
                list.add(map);
            }
        }
        return list;
    }

    public ModelMap buildModelList(SyscapitalCapitalDetailEntity detail) {
        ModelMap model = new ModelMap();
        model.put("capitalDetailId",detail.getCapitalDetailId());
        model.put("capitalId",detail.getCapitalId());
        model.put("capitalFee",detail.getCapitalFee());
        model.put("capitalType",detail.getCapitalType());
        model.put("capitalDesc",detail.getCapitalDesc());
        model.put("tid",detail.getTid());
        model.put("createdTime",detail.getCreatedTime());
        try {
            model.put("userName",URLDecoder.decode(userDao.selectById(detail.getUserId()).getNickName(),"utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return model;
    }

}
