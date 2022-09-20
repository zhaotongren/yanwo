package com.yanwo.dao;

import com.yanwo.entity.UserVipEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户购买会员的记录
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-04 15:30:43
 */
@Mapper
public interface UserVipDao extends BaseMapper<UserVipEntity> {
	
}
