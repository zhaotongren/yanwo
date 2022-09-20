package com.yanwo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.entity.SysindexArtEntity;
import com.yanwo.entity.UserApplyEntity;
import com.yanwo.entity.UserEntity;
import com.yanwo.service.SysindexArtService;
import com.yanwo.service.SystradeTradeService;
import com.yanwo.service.UserApplyService;
import com.yanwo.service.UserService;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.R;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.jws.WebParam;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class ApiUserController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ApiShoppingCartController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private UserApplyService userApplyService;
    @Autowired
    private SysindexArtService sysindexArtService;
    @Autowired
    private SystradeTradeService systradeTradeService;


    @RequestMapping(value = "userInfo",method = RequestMethod.POST)
    @ApiOperation("获取用户信息")
    public R userInfo(@RequestHeader String token){
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//查看地址需要先登录
                return R.auth("请登录");
            }
            UserEntity userEntity = userService.getById(user.getUserId());
            Map map=new HashMap<>();
            map.put("userId",userEntity.getUserId());
            map.put("nickName",userEntity.getNickName());
            map.put("cerHandPic",userEntity.getCerHandPic());
            map.put("mobile",userEntity.getUserId());
            map.put("sex",userEntity.getSex());
            map.put("birthday",userEntity.getBirthday());
            map.put("memberShip",userEntity.getMemberShip());

            Map tradeNum=systradeTradeService.getTradeNum(userEntity.getUserId());
            map.put("nonPayment",tradeNum.get("nonPayment").toString());//待付款
            map.put("dropShipping",tradeNum.get("dropShipping").toString());//代发货
            map.put("waitReceiving",tradeNum.get("waitReceiving").toString());//待收货
            map.put("completed",tradeNum.get("completed").toString());//待评价
            map.put("aftersales",tradeNum.get("aftersales").toString());//售后

            return R.okput(map);
        } catch (Exception e) {
            log.info("获取用户信息异常", e);
            return R.error();
        }
    }

    @RequestMapping(value = "updateUser",method = RequestMethod.POST)
    @ApiOperation("修改用户信息")
    public R updateUser(@RequestHeader String token,
                        @RequestParam(value = "realName",required = false) String realName,
                        @RequestParam(value = "cardId",required = false) String cardId,
                        @RequestParam(value = "addr",required = false)  String addr,
                        @RequestParam(value = "mobile",required = false)  String mobile){
        try {
            UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//查看地址需要先登录
                return R.auth("请登录");
            }
            //重复操作判断
            if (redisUtils.existsKey("updateUser" + user.getUserId())) {
                return R.error("操作频繁,请10秒后重新操作");
            } else {
                redisUtils.set("updateUser" + user.getUserId(), "0", 10);
            }
            UserEntity userEntity = userService.getById(user.getUserId());
            if("1".equals(userEntity.getMemberShip())){
                return R.error("正在审核中，不能重复申请！");
            }else if("4".equals(userEntity.getMemberShip())){
                return R.error("分销身份被平台取消，不能再次申请！");
            }
            //加密
            userEntity.setRealName(realName);
            userEntity.setCardId(cardId);
            userEntity.setAddr(addr);
            userEntity.setMemberShip("1");
            userService.updateById(userEntity);
            //添加申请记录
            UserApplyEntity userApply = new UserApplyEntity();
            userApply.setUserId(user.getUserId());
            userApply.setRealName(realName);
            userApply.setCardId(cardId);
            userApply.setAddr(addr);
            userApply.setMobile(mobile);
            userApply.setCreateTime(DateUtils.currentUnixTime());
            userApply.setStatus("1");
            userApplyService.save(userApply);
            return R.okput("操作成功");
        } catch (Exception e) {
            log.info("加盟分销发生异常", e);
            return R.error();
        }
    }
    @RequestMapping(value = "agreement",method = RequestMethod.POST)
    @ApiOperation("加盟协议")
    public R agreement(@RequestHeader String token){
        try {UserEntity user = getUserFromTokenApp(token);
            if (user == null) {//查看地址需要先登录
                return R.auth("请登录");
            }
            ModelMap model = new ModelMap();
            model.put("agreement","协议维护中...");
            QueryWrapper<SysindexArtEntity> queryWrapper = new QueryWrapper();
            queryWrapper.eq("type",3);
            queryWrapper.eq("enable_flag",0);
            List<SysindexArtEntity> list = sysindexArtService.list(queryWrapper);
            if(list != null && list.size() > 0){
                SysindexArtEntity art = list.get(0);
                model.put("agreement",URLDecoder.decode(art.getDescription(), "UTF-8"));
            }
            return R.okput(model);
        }catch (Exception e){
            log.info("获取加盟协议异常", e);
            return R.error();
        }
    }

}
