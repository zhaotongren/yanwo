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
 * @date 2019-09-07 11:20:59
 */
@Data
@TableName("ectools_trade_paybill")
public class EctoolsTradePaybillEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 子支付单编号
	 */
	@TableId
	private Integer paybillId;
	/**
	 * 主支付单编号
	 */
	private String paymentId;
	/**
	 * 被支付订单编号
	 */
	private String tid;
	/**
	 * 该订单支付的状态
	 */
	private String status;
	/**
	 * 该订单支付的金额
	 */
	private String payment;
	/**
	 * 会员id
	 */
	private String userId;
	/**
	 * 支付单创建时间
	 */
	private Integer createdTime;
	/**
	 * 支付完成时间
	 */
	private Integer payedTime;
	/**
	 * 最后更新时间
	 */
	private Integer modifiedTime;
	/**
	 * 商家名称
	 */
	private String mrzgShopName;

}
