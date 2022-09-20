package com.yanwo.service.impl;


import com.yanwo.entity.UserEntity;
import com.yanwo.redis.MiaoshaKey;
import com.yanwo.redis.RedisService;
import com.yanwo.utils.UUIDUtil;
import com.yanwo.utils.WxUtils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.dao.SysitemSeckillDao;
import com.yanwo.entity.SysitemSeckillEntity;
import com.yanwo.service.SysitemSeckillService;

import java.util.List;


@Service("sysitemSeckillService")
public class SysitemSeckillServiceImpl extends ServiceImpl<SysitemSeckillDao, SysitemSeckillEntity> implements SysitemSeckillService {

    @Autowired
    SysitemSeckillDao sysitemSeckillDao;

    @Autowired
    RedisService redisService;


    @Override
    public List<SysitemSeckillEntity> listGoodsVo(){
        return sysitemSeckillDao.listGoodsVo();
    }

    @Override
    public boolean checkVerifyCode(UserEntity user, long seckillId, int verifyCode){
        if(user == null || seckillId <=0) {
            return false;
        }
        Integer codeOld = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getUserId()+","+seckillId, Integer.class);
        if(codeOld == null || codeOld - verifyCode != 0 ) {
            return false;
        }
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getUserId()+","+seckillId);
        return true;
    }

    @Override
    public String createMiaoshaPath(UserEntity user, long seckillId){
        if(user == null || seckillId <=0) {
            return null;
        }
        String str = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(MiaoshaKey.getMiaoshaPath, ""+user.getUserId() + "_"+ seckillId, str);
        return str;
    }

    @Override
    public boolean checkPath(UserEntity user, long seckillId, String path){
        if(user == null || path == null) {
            return false;
        }
        String pathOld = redisService.get(MiaoshaKey.getMiaoshaPath, ""+user.getUserId() + "_"+ seckillId, String.class);
        return path.equals(pathOld);
    }

    @Override
    public boolean reduceStock(SysitemSeckillEntity sysitemSeckill){
        int ret = sysitemSeckillDao.reduceStock(sysitemSeckill.getId());
        return ret > 0;
    }
    @Override
    public boolean revertStock(Long id){
        int ret = sysitemSeckillDao.revertStock(id);
        return ret > 0;
    }

}
