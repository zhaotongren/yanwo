package com.yanwo.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanwo.dao.SysrateTraderateDao;
import com.yanwo.dao.UserDao;
import com.yanwo.entity.SysrateTraderateEntity;
import com.yanwo.entity.UserEntity;
import com.yanwo.service.SysrateTraderateService;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.ui.ModelMap;


@Service("sysrateTraderateService")
public class SysrateTraderateServiceImpl extends ServiceImpl<SysrateTraderateDao, SysrateTraderateEntity> implements SysrateTraderateService {

    @Autowired
    SysrateTraderateDao sysrateTraderateDao;
    @Autowired
    UserDao userDao;

    @Override
    public PageUtils queryPage(QueryWrapper<SysrateTraderateEntity> queryWrapper,Integer currPage) throws UnsupportedEncodingException {

        Page<SysrateTraderateEntity> page = new Page();
        page.setSize(10);
        page.setCurrent(currPage);
        IPage iPage =page(page, queryWrapper);
        iPage.setRecords(convertList(iPage.getRecords()));
        return new PageUtils(iPage);
    }

    public List convertList(List<SysrateTraderateEntity>  tradelist) throws UnsupportedEncodingException {
        List<ModelMap> list = new ArrayList<ModelMap>();
        if ( tradelist != null &&  tradelist.size() > 0) {
            for (SysrateTraderateEntity  trade : tradelist) {
                ModelMap map = buildModelList(trade);
                list.add(map);
            }
        }
        return list;
    }

    public ModelMap buildModelList(SysrateTraderateEntity  rate) throws UnsupportedEncodingException {
        ModelMap model = new ModelMap();
        UserEntity user = userDao.selectById(rate.getUserId());
        model.put("nickName", URLDecoder.decode(user.getNickName(), "utf-8"));
        model.put("headImg",user.getCerHandPic());
        model.put("createTime",rate.getCreatedTime());
        model.put("score",rate.getDescribeScore());
        model.put("content",URLDecoder.decode(rate.getContent(), "utf-8"));
        model.put("rateImg",rate.getRatePic());

        return model;
    }

    @Override
    public List<SysrateTraderateEntity> selectRate(Integer itemId) {
        return sysrateTraderateDao.selectRate(itemId);
    }

    @Override
    public Integer countRate(Integer itemId) {
        return sysrateTraderateDao.countRate(itemId);
    }

}
