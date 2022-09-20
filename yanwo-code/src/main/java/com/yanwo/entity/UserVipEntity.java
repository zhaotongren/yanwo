package com.yanwo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户购买会员的记录
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-04 15:30:43
 */
@Data
@TableName("tb_user_vip")
public class UserVipEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 
	 */
	private Integer userId;
	/**
	 * 会员开始日期
	 */
	private Integer startTime;
	/**
	 * 会员结束日期
	 */
	private Integer endTime;
	/**
	 * 创建时间
	 */
	private Integer createTime;
	/**
	 *1：VIP会员 ，2：知己会员
	 */
	private Integer vipType;
	/**
	 * 0：过期，1：正常
	 */
	private Integer status;

}
