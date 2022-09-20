package com.yanwo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 弹窗记录
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-06-08 11:06:51
 */
@Data
@TableName("sys_popping_record")
public class SysPoppingRecordEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@TableId
	private Integer id;
	/**
	 * 用户id
	 */
	private Integer userId;
	/**
	 * 弹窗id
	 */
	private Integer poppingId;
	/**
	 * 当天弹窗次数
	 */
	private Integer dayCount;
	/**
	 * 总弹窗次数
	 */
	private Integer total;
	/**
	 * 当前日期
	 */
	private String currentData;
	/**
	 * 上次弹窗时间
	 */
	private Integer agoTime;
}
