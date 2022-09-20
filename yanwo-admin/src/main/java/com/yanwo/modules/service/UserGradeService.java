package com.yanwo.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.UserGradeEntity;
import com.yanwo.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 会员之间的关系
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-04 15:30:43
 */
public interface UserGradeService extends IService<UserGradeEntity> {

    PageUtils queryPage(Map<String, Object> params);
    void cancelByParIdOne(Integer parIdOne);
    void cancelByParIdTwo(Integer parIdTwo);
}

