package com.yanwo.rabbitmq;

import com.yanwo.entity.MiaoshaOrderEntity;
import com.yanwo.entity.SysitemSeckillEntity;
import com.yanwo.entity.UserEntity;
import com.yanwo.redis.RedisService;
import com.yanwo.service.MiaoshaOrderService;
import com.yanwo.service.SysitemSeckillService;
import net.bytebuddy.asm.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MQReceiver {

	private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

	@Autowired
	RedisService redisService;

	@Autowired
	SysitemSeckillService sysitemSeckillService;

	@Autowired
	MiaoshaOrderService miaoshaOrderService;

	@RabbitListener(queues=MQConfig.MIAOSHA_QUEUE)
	public void receive(String message) {
		log.info("receive message:"+message);
		MiaoshaMessage mm  = RedisService.stringToBean(message, MiaoshaMessage.class);
		UserEntity user = mm.getUser();
		long seckillId = mm.getSeckillId();//秒杀ID

		SysitemSeckillEntity goods = sysitemSeckillService.getById(seckillId);
		int stock = goods.getSeckillStock();
		if(stock <= 0) {
			return;
		}
		//判断是否已经秒杀到了
		MiaoshaOrderEntity order = miaoshaOrderService.getMiaoshaOrderByUserIdGoodsId(user.getUserId(), seckillId,mm.getRandomNumber());
		if(order != null) {
			return;
		}
		//减库存 下订单 写入秒杀订单
		miaoshaOrderService.miaosha(user, goods,mm.getAddrId(),mm.getRandomNumber());
	}

}
