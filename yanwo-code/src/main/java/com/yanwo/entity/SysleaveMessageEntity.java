package com.yanwo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * @date 2019-09-05 14:57:16
 */
@Data
@TableName("sysleave_message")
public class SysleaveMessageEntity implements Serializable {
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
	 * 留言内容
	 */
	private String leaveMessage;
	/**
	 * 创建时间
	 */
	private Integer createTime;
	/**
	 * 上传图片 多张以逗号分隔
	 */
	private String imgList;

	@TableField(exist = false)
	private String userName;
	@TableField(exist = false)
	private String dateStr;

}
