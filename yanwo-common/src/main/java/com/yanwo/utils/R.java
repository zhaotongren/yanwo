/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yanwo.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */
public class R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	
	public R() {
		put("code", 0);
		put("msg", "success");
	}
	
	public static R error() {
		return error(500, "未知异常，请联系管理员");
	}

	public static R auth(String msg) {
		return error(-1, msg);
	}

	public static R error(String msg) {
		return error(500, msg);
	}
	
	public static R error(int code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static R error(int code, Map msg) {
		R r = new R();
		r.put("code", code);
		r.put("data", msg);
		return r;
	}

	public static R ok(String msg) {
		R r = new R();
		r.put("code", 0);
		r.put("msg", msg);
		return r;
	}

	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}

	public static R ok() {
		return new R();
	}

	@Override
	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}
	public static R okput(Object object) {
		R r = new R();
		r.put("data",object);
		return r;
	}

	public static R ok(String errmsg, Object data) {
		R r = new R();
		r.put("code", 0);
		r.put("msg", errmsg);
		r.put("data", data);
		return r;
	}
}
