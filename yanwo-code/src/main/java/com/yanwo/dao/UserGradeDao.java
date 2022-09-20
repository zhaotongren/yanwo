package com.yanwo.dao;

import com.yanwo.entity.UserGradeEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员之间的关系
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-04 15:30:43
 */
@Mapper
public interface UserGradeDao extends BaseMapper<UserGradeEntity> {

     Integer queryAllInvite(Integer userId);
     Integer queryIndirectInvite(Integer userId);
     int  cancelByParIdOne(Integer parIdOne);
     int  cancelByParIdTwo(Integer parIdTwo);

}
