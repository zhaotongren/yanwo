package com.yanwo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.dao.SysFreightDao;
import com.yanwo.entity.SysFreightEntity;
import com.yanwo.service.SysFreightService;
import com.yanwo.utils.JsonUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("sysFreightService")
public class SysFreightServiceImpl extends ServiceImpl<SysFreightDao, SysFreightEntity> implements SysFreightService {

    @Override
    public Map postFee(Integer freightId, String provincecode, String cityCode){
        Map resultMap = null;
        SysFreightEntity freightEntity = getById(freightId);
        if(freightEntity != null){
            resultMap = new HashMap();
            resultMap.put("postFee",freightEntity.getBasicsPostage());
            resultMap.put("postName",freightEntity.getName());
            //解析data 特殊地区运费
            if(StringUtils.isNotBlank(freightEntity.getSpecialPostage())){
                List<Map> list = JsonUtils.jsonToList(freightEntity.getSpecialPostage(),Map.class);
                for(Map datamap : list){
                    String code = datamap.get("cityCode").toString();
                    String money = datamap.get("fee").toString();
                    if(code.contains("/")){//省/市的code
                        if(code.equals(provincecode+"/"+cityCode)){
                            resultMap.put("postFee",money);
                            break;
                        }
                    }else{//省的code
                        if(code.equals(provincecode)){
                            resultMap.put("postFee",money);
                            break;
                        }
                    }
                }
            }
        }
        return resultMap;
    }
}
