/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yanwo.modules.task;

import com.yanwo.modules.service.SystradeTradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 结算定时任务
 *
 * settlementTask为spring bean的名称
 *
 * @author Mark sunlightcs@gmail.com
 */
@Component("closeTradeTask")
public class CloseTradeTask implements ITask {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	SystradeTradeService systradeTradeService;

	@Override
	public void run(String params){
		logger.debug("++++++++++++++++++++++++++++关闭未支付订单 开始+++++++++++++++++++++++++");
		systradeTradeService.closeTrade();
		logger.debug("++++++++++++++++++++++++++++关闭未支付订单 结束+++++++++++++++++++++++++");
	}
}
