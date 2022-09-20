/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yanwo.service;

import com.yanwo.utils.WxUtils.PayOrder;
import com.yanwo.utils.WxUtils.ResEntity;

public interface WeixinPayService {
	ResEntity unifiedOrder(PayOrder product);
}
