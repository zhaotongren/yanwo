package com.yanwo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-13 10:08:07
 */
@Data
@TableName("sysitem_sku")
public class SysitemSkuEntity {

	/**
	 * 规格id
	 */
	@TableId
	private Integer skuId;
	/**
	 * 商品id
	 */
	private Integer itemId;
	/**
	 * 规格名称
	 */
	private String title;
	/**
	 * 规格图
	 */
	private String img;
	/**
	 * 销售价
	 */
	private BigDecimal price;
	/**
	 * 成本价
	 */
	private BigDecimal costPrice;
	/**
	 * 市场价
	 */
	private BigDecimal mktPrice;
	/**
	 * 库存
	 */
	private Integer store;
	private BigDecimal integral;
	/**
	 * 商品条形码
	 */
	private String barcode;
}
