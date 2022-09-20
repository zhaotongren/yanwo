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
 * @date 2020-05-21 15:04:16
 */
@Data
@TableName("sysbank_card")
public class SysbankCardEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 银行卡id
	 */
	@TableId
	private Integer cardId;
	/**
	 * 用户id
	 */
	private Integer userId;
	/**
	 * 真实姓名
	 */
	private String realName;
	/**
	 * 银行卡号
	 */
	private String bankCard;
	/**
	 * 创建时间
	 */
	private Integer createTime;

}
