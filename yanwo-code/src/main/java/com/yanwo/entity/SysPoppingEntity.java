package com.yanwo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 弹窗
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-06-08 11:06:51
 */
@Data
@TableName("sys_popping")
public class SysPoppingEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 弹窗id
	 */
	@TableId
	private Integer poppingId;
	/**
	 * 名称
	 */
	private String poppingName;
	/**
	 * 说明
	 */
	private String poppingExplain;
	/**
	 * 图片
	 */
	private String poppingImg;
	/**
	 * 跳转链接
	 */
	private String skipUrl;
	/**
	 * 每天的弹出次数
	 */
	private Integer poppingDayCount;
	/**
	 * 每人弹出的总次数
	 */
	private Integer poppingSum;
	/**
	 * 状态：0启用；1禁用
	 */
	private Integer status;
	/**
	 * 弹出间隔
	 */
	private Integer intervalMin;
	/**
	 * 过期时间
	 */
	private Integer expiresTime;
	/**
	 * 创建时间
	 */
	private Integer createTime;

}
