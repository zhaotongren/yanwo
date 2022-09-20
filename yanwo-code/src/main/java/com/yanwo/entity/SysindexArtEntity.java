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
 * @date 2019-09-05 10:55:05
 */
@Data
@TableName("sysindex_art")
public class SysindexArtEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 内容标题
	 */
	private String title;
	/**
	 * 图文详情
	 */
	private String description;
	/**
	 * 类型
	 */
	private String type;
	/**
	 * 图片绝对路径
	 */
	private String imgPath;
	/**
	 * 是否启用
	 */
	private Integer enableFlag;
	/**
	 * 更新时间
	 */
	private Integer updateTime;

	@TableField(exist = false)
	private String dateStr;

}
