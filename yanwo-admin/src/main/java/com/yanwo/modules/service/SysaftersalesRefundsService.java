package com.yanwo.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SysaftersalesRefundsEntity;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-17 14:38:50
 */
public interface SysaftersalesRefundsService extends IService<SysaftersalesRefundsEntity> {

    PageUtils queryPage(Map<String, Object> params);
    R refund(SysaftersalesRefundsEntity refundsEntity,HttpServletRequest request);
    List<Map>  find(Map params);
}

