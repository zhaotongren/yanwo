package com.yanwo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员之间的关系
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-04 15:30:43
 */
@Data
@TableName("tb_user_grade")
public class UserGradeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(value = "user_id",type = IdType.INPUT)
	private Integer userId;
	/**
	 * 间接推荐人的userid
	 */
	private Integer parIdOne;
	/**
	 * 直接推荐人的userid
	 */
	private Integer parIdTwo;
	/**
	 * 创建时间
	 */
	private Integer createTime;
	/**
	 * 间接推荐人的userid
	 */
	private Integer firstUserId;
	/**
	 * 直接推荐人的userid
	 */
	private Integer twoUserId;

}
