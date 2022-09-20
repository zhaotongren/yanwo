package com.yanwo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SysitemSeckillEntity;
import com.yanwo.entity.UserEntity;

import java.util.List;

/**
 * 
 *
 * @author wangbeibei
 * @date 2020-06-02 15:15:15
 */
public interface SysitemSeckillService extends IService<SysitemSeckillEntity> {
    public List<SysitemSeckillEntity> listGoodsVo();
    public boolean checkVerifyCode(UserEntity user, long seckillId, int verifyCode);
    public String createMiaoshaPath(UserEntity user, long seckillId);
    public boolean checkPath(UserEntity user, long seckillId, String path);
    public boolean reduceStock(SysitemSeckillEntity sysitemSeckill);
    public boolean revertStock(Long id);
}

