package com.yanwo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 运费模板
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-14 11:40:36
 */
@Data
@TableName("sys_freight")
public class SysFreightEntity {

	/**
	 * 运费模板id
	 */
	@TableId
	private Integer freightId;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 基础邮费价格
	 */
	private BigDecimal basicsPostage;
	/**
	 * 特殊邮费价格
	 */
	private String specialPostage;
	/**
	 * 状态
	 */
	private Integer status;

}
