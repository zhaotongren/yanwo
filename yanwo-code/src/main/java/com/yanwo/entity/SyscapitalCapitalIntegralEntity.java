package com.yanwo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 积分记录
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-24 15:49:03
 */
@Data
@TableName("syscapital_capital_integral")
public class SyscapitalCapitalIntegralEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 
	 */
	private Integer capitalId;
	/**
	 * 用户ID
	 */
	private Integer userId;
	/**
	 * 
	 */
	private BigDecimal integralFee;
	/**
	 * 1：产生 2：消耗
	 */
	private Integer integralType;
	/**
	 * 与该条记录关联的大订单号tid
	 */
	private String tid;
	/**
	 * 与该条记录关联的小订单号oid
	 */
	private Long oid;
	/**
	 * 0：无效 1：有效
	 */
	private Integer status;
	/**
	 * 
	 */
	private Integer createdTime;
	/**
	 * 
	 */
	private Integer modifiedTime;

}
