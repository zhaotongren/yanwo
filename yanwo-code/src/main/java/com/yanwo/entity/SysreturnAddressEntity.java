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
 * @date 2020-04-27 16:52:54
 */
@Data
@TableName("sysreturn_address")
public class SysreturnAddressEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 回寄id
	 */
	@TableId
	private Integer returnId;
	/**
	 * 回寄地址
	 */
	private String address;

}
