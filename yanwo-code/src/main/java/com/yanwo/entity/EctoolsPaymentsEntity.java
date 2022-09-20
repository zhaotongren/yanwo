package com.yanwo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-07 11:20:59
 */
@Data
@TableName("ectools_payments")
public class EctoolsPaymentsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 支付单号
	 */
	@TableId(type = IdType.INPUT)
	private String paymentId;
	/**
	 * 需要支付的金额
	 */
	private BigDecimal money;
	/**
	 * 支付货币金额
	 */
	private BigDecimal curMoney;
	/**
	 * 支付状态
	 */
	private String status;
	/**
	 * 会员
	 */
	private Integer userId;
	/**
	 * 会员用户名
	 */
	private String userName;
	/**
	 * 支付类型
	 */
	private String payType;
	/**
	 * 支付方式名称
	 */
	private String payAppId;
	/**
	 * 支付方式名
	 */
	private String payName;
	/**
	 * 支付完成时间
	 */
	private Integer payedTime;
	/**
	 * 货币
	 */
	private String currency;
	/**
	 * 支付IP
	 */
	private String ip;
	/**
	 * 支付单创建时间
	 */
	private Integer createdTime;
	/**
	 * 最后更新时间
	 */
	private Integer modifiedTime;

}
