package com.yanwo.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yanwo.common.utils.Query;
import com.yanwo.dao.UserDao;
import com.yanwo.entity.SyscapitalCapitalWithdrawEntity;
import com.yanwo.entity.UserEntity;
import com.yanwo.modules.service.SysrechargeCardService;
import com.yanwo.utils.PageUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yanwo.dao.SysrechargeCardDao;
import com.yanwo.entity.SysrechargeCardEntity;
import org.springframework.ui.ModelMap;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service("sysrechargeCardService")
public class SysrechargeCardServiceImpl extends ServiceImpl<SysrechargeCardDao, SysrechargeCardEntity> implements SysrechargeCardService {

    @Resource
    private UserDao userDao;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SysrechargeCardEntity> qw = new QueryWrapper();
        if(params.get("status") != null && StringUtils.isNotBlank(params.get("status").toString())){
            qw.eq("status",Integer.valueOf(params.get("status").toString()));
        }
        if (params.get("startCard") == null || StringUtils.isBlank(params.get("startCard").toString())) {
            if (params.get("endCard") != null && StringUtils.isNotBlank(params.get("endCard").toString())) {
                qw.like("card_no",params.get("endCard").toString());
            }
        } else {
            if (params.get("endCard") == null || StringUtils.isBlank(params.get("endCard").toString())) {
                qw.like("card_no",params.get("startCard").toString());
            } else {
                qw.between("card_no",params.get("startCard").toString(),params.get("endCard").toString());
            }
        }
        if((params.get("nickname") != null && StringUtils.isNotBlank(params.get("nickname").toString())) || (params.get("mobile") != null && StringUtils.isNotBlank(params.get("mobile").toString()))){
            QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
            if(params.get("nickname") != null && StringUtils.isNotBlank(params.get("nickname").toString())){
                try {
                    queryWrapper.like("nick_name", URLEncoder.encode(params.get("nickname").toString(), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            if(params.get("mobile") != null && StringUtils.isNotBlank(params.get("mobile").toString())){
                queryWrapper.like("mobile", params.get("mobile").toString());
            }
            List<UserEntity> userList = userDao.selectList(queryWrapper);
            List<Integer> userIds = new ArrayList();
            if(userList != null && userList.size() > 0){
                for(UserEntity user : userList){
                    userIds.add(user.getUserId());
                }
                qw.in("user_id",userIds);
            }else{
                qw.eq("user_id",0);
            }

        }
        qw.orderByDesc("created_time");
        IPage<SysrechargeCardEntity> page = this.page(
                new Query<SysrechargeCardEntity>().getPage(params),
                qw
        );
        page.setRecords(convertList(page.getRecords()));

        return new PageUtils(page);
    }
    public List convertList(List<SysrechargeCardEntity>  sysrechargeCardEntities) {
        List<ModelMap> list = new ArrayList<ModelMap>();
        if ( sysrechargeCardEntities != null &&  sysrechargeCardEntities.size() > 0) {
            for (SysrechargeCardEntity  sysrechargeCardEntity : sysrechargeCardEntities) {
                ModelMap map = buildModelList(sysrechargeCardEntity);
                list.add(map);
            }
        }
        return list;
    }
    public ModelMap buildModelList(SysrechargeCardEntity  sysrechargeCardEntity) {
        ModelMap model = new ModelMap();

        model.put("id",sysrechargeCardEntity.getId());
        if(sysrechargeCardEntity.getUserId() != null){
            UserEntity userEntity = userDao.selectById(sysrechargeCardEntity.getUserId());
            try {
                model.put("userName", URLDecoder.decode(userEntity.getNickName(),"utf-8"));
                model.put("phone",userEntity.getMobile());
                model.put("userId",userEntity.getUserId());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        model.put("cardNo",sysrechargeCardEntity.getCardNo());
        model.put("status",sysrechargeCardEntity.getStatus());
        model.put("rechargeMoney",sysrechargeCardEntity.getRechargeMoney());
        model.put("createdTime",sysrechargeCardEntity.getCreatedTime());
        model.put("modifiedTime",sysrechargeCardEntity.getModifiedTime());
        model.put("disabled",sysrechargeCardEntity.getDisabled());
        return model;
    }

    @Override
    public List<SysrechargeCardEntity> queryByCardList(String cardNo){
        QueryWrapper<SysrechargeCardEntity> qw = new QueryWrapper();
        qw.eq("card_no",cardNo);
        return this.list(qw);
    }

}
