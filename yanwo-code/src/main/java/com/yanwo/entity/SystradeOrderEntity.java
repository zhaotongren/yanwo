package com.yanwo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-04 16:51:45
 */
@Data
@TableName("systrade_order")
public class SystradeOrderEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 子订单编号
	 */
//	@TableId(value = "oid",type = IdType.INPUT)
	@TableId
	private Long oid;
	/**
	 * 订单编号
	 */
	private String tid;
	/**
	 * 商品id
	 */
	private Integer itemId;
	/**
	 * 商品标题
	 */
	private String title;
	/**
	 * 商品价格
	 */
	private BigDecimal price;
	/**
	 * 购买数量
	 */
	private Integer num;
	/**
	 * 商品图片绝对路径
	 */
	private String picPath;
	/**
	 * 买家id
	 */
	private Integer userId;
	/**
	 * 应付金额
	 */
	private BigDecimal totalFee;
	/**
	 * 实付金额
	 */
	private BigDecimal payment;
	/**
	 * 抵扣金额
	 */
	private BigDecimal welfareFee;
	/**
	 * 成本价
	 */
	private BigDecimal costPrice;

	private Integer skuId;
	/**
	 * 规格信息
	 */
	private String specInfo;
	/**
	 * 状态
	 */
	private String status;
	/**
	 * 售后状态
	 */
	private String aftersalesStatus;

	private Integer isSettlement;

	private Long seckillId;

	/**
	 * 充值余额支付
	 */
	private BigDecimal rechargeFee;

}
