package com.yanwo.modules.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.Constant.Constants;
import com.yanwo.entity.SysopenIdEntity;
import com.yanwo.entity.UserEntity;

import com.yanwo.modules.service.SysopenIdService;
import com.yanwo.modules.service.UserService;
import com.yanwo.modules.utils.HttpUtils;
import com.yanwo.modules.utils.WXPublicUtils;
import com.yanwo.utils.RedisUtils;
import com.yanwo.utils.WxUtils.WXPayUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

@RestController
public class SendDemoContoller {

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private UserService userService;
    @Autowired
    private SysopenIdService sysopenIdService;


    @RequestMapping(value="/receive/msg",method= RequestMethod.GET)
    public String  verifyWXToken(HttpServletRequest request, HttpServletResponse resp) {
        try {
            System.out.println("-------------验证token开始-----------");
            String msgSignature = request.getParameter("signature");
            String msgTimestamp = request.getParameter("timestamp");
            String msgNonce = request.getParameter("nonce");
            String echostr = request.getParameter("echostr");
            if (WXPublicUtils.verifyUrl(msgSignature, msgTimestamp, msgNonce)) {
                return echostr;
            }
            System.out.println("-------------验证token结束-----------");
            return null;
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("-------------验证token异常-----------");
            return null;
        }
    }


    @RequestMapping(value="/receive/msg",method= RequestMethod.POST)
    @ResponseBody
    public String  responseEvent(HttpServletRequest req, HttpServletResponse resp) {
        try {
            System.out.println("-------------微信发送关注/取消公众号开始-----------");
            getAccessToken();
            InputStream inputStream = req.getInputStream();
            StringBuffer sb = new StringBuffer();
            String s;
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while ((s = in.readLine()) != null) {
                sb.append(s);
            }
            in.close();
            inputStream.close();
//            sb.append("<xml><ToUserName><![CDATA[gh_8989fb507824]]></ToUserName><FromUserName><![CDATA[oTzpewksz9G1Ds3t7VBmR6wUmTp8]]></FromUserName><CreateTime>1591603417</CreateTime><MsgType><![CDATA[event]]></MsgType><Event><![CDATA[subscribe]]></Event><EventKey><![CDATA[]]></EventKey></xml>");
            Map<String, String> map= WXPayUtil.xmlToMap(sb.toString());
            System.out.println("微信接收到的消息为:"+map.toString());
            String fromUserName = map.get("FromUserName");//消息来源用户标识
            String msgType = map.get("MsgType");//消息类型(event或者text)
            System.out.println("openId:"+fromUserName);
            System.out.println("消息类型为:"+msgType);
            String eventType = map.get("Event");//事件类型

            if("event".equals(msgType)){//如果为事件类型
                if("subscribe".equals(eventType)){//处理订阅事件
                    String accessToken = getAccessToken();
                    String data = HttpUtils.sendGet("https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + accessToken + "&openid=" + fromUserName + "&lang=zh_CN");
                    JSONObject demoJson =JSONObject.fromObject(data);
                    String unionId = demoJson.getString("unionid");
                    QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("unionid",unionId);
                    UserEntity userEntity = userService.getOne(queryWrapper);
                    if(null!=userEntity){
                        userEntity.setUnionid(unionId);
                        userEntity.setPublicOpenId(fromUserName);
                        userService.updateById(userEntity);

                        SysopenIdEntity sysopenIdEntity = sysopenIdService.getById(unionId);
                        if(sysopenIdEntity==null){
                            sysopenIdEntity.setUnionid(unionId);
                            sysopenIdEntity.setOpenId(fromUserName);
                            sysopenIdService.save(sysopenIdEntity);
                        }
                    }
                }else if("unsubscribe".equals(eventType)){//处理取消订阅事件
                    QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("public_open_id",fromUserName);
                    UserEntity userEntity = userService.getOne(queryWrapper);
                    userEntity.setPublicOpenId("");
                    userService.updateById(userEntity);
                }
            }
            System.out.println("-------------微信发送关注/取消公众号结束-----------");
            return null;
        } catch (Exception e) {
            System.out.println("异常信息"+e);
            System.out.println("-------------微信发送关注/取消公众号异常-----------");
            return null;

        }
    }

    public String getAccessToken(){
        String accessToken = redisUtils.get(Constants.ACCESSTOKEN);
        if(StringUtils.isBlank(accessToken)){
            String message = HttpUtils.sendGet("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
                    + "wx7a855d5b2991ca2e" + "&secret=" + "f092b15b5df2cc45f5c545f4fd35c655");
            System.out.println(message);
            JSONObject demoJson =JSONObject.fromObject(message);
            accessToken = demoJson.getString("access_token");
            String expiresIn = demoJson.getString("expires_in");
            redisUtils.set(Constants.ACCESSTOKEN,accessToken,Integer.valueOf(expiresIn));
        }else{

        }
        System.out.println(accessToken);
        return accessToken;
    }

}
