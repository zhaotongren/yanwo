package com.yanwo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.dao.SyscapitalCapitalDao;
import com.yanwo.dao.SyscapitalCapitalDetailDao;
import com.yanwo.dao.SysrechargeCardDao;
import com.yanwo.entity.SyscapitalCapitalDetailEntity;
import com.yanwo.entity.SyscapitalCapitalEntity;
import com.yanwo.entity.SysrechargeCardEntity;
import com.yanwo.service.SysrechargeCardService;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.R;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;


@Service("sysrechargeCardService")
@Transactional
public class SysrechargeCardServiceImpl extends ServiceImpl<SysrechargeCardDao, SysrechargeCardEntity> implements SysrechargeCardService {

    @Resource
    private SyscapitalCapitalDao capitalDao;
    @Resource
    private SyscapitalCapitalDetailDao capitalDetailDao;

    @Override
    public List<SysrechargeCardEntity> queryByCardList(String cardNo){
        QueryWrapper<SysrechargeCardEntity> qw = new QueryWrapper();
        qw.eq("card_no",cardNo);
        return this.list(qw);
    }

    @Override
    public R recharge(Integer userId,String cardNo, String password){
        List<SysrechargeCardEntity> zCards = queryByCardList(cardNo);
        if(zCards != null && zCards.size() > 0){
            SysrechargeCardEntity card = zCards.get(0);
            if(!password.equals(card.getPassword())){
                return R.error("请输入正确的密码");
            }
            if(card.getDisabled() == 1){
                return R.error("该卡已失效,请联系平台!");
            }
            card.setUserId(userId);
            card.setModifiedTime(DateUtils.currentUnixTime());
            card.setStatus(1);
            updateById(card);
            //查询用户资产信息
            QueryWrapper<SyscapitalCapitalEntity> queryWrapper = new QueryWrapper();
            queryWrapper.eq("user_id",userId);
            SyscapitalCapitalEntity capitalEntity = capitalDao.selectOne(queryWrapper);
            if(capitalEntity == null){
                capitalEntity = new SyscapitalCapitalEntity();
                capitalEntity.setUserId(userId);
                capitalEntity.setTotalRecharge(card.getRechargeMoney());
                capitalEntity.setTotalCapital(card.getRechargeMoney());
                capitalEntity.setTotalIntegral(BigDecimal.ZERO);
                capitalDao.insert(capitalEntity);
            }else {
                capitalEntity.setTotalRecharge(capitalEntity.getTotalRecharge().add(card.getRechargeMoney()));
                capitalEntity.setTotalCapital(capitalEntity.getTotalCapital().add(card.getRechargeMoney()));
                capitalDao.updateById(capitalEntity);
            }
            //增加充值流水
            SyscapitalCapitalDetailEntity detailEntity = new SyscapitalCapitalDetailEntity();
            detailEntity.setUserId(userId);
            detailEntity.setCapitalId(capitalEntity.getCapitalId());
            detailEntity.setCapitalFee(card.getRechargeMoney());
            detailEntity.setCapitalType(7);
            detailEntity.setCreatedTime(DateUtils.currentUnixTime());
            capitalDetailDao.insert(detailEntity);
            return R.ok();
        }else {
            return R.error("请输入正确的卡号");
        }
    }

}
