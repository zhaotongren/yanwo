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
 * @date 2020-04-16 16:40:22
 */
@Data
@TableName("sysaftersales_refunds")
public class SysaftersalesRefundsEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 退款申请ID
	 */
	@TableId
	private Integer refundsId;
	/**
	 * 退款申请编号
	 */
	private String refundBn;
	/**
	 * 会员id
	 */
	private Integer userId;
	/**
	 * 该退款单的主订单号
	 */
	private String tid;
	/**
	 * 该退款单的订单号
	 */
	private String oid;
	/**
	 * 0取消订单  1退货退款
	 */
	private String refundsType;
	/**
	 * 0未审核 1已完成退款 3商家审核通过 4商家审核不通过 5商家强制关单 6平台强制关单 7平台审核不通过 8等待客户回寄商品 9等待商家收货 10商家收货
	 */
	private String status;
	/**
	 *
	 */
	private String refundsReason;
	/**
	 *
	 */
	private BigDecimal payment;
	/**
	 * 商品金额
	 */
	private BigDecimal totalPrice;
	/**
	 * 退款金额
	 */
	private BigDecimal refundFee;
	/**
	 * 运费的金额
	 */
	private BigDecimal postFee;
	/**
	 *
	 */
	private Integer createdTime;
	/**
	 *
	 */
	private Integer modifiedTime;
	/**
	 *
	 */
	private Integer refundNum;
	/**
	 *
	 */
	private Integer refundFinishTime;
	/**
	 *
	 */
	private String evidencePic;
	/**
	 * 平台处理申请说明
	 */
	private String adminExplanation;
	/**
	 * 回寄物流信息
	 */
	private String sendbackData;
	/**
	 *
	 */
	private String description;
	private String returnAddress;

	private BigDecimal rechargeFee;
}
