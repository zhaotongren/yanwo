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
@TableName("syscapital_capital_detail")
public class SyscapitalCapitalDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 资产明细id
	 */
	@TableId
	private Integer capitalDetailId;
	/**
	 * 资产id
	 */
	private Integer capitalId;
	/**
	 * 返利金额
	 */
	private BigDecimal capitalFee;
	/**
	 * 产生类型 1：直级返利 2：间接返利 3：超级身份返利 4：自己购买商品返利 5：消费 6：提现
	 */
	private Integer capitalType;
	/**
	 * 产生描述
	 */
	private String capitalDesc;
	/**
	 * 与该条记录关联的大订单号tid
	 */
	private String tid;
	/**
	 * 与该条记录关联的小订单号oid
	 */
	private Long oid;
	/**
	 * 创建时间
	 */
	private Integer createdTime;
	/**
	 *
	 */
	private Integer userId;


}
