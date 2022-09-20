package com.yanwo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

import java.util.List;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-03 11:00:33
 */
@Data
@TableName("sysitem_item")
public class SysitemItemEntity  implements Serializable {

	/**
	 * item_id
	 */
	@TableId
	private Integer itemId;
	/**
	 * 分类id
	 */
	private Integer catId;
	private Integer freightId;
	/**
	 * 商品标题
	 */
	private String title;
	private String subTitle;
	/**
	 * 商品默认图
	 */
	private String imageDefault;
	/**
	 * 商品图片
	 */
	private String listImage;
	/**
	 * 单位
	 */
	private String unit;
	/**
	 * 销售价
	 */
	private BigDecimal maxPrice;
	/**
	 * 成本价
	 */
	private BigDecimal minPrice;
	/**
	 * 状态（0:删除,1:下架,2:上架）
	 */
	private Integer approveStatus;
	/**
	 * 商品介绍 图文详情
	 */
	private String description;
	/**
	 * 已售数量
	 */
	private Integer soldNum;
	/**
	 * 创建时间
	 */
	private Integer createdTime;
	/**
	 * 最后更新时间
	 */
	private Integer modifiedTime;
	/**
	 * 评分
	 */
	private Integer grade;
	private Integer itemType;

	private BigDecimal maxIntegral;
	private BigDecimal minIntegral;

	private Integer itemSort;
	private Integer isRecommend;
	/**
	 * 自定义销量
	 */
	private Integer customSales;
	/**
	 * 总销量=实际+自定义
	 */
	private Integer totalSales;

	@TableField(exist = false)
	private String price;

	@TableField(exist = false)
	private List<SysitemSkuEntity> sysitemSkuEntities;

	private Integer isSeckill;

	@TableField(exist = false)
	private List<SysitemSeckillEntity> sysitemSeckills;



}
