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
 * @date 2019-09-12 15:18:01
 */
@Data
@TableName("sysnotify_message")
public class SysnotifyMessageEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 自增主键
	 */
	@TableId
	private Long messageId;
	/**
	 * 消息标题
	 */
	private String title;
	/**
	 * 消息内容
	 */
	private String content;
	/**
	 * 消息类型（1：系统消息2：物流消息3：活动消息）
	 */
	private Integer type;
	/**
	 * 创建时间
	 */
	private Integer createdTime;

}
