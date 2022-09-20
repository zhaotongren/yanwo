package com.yanwo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 优惠券
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-06-01 14:41:36
 */
@Data
@TableName("sysactivity_coupon")
public class SysactivityCouponEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 优惠券id
	 */
	@TableId
	private Integer couponId;
	/**
	 * 优惠券名称
	 */
	private String couponName;
	/**
	 * 优惠券详细说明
	 */
	private String couponDetails;
	/**
	 * 优惠券金额
	 */
	private BigDecimal money;
	/**
	 * 可使用该优惠券的金额
	 */
	private BigDecimal restrictMoney;
	/**
	 * 优惠券数量
	 */
	private Integer count;
	/**
	 * 优惠券类型：0总金额可用；1单商品可用
	 */
	private Integer couponType;
	/**
	 * 到期时间
	 */
	private Integer expireTime;
	/**
	 * 优惠券状态：0正常；1禁用
	 */
	private Integer couponStatus;
	/**
	 * 创建时间
	 */
	private Integer createTime;
	/**
	 * 已领取数量
	 */
	private Integer alreadyGet;
	/**
	 * 已使用数量
	 */
	private Integer alreadyUse;
	/**
	 * 每人限领数量
	 */
	private Integer restrictNumber;
}
