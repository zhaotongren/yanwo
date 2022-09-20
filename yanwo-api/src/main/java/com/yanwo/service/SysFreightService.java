package com.yanwo.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SysFreightEntity;

import java.math.BigDecimal;
import java.util.Map;


public interface SysFreightService extends IService<SysFreightEntity> {

    public Map postFee(Integer freightId, String provincecode, String cityCode);

}

