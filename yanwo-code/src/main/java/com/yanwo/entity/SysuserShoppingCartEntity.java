package com.yanwo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-06 10:41:45
 */
@Data
@TableName("sysuser_shopping_cart")
public class SysuserShoppingCartEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer cartId;
	/**
	 * 会员ident,会员信息和session生成的唯一值
	 */
	private String userIdent;
	/**
	 * 会员id
	 */
	private Integer userId;
	/**
	 * 购物车对象类型
	 */
	private String objType;
	/**
	 * 
	 */
	private String objIdent;
	/**
	 * 商品id
	 */
	private Integer itemId;
	/**
	 * 规格id
	 */
	private Integer skuId;
	/**
	 * 商品标题
	 */
	private String title;
	/**
	 * 商品默认图
	 */
	private String imageDefaultId;
	/**
	 * 数量
	 */
	private Integer quantity;
	/**
	 * 加入购物车时间
	 */
	private Integer createdTime;
	/**
	 * 最后修改时间
	 */
	private Integer modifiedTime;
	/**
	 * 规格信息
	 */
	private String specInfo;

	@TableField(exist = false)
	private BigDecimal price;

}
