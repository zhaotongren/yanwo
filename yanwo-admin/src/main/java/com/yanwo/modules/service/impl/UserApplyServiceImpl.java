package com.yanwo.modules.service.impl;

import com.yanwo.dao.UserGradeDao;
import com.yanwo.entity.UserEntity;
import com.yanwo.modules.service.UserApplyService;
import com.yanwo.modules.service.UserService;
import com.yanwo.utils.PageUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.Query;

import com.yanwo.dao.UserApplyDao;
import com.yanwo.entity.UserApplyEntity;
import org.springframework.ui.ModelMap;


@Service("userApplyService")
public class UserApplyServiceImpl extends ServiceImpl<UserApplyDao, UserApplyEntity> implements UserApplyService {

    @Autowired
    private UserService userService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<UserApplyEntity> queryWrapper = new QueryWrapper<>();
        if(params.containsKey("searchType") && StringUtils.isNotBlank(params.get("searchType").toString())){
            if(params.containsKey("searchContent") && StringUtils.isNotBlank(params.get("searchContent").toString())){
                //手机号
                if("1".equals(params.get("searchType").toString())){
                    queryWrapper.like("mobile",params.get("searchContent").toString());
                }
                //姓名
                else if("2".equals(params.get("searchType").toString())){
                    queryWrapper.like("real_name",params.get("searchContent").toString());
                }
                //身份证号
                else if("3".equals(params.get("searchType").toString())){
                    queryWrapper.like("card_id",params.get("searchContent").toString());
                }
            }
        }
        queryWrapper.orderByDesc("create_time");
        IPage iPage =page(new Query<UserApplyEntity>().getPage(params), queryWrapper);
        iPage.setRecords(convertList(iPage.getRecords()));
        return new PageUtils(iPage);
    }
    public List convertList(List<UserApplyEntity>  userList) {
        List<ModelMap> list = new ArrayList<ModelMap>();
        if ( userList != null &&  userList.size() > 0) {
            for (UserApplyEntity  userApply : userList) {
                ModelMap map = buildModelList(userApply);
                list.add(map);
            }
        }
        return list;
    }
    public ModelMap buildModelList(UserApplyEntity  userApply) {
        ModelMap model = new ModelMap();
        model.put("id",userApply.getId());
        model.put("userId",userApply.getUserId());
        UserEntity user = userService.getById(userApply.getUserId());
        try {
            model.put("nickName", URLDecoder.decode(user.getNickName(),"utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        model.put("cerHandPic",user.getCerHandPic());
        model.put("mobile",userApply.getMobile());
        model.put("realName",userApply.getRealName());
        model.put("cardId",userApply.getCardId());
        model.put("addr",userApply.getAddr());
        model.put("reson",userApply.getReson());
        model.put("status",userApply.getStatus());
        model.put("createTime",user.getCreateTime());

        return model;
    }

}
