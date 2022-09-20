package com.yanwo.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.access.AccessLimit;
import com.yanwo.common.resultbean.ResultGeekQ;
import com.yanwo.entity.*;
import com.yanwo.rabbitmq.MQSender;
import com.yanwo.rabbitmq.MiaoshaMessage;
import com.yanwo.redis.GoodsKey;
import com.yanwo.redis.RedisService;
import com.yanwo.service.MiaoshaOrderService;
import com.yanwo.service.SysitemSeckillService;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;
import com.yanwo.utils.RedisUtils;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yanwo.common.enums.ResultStatus.*;


@RestController
@RequestMapping("/miaosha")
public class MiaoshaController extends BaseController implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(MiaoshaController.class);

    @Autowired
    SysitemSeckillService sysitemSeckillService;

    @Autowired
    RedisService redisService;

    @Autowired
    MiaoshaOrderService miaoshaOrderService;

    @Autowired
    MQSender mqSender;

    @Autowired
    protected RedisUtils redisUtils;


    private HashMap<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

    @PostMapping("/list")
    @ApiOperation("秒杀列表")
    public R list() {
        try {
            List<SysitemSeckillEntity> goodsList = sysitemSeckillService.listGoodsVo();
            Map map = new HashMap<>();
            map.put("goodsList",goodsList);
            map.put("nowTime", DateUtils.currentUnixTime());
            return R.okput(map);
        } catch (Exception e) {
            logger.info("获取秒杀列表发生异常", e);
            return R.error();
        }
    }

    /**
     * QPS:1306
     * 5000 * 10
     * get　post get 幂等　从服务端获取数据　不会产生影响　　post 对服务端产生变化
     */
    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
//    @RequestMapping(value="/{path}/do_miaosha", method= RequestMethod.POST)
    @RequestMapping(value="/do_miaosha", method= RequestMethod.POST)
    public ResultGeekQ<Integer> miaosha(@RequestHeader("token")String token,
//                                        @PathVariable("path") String path,
                                        @RequestParam("seckillId") long seckillId,
                                        @RequestParam("addrId") Integer addrId) {
        ResultGeekQ<Integer> result = ResultGeekQ.build();

        UserEntity user = getUserFromTokenApp(token);
        if (user == null) {
            result.withError(SESSION_ERROR.getCode(), SESSION_ERROR.getMessage());
            return result;
        }
        //验证path
//        boolean check = sysitemSeckillService.checkPath(user, seckillId, path);
//        if (!check) {
//            result.withError(REQUEST_ILLEGAL.getCode(), REQUEST_ILLEGAL.getMessage());
//            return result;
//        }
        //是否已经秒杀到
//        MiaoshaOrderEntity order = miaoshaOrderService.getMiaoshaOrderByUserIdGoodsId(user.getUserId(), seckillId);
//        if (order != null) {
//            result.withError(REPEATE_MIAOSHA.getCode(), REPEATE_MIAOSHA.getMessage());
//            return result;
//        }
        //秒杀结束
        if(!redisUtils.existsKey("miaosha_" + seckillId)){
            result.withError(MIAO_SHA_OUT.getCode(), MIAO_SHA_OUT.getMessage());
            return result;
        }
        //内存标记，减少redis访问
        String over = redisUtils.get("miaosha_" + seckillId);
        if ("1".equals(over)) {
            logger.info("商品已经秒杀完毕 over:" + over);
            result.withError(MIAO_SHA_OVER.getCode(), MIAO_SHA_OVER.getMessage());
            return result;
        }

        String store = redisService.get(GoodsKey.getMiaoshaGoodsStock.getPrefix() + seckillId);
        if(Integer.valueOf(store) <= 0){
            logger.info("商品已经秒杀完毕 store:" + store);
            localOverMap.put(seckillId, true);
            redisUtils.set("miaosha_" + seckillId,"1");
            result.withError(MIAO_SHA_OVER.getCode(), MIAO_SHA_OVER.getMessage());
            return result;
        }
        //预见库存 - 1
        Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + seckillId);

        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setSeckillId(seckillId);
        mm.setUser(user);
        mm.setAddrId(addrId);
        mm.setRandomNumber(DateUtils.currentUnixTime());//区分同一个用户对于某个商品秒多次（只有userId和seckillId确定不了唯一）
        mqSender.sendMiaoshaMessage(mm);
        result.setData(mm.getRandomNumber());
        return result;
    }
    /**
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     */
    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    public ResultGeekQ<String> miaoshaResult(Model model,
                                           @RequestHeader("token")String token,
                                           @RequestParam("seckillId") long seckillId,
                                           @RequestParam("randomNumber") Integer randomNumber) {
        ResultGeekQ<String> result = ResultGeekQ.build();
        UserEntity user = getUserFromTokenApp(token);
        if (user == null) {
            result.withError(SESSION_ERROR.getCode(), SESSION_ERROR.getMessage());
            return result;
        }
        model.addAttribute("user", user);
        String miaoshaResult = miaoshaOrderService.getMiaoshaResult(user.getUserId(), seckillId,randomNumber);
        result.setData(miaoshaResult);
        return result;
    }

    /**
     * 系统初始化
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<SysitemSeckillEntity> seckillList = sysitemSeckillService.listGoodsVo();
        if (seckillList == null) {
            return;
        }
        for (SysitemSeckillEntity seckill : seckillList) {
            redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + seckill.getId(), seckill.getSeckillStock());
            redisUtils.set("miaosha_" + seckill.getId(),"0");
        }
    }

}
