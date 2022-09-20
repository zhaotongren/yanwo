package com.yanwo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 钱包支付记录
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-07 15:07:28
 */
@Data
@TableName("syscapital_capital_pay")
public class SyscapitalCapitalPayEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 福利关联的订单号
	 */
	@TableId(value = "tid",type = IdType.INPUT)
	private String tid;
	/**
	 * 福利关联的支付单号
	 */
	private String paymentId;
	/**
	 * 资产id
	 */
	private Integer capitalId;
	/**
	 * 钱包支付金额
	 */
	private BigDecimal payFee;
	/**
	 * 支付状态
	 */
	private Integer status;
	/**
	 * 产生时间
	 */
	private Integer createdTime;
	/**
	 * 修改时间
	 */
	private Integer modifiedTime;

}
