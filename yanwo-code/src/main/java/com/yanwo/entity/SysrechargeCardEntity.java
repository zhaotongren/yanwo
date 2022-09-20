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
 * @date 2020-08-31 15:25:24
 */
@Data
@TableName("sysrecharge_card")
public class SysrechargeCardEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 卡号
	 */
	private String cardNo;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 0未激活   1已激活
	 */
	private Integer status;
	/**
	 * 创建时间
	 */
	private Integer createdTime;
	/**
	 * 修改时间
	 */
	private Integer modifiedTime;
	/**
	 * 用户id
	 */
	private Integer userId;
	/**
	 * 
	 */
	private BigDecimal rechargeMoney;

	private Integer disabled;

}
