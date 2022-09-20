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
 * @date 2020-05-08 16:57:42
 */
@Data
@TableName("tb_user_apply")
public class UserApplyEntity implements Serializable {
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
	 * 分销身份（0未申请  1待审核  2审核通过  3审核驳回 4管理员取消）
	 */
	private String status;
	/**
	 * 姓名
	 */
	private String realName;
	/**
	 * 身份证号
	 */
	private String cardId;
	/**
	 * 
	 */
	private String addr;
	/**
	 * 驳回原因
	 */
	private String reson;
	/**
	 * 手机号
	 */
	private String mobile;
	/**
	 * 
	 */
	private Integer createTime;
	/**
	 * 
	 */
	private Integer modifiedTime;

}
