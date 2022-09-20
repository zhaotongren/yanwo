package com.yanwo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * @date 2019-09-07 09:55:15
 */
@Data
@TableName("syscapital_capital_withdraw")
public class SyscapitalCapitalWithdrawEntity implements Serializable {
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
	 * 
	 */
	private BigDecimal withdrawFee;
	/**
	 * 用户ID
	 */
	private Integer userId;
	/**
	 * 0：待审核 1：审核通过 2:拒绝
	 */
	private Integer status;
	/**
	 * 体现原因
	 */
	private String withdrawReason;
	/**
	 * 驳回原因
	 */
	private String refuseReason;
	/**
	 * 
	 */
	private Integer createdTime;
	/**
	 * 
	 */
	private Integer modifiedTime;
	@TableField(exist = false)
	private String userName;

	private BigDecimal poundageFee;

	private BigDecimal	actualFee;

	/**
	 * 银行卡号
	 */
	private String bankCard;

	private String realName;
}
