package com.yanwo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yanwo.enumpackage.UserGradeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-08-31 11:00:11
 */
@Data
@TableName("tb_user")
public class UserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer userId;
	/**
	 * 昵称
	 */
	private String nickName;
	/**
	 * 头像地址
	 */
	private String cerHandPic;
	/**
	 * 手机号
	 */
	private String mobile;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 微信openID
	 */
	private String wxOpenId;
	/**
	 * 分销身份（0未申请  1待审核  2审核通过  3审核驳回 4管理员取消）
	 */
	private String memberShip;
	/**
	 * 注册时间
	 */
	private Integer createTime;
	/**
	 * 性别（1:男   2:女 3：未知）
	 */
	private String sex;

	private String realName;//真实姓名

	private String cardId;//身份证号
	private Integer birthday;//生日
	private String addr;//地址
	private String reson;//驳回原因
	private Integer colonel;
	private String unionid;
	private String publicOpenId;

	@TableField(exist = false)
	private String parName;
	@TableField(exist = false)
	private Integer promoteNum;
	@TableField(exist = false)
	private String gradeName;
	@TableField(exist = false)
	private String createTimeValue;




}
