package com.yanwo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanwo.dao.SyscapitalCapitalWithdrawDao;
import com.yanwo.entity.SyscapitalCapitalDetailEntity;
import com.yanwo.entity.SyscapitalCapitalWithdrawEntity;
import com.yanwo.service.SyscapitalCapitalWithdrawService;
import com.yanwo.utils.GUtils;
import com.yanwo.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.ui.ModelMap;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service("syscapitalCapitalWithdrawService")
public class SyscapitalCapitalWithdrawServiceImpl extends ServiceImpl<SyscapitalCapitalWithdrawDao, SyscapitalCapitalWithdrawEntity> implements SyscapitalCapitalWithdrawService {
    @Override
    public PageUtils queryPage(Integer userId, int page) {
        QueryWrapper<SyscapitalCapitalWithdrawEntity> qw = new QueryWrapper<>();
        if(userId != null){
            qw.eq("user_id",userId);
        }
        qw.orderByDesc("created_time");
        Page<SyscapitalCapitalWithdrawEntity> pageW = new Page();
        pageW.setSize(10);
        pageW.setCurrent(page);
        IPage iPage =page(pageW, qw);
        iPage.setRecords(convertList(iPage.getRecords()));
        return new PageUtils(iPage);
    }
    public List convertList(List<SyscapitalCapitalWithdrawEntity>  capitalDetailList) {
        List<ModelMap> list = new ArrayList<ModelMap>();
        if ( capitalDetailList != null &&  capitalDetailList.size() > 0) {
            for (SyscapitalCapitalWithdrawEntity  detail : capitalDetailList) {
                ModelMap map = buildModelList(detail);
                list.add(map);
            }
        }
        return list;
    }
    /**
     * 构造列表模型
     * @param withdrawEntity
     * @return
     */
    public ModelMap buildModelList( SyscapitalCapitalWithdrawEntity  withdrawEntity) {
        ModelMap model = new ModelMap();
        model.put("withdrawFee",withdrawEntity.getWithdrawFee());
        model.put("withdrawReason",withdrawEntity.getWithdrawReason());
        model.put("createTime", GUtils.IntegerToDate(withdrawEntity.getCreatedTime()));
        model.put("statusName", withdrawEntity.getStatus() == 0 ? "待审核" : withdrawEntity.getStatus() == 1 ? "审核通过" : "驳回");
        return model;
    }
}
