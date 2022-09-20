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
 * @date 2020-06-09 14:13:49
 */
@Data
@TableName("sysopen_id")
public class SysopenIdEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * unionid(小程序和公众号唯一标识)
	 */
	@TableId
	private String unionid;
	/**
	 * 公众号openId
	 */
	private String openId;

}
