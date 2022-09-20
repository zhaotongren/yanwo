package com.yanwo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户优惠券
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-06-01 18:08:32
 */
@Data
@TableName("sysuser_coupon")
public class SysuserCouponEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 领取id
	 */
	@TableId
	private Integer getId;
	/**
	 * 用户id
	 */
	private Integer userId;
	/**
	 * 优惠券id
	 */
	private Integer couponId;
	/**
	 * 状态：0未使用，1已使用，2已过期
	 */
	private Integer getStatus;
	/**
	 * 领取时间
	 */
	private Integer getTime;
	/**
	 * 使用时间
	 */
	private Integer useTime;

}
