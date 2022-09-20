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
 * @date 2020-04-16 10:05:42
 */
@Data
@TableName("sysrate_traderate")
public class SysrateTraderateEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 评价ID
	 */
	@TableId
	private Long rateId;
	/**
	 * 订单号
	 */
	private String tid;
	/**
	 * 子订单号
	 */
	private String oid;
	/**
	 * 
	 */
	private Integer userId;
	/**
	 * 描述相符
	 */
	private Integer describeScore;
	/**
	 * 
	 */
	private String content;
	/**
	 * 晒单图片
	 */
	private String ratePic;
	/**
	 * 是否匿名
	 */
	private Integer anony;
	/**
	 * 
	 */
	private Integer createdTime;
	/**
	 * 
	 */
	private Integer modifiedTime;
	/**
	 * 是否有效
	 */
	private Integer disabled;

	private Integer itemId;


}
