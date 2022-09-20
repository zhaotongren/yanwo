package com.yanwo.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanwo.utils.GUtils;
import com.yanwo.utils.PageUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yanwo.dao.SyscapitalCapitalIntegralDao;
import com.yanwo.entity.SyscapitalCapitalIntegralEntity;
import com.yanwo.service.SyscapitalCapitalIntegralService;
import org.springframework.ui.ModelMap;


@Service("syscapitalCapitalIntegralService")
public class SyscapitalCapitalIntegralServiceImpl extends ServiceImpl<SyscapitalCapitalIntegralDao, SyscapitalCapitalIntegralEntity> implements SyscapitalCapitalIntegralService {

    @Override
    public PageUtils queryPage(Integer userId, int page) {
        QueryWrapper<SyscapitalCapitalIntegralEntity> qw = new QueryWrapper<>();
        if(userId != null){
            qw.eq("user_id",userId);
        }
        qw.eq("status",1);
        qw.orderByDesc("created_time");
        Page<SyscapitalCapitalIntegralEntity> pageW = new Page();
        pageW.setSize(10);
        pageW.setCurrent(page);
        IPage iPage =page(pageW, qw);
        iPage.setRecords(convertList(iPage.getRecords()));
        return new PageUtils(iPage);
    }
    public List convertList(List<SyscapitalCapitalIntegralEntity>  capitalDetailList) {
        List<ModelMap> list = new ArrayList<ModelMap>();
        if ( capitalDetailList != null &&  capitalDetailList.size() > 0) {
            for (SyscapitalCapitalIntegralEntity  integral : capitalDetailList) {
                ModelMap map = buildModelList(integral);
                list.add(map);
            }
        }
        return list;
    }
    /**
     * 构造列表模型
     * @param integralEntity
     * @return
     */
    public ModelMap buildModelList( SyscapitalCapitalIntegralEntity  integralEntity) {
        ModelMap model = new ModelMap();
        model.put("integralFee",integralEntity.getIntegralType() == 2 ? "-"+integralEntity.getIntegralFee() : "+"+integralEntity.getIntegralFee());
        model.put("tid",integralEntity.getTid().toString());
        model.put("createTime", GUtils.IntegerToDate(integralEntity.getCreatedTime()));
        return model;
    }

}
