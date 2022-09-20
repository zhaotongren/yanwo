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
 * @date 2020-06-02 15:15:15
 */
@Data
@TableName("sysitem_seckill")
public class SysitemSeckillEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 
	 */
	private Integer itemId;
	/**
	 * 
	 */
	private Integer skuId;
	/**
	 * 商品名
	 */
	private String goodsTitle;
	/**
	 * 秒杀库存
	 */
	private Integer seckillStock;
	/**
	 * 秒杀价
	 */
	private BigDecimal seckillPrice;
	/**
	 * 商品主图
	 */
	private String goodsImg;
	/**
	 * 商品规格
	 */
	private String goodsSpec;
	/**
	 * 
	 */
	private BigDecimal goodsPrice;
	/**
	 * 商品库存
	 */
	private Integer goodsStock;
	/**
	 * 
	 */
	private Integer startTime;
	/**
	 * 
	 */
	private Integer endTime;
	/**
	 * 
	 */
	private Integer createTime;

	private Integer status;

}
