package com.yanwo.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SystradeOrderEntity;
import com.yanwo.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-09-04 16:51:45
 */
public interface SystradeOrderService extends IService<SystradeOrderEntity> {
    List<SystradeOrderEntity> getOrdersByTid(String tid);
}

