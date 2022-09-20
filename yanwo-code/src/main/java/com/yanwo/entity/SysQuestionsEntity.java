package com.yanwo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 问答表
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-18 10:32:32
 */
@Data
@TableName("sys_questions")
public class SysQuestionsEntity implements Serializable {

	/**
	 * 问题Id
	 */
	@TableId
	private Integer questionsId;
	/**
	 * 问题
	 */
	private String issue;
	/**
	 * 回答
	 */
	private String answer;
	/**
	 * 状态（0:删除,1:正常）
	 */
	private Integer status;
	/**
	 * 创建时间
	 */
	private Integer createTime;

}
