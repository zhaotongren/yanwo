package com.yanwo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-06 15:27:41
 */
@Data
@TableName("sysuser_user_addrs")
public class SysuserUserAddrsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 会员地址ID
	 */
	@TableId
	private Integer addrId;
	/**
	 * 会员ID
	 */
	private Integer userId;
	/**
	 * 会员名称
	 */
	private String name;
	/**
	 * 收货人所在地区
	 */
	private String area;
	/**
	 * 地址
	 */
	private String addr;
	/**
	 * 邮编
	 */
	private String zip;
	/**
	 * 手机
	 */
	private String mobile;
	/**
	 * 默认地址
	 */
	private Integer defAddr;

}
