package com.yanwo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.List;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-04 16:51:45
 */
@Data
@TableName("systrade_trade")
public class SystradeTradeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 订单号
	 */
	@TableId(value = "tid",type = IdType.INPUT)
	private String tid;
	/**
	 * 用户id
	 */
	private Integer userId;
	/**
	 * 收货地址id
	 */
	private Integer addrId;
	/**
	 * 状态
	 */
	private String status;
	/**
	 * 商品总金额
	 */
	private BigDecimal totalFee;
	/**
	 * 实付金额,订单最终总额
	 */
	private BigDecimal payment;
	/**
	 * 抵扣金额
	 */
	private BigDecimal welfareFee;
	/**
	 * 订单创建时间
	 */
	private Integer createdTime;
	/**
	 * 付款时间
	 */
	private Integer payTime;
	/**
	 * 发货时间
	 */
	private Integer consignTime;
	/**
	 * 修改时间
	 */
	private Integer modifiedTime;
	/**
	 * 订单完成时间
	 */
	private Integer endTime;
	/**
	 * 
	 */
	private String remark;
	/**
	 * 收货人姓名
	 */
	private String receiverName;
	/**
	 * 收货人详细地址
	 */
	private String receiverAddress;
	/**
	 * 收货人手机号
	 */
	private String receiverMobile;
	/**
	 * 快递单号
	 */
	private String courierNumber;
	/**
	 * 物流公司
	 */
	private String courierName;

	private String rateParam;
	/**
	 * 运费
	 */
	private BigDecimal postFee;

	/**
	 * 是否删除
	 */
	private Boolean deleted;
	/**
	 * 快递公司简称
	 */
	private String courierCode;

	/**
	 * 买家是否已评价
	 */
	private Boolean buyerRate;
	/**
	 * 结算时间
	 */
	private Integer settlementTime;

	/**
	 * 0普通单子   1积分兑换   2优惠券   3秒杀  4充值余额支付   5优惠券+充值余额
	 */
	private Integer type;

	private Long seckillId;

	/**
	 * 充值余额支付
	 */
	private BigDecimal rechargeFee;

	@TableField(exist = false)
	private String numValue;
	@TableField(exist = false)
	private String userName;
	@TableField(exist = false)
	private String createdTimeValue;
	@TableField(exist = false)
	private String payTimeValue;
	@TableField(exist = false)
	private String consignTimeValue;
	@TableField(exist = false)
	private String finishTimeValue;
	@TableField(exist = false)
	private List orderList;

	@TableField(exist = false)
	private String userPic;
	@TableField(exist = false)
	private String realName;
	@TableField(exist = false)
	private String cardId;
	@TableField(exist = false)
	private Double orderTotalPrice;
	@TableField(exist = false)
	private String paymentId;
	@TableField(exist = false)
	private String settlementTimeValue;

}
