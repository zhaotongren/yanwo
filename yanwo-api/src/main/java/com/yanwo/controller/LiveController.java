package com.yanwo.controller;

import com.alibaba.fastjson.JSONObject;
import com.yanwo.utils.CommonHttpUtils;
import com.yanwo.utils.JsonUtils;
import com.yanwo.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 直播
 *
 * @author 王庆彬
 */
@RestController
@RequestMapping("live")
public class LiveController extends BaseController {
    public static String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx53e0ce07db66b556&secret=c1e9b2c79cbc0a7e69f2fe363440d20b";
    String liveUrl = "https://api.weixin.qq.com/wxa/business/getliveinfo?access_token=";

    /**
     * 获取直播房间列表
     *
     * @return
     */
    @RequestMapping("/liveList")
    public R liveList() {
        String accessToken = (String) JsonUtils.jsonToMap(CommonHttpUtils.httpClienOfGet(TOKEN_URL, null)).get("access_token");
        Map roomMap = new HashMap(2);
        roomMap.put("start", 0);
        roomMap.put("limit", 100);
        String roomList = CommonHttpUtils.jsonOfPost(liveUrl + accessToken, JsonUtils.objectToJson(roomMap));
        System.out.println(roomList);
        if (StringUtils.isBlank(roomList) || !JsonUtils.jsonToMap(roomList).get("errcode").equals(0)) {
            return R.error("暂无直播信息");
        }
        return R.okput(onAir(roomList, 101));
    }

    private JSONObject onAir(String roomList, Integer statusNum) {
        Map map = JsonUtils.jsonToMap(roomList);
        List<Map> list = (List) map.get("room_info");
        Iterator<Map> iterator = list.iterator();
        while (iterator.hasNext()) {
            Map next = iterator.next();
            if (!next.get("live_status").equals(statusNum)) {
                iterator.remove();
            }
        }
        map.put("room_info", list);
        JSONObject jsonObject = JSONObject.parseObject(JsonUtils.objectToJson(map));
        //System.out.println(jsonObject.toJSONString());
        return jsonObject;
    }

    /**
     * 获取回放列表
     */
    @RequestMapping("/playbackList")
    public R playbackList(Integer pageNum) {
        pageNum-=1;
        String accessToken = (String) JsonUtils.jsonToMap(CommonHttpUtils.httpClienOfGet(TOKEN_URL, null)).get("access_token");
        Map roomMap = new HashMap(2);
        roomMap.put("start", pageNum*10);
        roomMap.put("limit", 10);
        String roomList = CommonHttpUtils.jsonOfPost(liveUrl + accessToken, JsonUtils.objectToJson(roomMap));
        System.out.println("直播间列表返回结果："+roomList);
        if (StringUtils.isBlank(roomList) || !JsonUtils.jsonToMap(roomList).get("errcode").equals(0)) {
            return R.error("暂无直播信息");
        }
        JSONObject jsonObject = onAir(roomList, 103);
        List<Map> roomInfos = (List) jsonObject.get("room_info");
        for (Map roomInfo : roomInfos) {
            Integer roomId = (Integer) roomInfo.get("roomid");
            Map map = new HashMap(4);
            map.put("action", "get_replay");
            map.put("room_id", roomId);
            map.put("start", 0);
            map.put("limit", 100);
            String s = CommonHttpUtils.jsonOfPost(liveUrl + accessToken, JsonUtils.objectToJson(map));
            if (StringUtils.isBlank(s) || !JsonUtils.jsonToMap(s).get("errcode").equals(0)) {
                continue;
            }
            Map jsonToMap = JsonUtils.jsonToMap(s);
            List<Map> live_replay = (List<Map>) jsonToMap.get("live_replay");
            for (Map replayMap : live_replay) {
                replayMap.put("coverImg", roomInfo.get("cover_img"));
                replayMap.put("roomName", roomInfo.get("name"));
                replayMap.put("roomId",roomId);
            }
            roomInfo.put("live_replay",live_replay);
        }
        Integer total = (Integer) jsonObject.get("total");
        int y=total%10==0?0:1;
        int pageTotal=total/10+y;
        jsonObject.put("pageTotal",pageTotal);
        jsonObject.put("currentPage",pageNum+1);
        jsonObject.put("room_info",roomInfos);
        return R.okput(jsonObject);
    }
}
