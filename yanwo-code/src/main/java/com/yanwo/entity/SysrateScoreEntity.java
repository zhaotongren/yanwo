package com.yanwo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("sysrate_score")
public class SysrateScoreEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 订单ID
	 */
	@TableId(value = "tid",type = IdType.INPUT)
	private Long tid;
	/**
	 * 用户ID
	 */
	private Integer userId;
	/**
	 * 服务态度
	 */
	private Integer attitudeScore;
	/**
	 * 物流服务
	 */
	private Integer logisticsServiceScore;
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

}
