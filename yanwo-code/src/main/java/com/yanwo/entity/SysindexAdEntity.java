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
 * @date 2019-08-31 15:23:48
 */
@Data
@TableName("sysindex_ad")
public class SysindexAdEntity implements Serializable {
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
	 * 广告描述
	 */
	private String adDesc;
	/**
	 * 链接
	 */
	private String url;
	/**
	 * 广告类型
	 */
	private String type;
	/**
	 * 图片绝对路径
	 */
	private String pic;
	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * 是否启用
	 */
	private Integer enableFlag;
	/**
	 * 更新时间
	 */
	private Integer updateTime;

}
