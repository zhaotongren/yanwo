package com.yanwo.modules.service.impl;

import com.yanwo.dao.SyscapitalCapitalDao;
import com.yanwo.dao.SyscapitalCapitalDetailDao;
import com.yanwo.dao.SyscapitalCapitalWithdrawDao;
import com.yanwo.dao.UserDao;
import com.yanwo.entity.SyscapitalCapitalDetailEntity;
import com.yanwo.entity.SyscapitalCapitalEntity;
import com.yanwo.entity.SyscapitalCapitalWithdrawEntity;
import com.yanwo.entity.UserEntity;
import com.yanwo.modules.service.SyscapitalCapitalWithdrawService;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.Query;
import org.springframework.ui.ModelMap;


@Service("syscapitalCapitalWithdrawService")
public class SyscapitalCapitalWithdrawServiceImpl extends ServiceImpl<SyscapitalCapitalWithdrawDao, SyscapitalCapitalWithdrawEntity> implements SyscapitalCapitalWithdrawService {

    @Autowired
    UserDao userDao;
    @Autowired
    SyscapitalCapitalDetailDao syscapitalCapitalDetailDao;
    @Autowired
    SyscapitalCapitalDao syscapitalCapitalDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SyscapitalCapitalWithdrawEntity> qw = new QueryWrapper();
        if(params.get("status") != null && StringUtils.isNotBlank(params.get("status").toString())){
            qw.eq("status",Integer.valueOf(params.get("status").toString()));
        }
        qw.orderByDesc("created_time");
        IPage<SyscapitalCapitalWithdrawEntity> page = this.page(
                new Query<SyscapitalCapitalWithdrawEntity>().getPage(params),
                qw
        );
        page.setRecords(convertList(page.getRecords()));

        return new PageUtils(page);
    }

    public List convertList(List<SyscapitalCapitalWithdrawEntity>  withdrawEntities) {
        List<ModelMap> list = new ArrayList<ModelMap>();
        if ( withdrawEntities != null &&  withdrawEntities.size() > 0) {
            for (SyscapitalCapitalWithdrawEntity  withdrawEntity : withdrawEntities) {
                ModelMap map = buildModelList(withdrawEntity);
                list.add(map);
            }
        }
        return list;
    }

    public ModelMap buildModelList(SyscapitalCapitalWithdrawEntity  withdrawEntity) {
        ModelMap model = new ModelMap();

        model.put("id",withdrawEntity.getId());
        UserEntity userEntity = userDao.selectById(withdrawEntity.getUserId());
        try {

                model.put("userName", URLDecoder.decode(userEntity.getNickName(),"utf-8"));


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        model.put("phone",userEntity.getMobile());
        model.put("bankCard",withdrawEntity.getBankCard());
        model.put("realName",withdrawEntity.getRealName());
        model.put("withdrawFee",withdrawEntity.getWithdrawFee());
        model.put("poundageFee",withdrawEntity.getPoundageFee());
        model.put("actualFee",withdrawEntity.getActualFee());
        model.put("status",withdrawEntity.getStatus());
        model.put("withdrawReason",withdrawEntity.getWithdrawReason());
        model.put("refuseReason",withdrawEntity.getRefuseReason());
        model.put("createdTime",withdrawEntity.getCreatedTime());
        model.put("modifiedTime",withdrawEntity.getModifiedTime());

        return model;
    }

    @Override
    public R audit(Map map){
        Integer id = Integer.valueOf(map.get("id").toString());
        Integer status = Integer.valueOf(map.get("status").toString());
        String reason = (String) map.get("reason");
        SyscapitalCapitalWithdrawEntity withdrawEntity = this.getById(id);

        if(status == 1){//审核通过
            //插入提现记录
            SyscapitalCapitalDetailEntity detailEntity = new SyscapitalCapitalDetailEntity();
            detailEntity.setCapitalId(withdrawEntity.getCapitalId());
            detailEntity.setCapitalFee(BigDecimal.ZERO.subtract(withdrawEntity.getWithdrawFee()));
            detailEntity.setCapitalType(6);
            detailEntity.setCapitalDesc("提现");
            detailEntity.setCreatedTime(DateUtils.currentUnixTime());
            detailEntity.setUserId(withdrawEntity.getUserId());
            syscapitalCapitalDetailDao.insert(detailEntity);

            SyscapitalCapitalEntity syscapitalCapitalEntity = syscapitalCapitalDao.selectById(detailEntity.getCapitalId());
            //BigDecimal fee = withdrawEntity.getPoundageFee().add(withdrawEntity.getActualFee());//此次提现金额
            syscapitalCapitalEntity.setTotalCapital(syscapitalCapitalEntity.getTotalCapital().subtract(withdrawEntity.getWithdrawFee()));
            syscapitalCapitalDao.updateById(syscapitalCapitalEntity);
        }
        //更新提现状态
        withdrawEntity.setStatus(status);
        withdrawEntity.setRefuseReason(reason);
        this.updateById(withdrawEntity);
        return R.ok();
    }

}
