/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yanwo.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.dao.SysDictDao;
import com.yanwo.dao.UserDao;
import com.yanwo.entity.SysDictEntity;
import com.yanwo.entity.TokenEntity;
import com.yanwo.entity.UserEntity;
import com.yanwo.exception.RRException;
import com.yanwo.form.LoginForm;
import com.yanwo.service.SysDictService;
import com.yanwo.service.TokenService;
import com.yanwo.service.UserService;
import com.yanwo.validator.Assert;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("sysDictService")
public class SysDictServiceImpl extends ServiceImpl<SysDictDao, SysDictEntity> implements SysDictService {
	@Autowired
	private SysDictDao sysDictDao;

	@Override
	public String getValueByCode(String type,String code) {

		return sysDictDao.getValueByCode(type,code);
	}


}
