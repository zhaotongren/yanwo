package com.yanwo.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanwo.dao.SyscapitalCapitalDetailDao;
import com.yanwo.entity.SyscapitalCapitalDetailEntity;
import com.yanwo.entity.SyscapitalCapitalEntity;
import com.yanwo.entity.SysitemItemEntity;
import com.yanwo.service.SyscapitalCapitalDetailService;
import com.yanwo.utils.GUtils;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.Query;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.ui.ModelMap;


@Service("syscapitalCapitalDetailService")
public class SyscapitalCapitalDetailServiceImpl extends ServiceImpl<SyscapitalCapitalDetailDao, SyscapitalCapitalDetailEntity> implements SyscapitalCapitalDetailService {

    @Override
    public PageUtils queryPage(Integer userId,int page, Integer type) {
        QueryWrapper<SyscapitalCapitalDetailEntity> qw = new QueryWrapper<>();
        if(userId != null){
            qw.eq("user_id",userId);
        }
        if(type == 1){
            //返佣流水
            qw.notIn("capital_type",6,7,8,9);
        }else if(type == 2){
            //充值流水
            qw.in("capital_type",7,8,9);
        }

        qw.orderByDesc("created_time");
        Page<SyscapitalCapitalDetailEntity> pageW = new Page();
        pageW.setSize(10);
        pageW.setCurrent(page);
        IPage iPage =page(pageW, qw);
        iPage.setRecords(convertList(iPage.getRecords()));
        return new PageUtils(iPage);
    }
    public List convertList(List<SyscapitalCapitalDetailEntity>  capitalDetailList) {
        List<ModelMap> list = new ArrayList<ModelMap>();
        if ( capitalDetailList != null &&  capitalDetailList.size() > 0) {
            for (SyscapitalCapitalDetailEntity  detail : capitalDetailList) {
                ModelMap map = buildModelList(detail);
                list.add(map);
            }
        }
        return list;
    }
    /**
     * 构造列表模型
     * @param capitaldetail
     * @return
     */
    public ModelMap buildModelList( SyscapitalCapitalDetailEntity  capitaldetail) {
        ModelMap model = new ModelMap();
        model.put("capitalFee",capitaldetail.getCapitalFee());
        model.put("capitalType",getTypeValue(capitaldetail.getCapitalType()));
        model.put("createTime", GUtils.IntegerToDate(capitaldetail.getCreatedTime()));
        return model;
    }
    private String getTypeValue(Integer type){
        switch (type){
            case 1:
                return "直级返利";
            case 2:
                return "间接返利";
            case 3:
                return "团长返利";
            case 4:
                return "自己购买商品返利";
            case 5:
                return "消费";
            case 6:
                return "提现";
            case 7:
                return "充值";
            case 8:
                return "消费";
            case 9:
                return "退款";
            default:
                return "";
        }
    }

}
