package com.yanwo.modules.service.impl;

import com.yanwo.Constant.Constants;
import com.yanwo.common.utils.GUtils;
import com.yanwo.dao.*;
import com.yanwo.entity.*;
import com.yanwo.modules.service.UserService;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;
import com.yanwo.utils.RedisUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {

    @Autowired
    private UserGradeDao userGradeDao;
    @Autowired
    private UserVipDao userVipDao;
    @Autowired
    protected RedisUtils redisUtils;
    @Autowired
    SystradeTradeDao systradeTradeDao;
    @Autowired
    SystradeOrderDao systradeOrderDao;
    @Autowired
    SysuserUserAddrsDao sysuserUserAddrsDao;
    @Autowired
    SysitemItemDao sysitemItemDao;
    @Autowired
    private UserDao userDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();

        if (params.containsKey("searchType") && StringUtils.isNotBlank(params.get("searchType").toString())) {
            if ("1".equals(params.get("searchType").toString())) {
                if (params.containsKey("searchContent") && StringUtils.isNotBlank(params.get("searchContent").toString())) {
                    try {
                        queryWrapper.like("nick_name", URLEncoder.encode(params.get("searchContent").toString(), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (params.containsKey("searchContent") && StringUtils.isNotBlank(params.get("searchContent").toString())) {
                    queryWrapper.like("mobile", params.get("searchContent").toString());
                }
            }
        }
        if (params.containsKey("type")) {
            queryWrapper.eq("member_ship", 2);
        }
        if(params.containsKey("colonel") && StringUtils.isNotBlank(params.get("colonel").toString())){
            queryWrapper.eq("colonel", params.get("colonel").toString());
        }

        if(params.containsKey("memberShip") && StringUtils.isNotBlank(params.get("memberShip").toString())){
            queryWrapper.eq("member_ship", params.get("memberShip").toString());
        }

        queryWrapper.orderByDesc("create_time");
        IPage iPage = page(new Query<UserEntity>().getPage(params), queryWrapper);
        if (params.containsKey("type")) {
            iPage.setRecords(convertList(iPage.getRecords(), 2));
        } else {
            iPage.setRecords(convertList(iPage.getRecords(), 1));
        }

        return new PageUtils(iPage);
    }

    public List convertList(List<UserEntity> userList, Integer type) {
        List<ModelMap> list = new ArrayList<ModelMap>();
        if (userList != null && userList.size() > 0) {
            for (UserEntity user : userList) {
                if (type == 1) {
                    ModelMap map = buildModelList(user);
                    list.add(map);
                } else {
                    if ("2".equals(user.getMemberShip())) {
                        ModelMap map = buildRelationList(user);
                        list.add(map);
                    }
                }
            }
        }
        return list;
    }

    public ModelMap buildRelationList(UserEntity user) {
        ModelMap model = new ModelMap();
        try {

                model.put("userId", user.getUserId());
                model.put("nickName", URLDecoder.decode(user.getNickName(), "utf-8"));
                model.put("cerHandPic", user.getCerHandPic());
                model.put("mobile", user.getMobile());
                model.put("createTime", user.getCreateTime());
                model.put("sex", user.getSex());
                model.put("referrerName", "");
                UserGradeEntity gradeEntity = userGradeDao.selectById(user.getUserId());
                if (null != gradeEntity && null != gradeEntity.getParIdTwo()) {
                    UserEntity userEntity = userDao.selectById(gradeEntity.getParIdTwo());
                    if (null != userEntity) {
                        model.put("referrerName", URLDecoder.decode(userEntity.getNickName(), "utf-8"));
                    }
                }
                model.put("colonel", user.getColonel());

                model.put("direct", "0");//直接推荐人数
                QueryWrapper<UserGradeEntity> directWrapper = new QueryWrapper<>();
                directWrapper.eq("par_id_two",user.getUserId());
                List<UserGradeEntity> directs = userGradeDao.selectList(directWrapper);
                if(!directs.isEmpty()){
                    model.put("direct", directs.size());//直接推荐人数
                }

                model.put("indirect", "0");//间接推荐人数
                QueryWrapper<UserGradeEntity> indirectWrapper = new QueryWrapper<>();
                indirectWrapper.eq("par_id_one",user.getUserId());
                List<UserGradeEntity> indirects = userGradeDao.selectList(indirectWrapper);
                if(!indirects.isEmpty()){
                    model.put("indirect", indirects.size());//间接推荐人数
                }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return model;
    }


    public ModelMap buildModelList(UserEntity user) {
        ModelMap model = new ModelMap();
        model.put("userId", user.getUserId());
        try {
            model.put("nickName", URLDecoder.decode(user.getNickName(), "utf-8"));

            model.put("cerHandPic", user.getCerHandPic());
            model.put("mobile", user.getMobile());
            model.put("createTime", user.getCreateTime());
            model.put("sex", user.getSex());
            model.put("referrerName", "");
            UserGradeEntity gradeEntity = userGradeDao.selectById(user.getUserId());
            if (null != gradeEntity && null != gradeEntity.getParIdTwo()) {
                UserEntity userEntity = userDao.selectById(gradeEntity.getParIdTwo());
                if (null != userEntity) {
                    model.put("referrerName", URLDecoder.decode(userEntity.getNickName(), "utf-8"));
                }
            }
            model.put("memberShip", user.getMemberShip());
            model.put("reson", user.getReson());
            model.put("colonel", user.getColonel());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return model;
    }

}
