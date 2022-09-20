package com.yanwo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.entity.UserEntity;
import com.yanwo.entity.UserGradeEntity;
import com.yanwo.service.UserGradeService;
import com.yanwo.service.UserService;
import com.yanwo.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Api(tags = "用户关系接口")
public class ApiGradeController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ApiLoginController.class);

    @Autowired
    private UserGradeService userGradeService;
    @Autowired
    private UserService userService;

    @PostMapping("/grade")
    @ApiOperation("推荐列表")
    public R grade(@RequestHeader("token") String token) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {
                return R.auth("请登录");
            }
            Integer userId = user.getUserId();

            QueryWrapper<UserGradeEntity> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("par_id_two",userId);
            //直接邀请人
            List<UserGradeEntity> list=userGradeService.list(queryWrapper);
            List userList=new ArrayList<>();
            for(UserGradeEntity grade:list){
                UserEntity userEntity = userService.getById(grade.getUserId());
                userEntity.setNickName(URLDecoder.decode(userEntity.getNickName(), "utf-8"));
                userList.add(userEntity);
            }

            Integer all=userGradeService.queryAllInvite(userId);
            Integer indirect=userGradeService.queryIndirectInvite(userId);


            Map map=new HashMap();
            map.put("referrer",userList);
            map.put("all",all);
            map.put("indirect",indirect);

            return R.okput(map);
        } catch (Exception e) {
            logger.info("获取推荐列表发生异常", e);
            return R.error("系统异常，请联系客服处理！");
        }
    }
    @PostMapping("/indirectRecom")
    @ApiOperation("间接推荐列表")
    public R indirectRecom(@RequestHeader("token") String token,Integer userId) {
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {
                return R.auth("请登录");
            }

            QueryWrapper<UserGradeEntity> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("par_id_two",userId);
            //根据二级查三级人
            List<UserGradeEntity> list=userGradeService.list(queryWrapper);
            List userList=new ArrayList<>();
            for(UserGradeEntity grade:list){
                UserEntity userEntity = userService.getById(grade.getUserId());
                userEntity.setNickName(URLDecoder.decode(userEntity.getNickName(), "utf-8"));
                userList.add(userEntity);
            }
            Map map=new HashMap();
            map.put("referrer",userList);

            return R.okput(map);
        } catch (Exception e) {
            logger.info("获取间接推荐列表发生异常", e);
            return R.error("系统异常，请联系客服处理！");
        }
    }
}
