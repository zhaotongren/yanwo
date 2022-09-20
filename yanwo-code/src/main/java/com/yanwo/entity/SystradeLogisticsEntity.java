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
 * @date 2019-09-05 18:17:58
 */
@Data
@TableName("systrade_logistics")
public class SystradeLogisticsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long eid;
	/**
	 * 快递公司名
	 */
	private String logisticsName;
	private String logisticsCode;

}
