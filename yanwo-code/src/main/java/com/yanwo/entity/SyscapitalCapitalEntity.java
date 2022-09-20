package com.yanwo.entity;

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
@TableName("syscapital_capital")
public class SyscapitalCapitalEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 资产id
	 */
	@TableId
	private Integer capitalId;
	/**
	 * 钱包总额(返佣+充值)
	 */
	private BigDecimal totalCapital;
	/**
	 * 用户id
	 */
	private Integer userId;
	/**
	 * 钱包支付密码
	 */
	private String walletPwd;
	/**
	 * 积分总额
	 */
	private BigDecimal totalIntegral;
	/**
	 * 充值总额
	 */
	private BigDecimal totalRecharge;

}
