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
 * @date 2020-04-16 14:44:58
 */
@Data
@TableName("sysaftersales_aftersales")
public class SysaftersalesAftersalesEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long aftersalesBn;
	/**
	 * 
	 */
	private Integer userId;
	/**
	 * 
	 */
	private String aftersalesType;
	/**
	 * 0、等待商家处理（等待商家同意用户申请：换货，退款，退货退款）1、等待回寄（商家已同意客户申请，等待客户回寄：换货，退货退款）2、等待商家确认收货（客户已发货，等待商家确认收货：退货退款）3、商家已驳回（商家驳回客户申请：退款 退货退款）4、商家已处理（这个暂时没用过）5、商家已确认收货（商家已确认收货，等待admin退款：退货退款）6、平台已驳回（平台驳回退款：退款）7、平台已处理平台已退款（平台已退款：退款，退货退款）
	 */
	private String progress;
	/**
	 * 
	 */
	private String status;
	/**
	 * 
	 */
	private Long tid;
	/**
	 * 
	 */
	private Long oid;
	/**
	 * 
	 */
	private String title;
	/**
	 * 
	 */
	private Integer num;
	/**
	 * 
	 */
	private String reason;
	/**
	 * 申请描述
	 */
	private String description;
	/**
	 * 
	 */
	private String evidencePic;
	/**
	 * 平台处理申请说明
	 */
	private String adminExplanation;
	/**
	 * 消费者提交退货物流信息
	 */
	private String sendbackData;
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
	private Integer itemId;
	/**
	 * 
	 */
	private Integer skuId;

}
