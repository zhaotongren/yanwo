package com.yanwo.modules.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.Query;
import com.yanwo.dao.SysitemSeckillDao;
import com.yanwo.entity.SysitemSeckillEntity;
import com.yanwo.entity.SysitemSkuEntity;
import com.yanwo.modules.service.SysitemSeckillService;
import com.yanwo.modules.service.SysitemSkuService;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.PageUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service("sysitemSeckillService")
public class SysitemSeckillServiceImpl extends ServiceImpl<SysitemSeckillDao, SysitemSeckillEntity> implements SysitemSeckillService {
    @Autowired
    private SysitemSkuService sysitemSkuService;

    @Override
    public PageUtils queryList(Map<String, Object> params, long pageSize, long pageCurrent){

        QueryWrapper<SysitemSeckillEntity> queryWrapper = new QueryWrapper<>();

        if(params.containsKey("status") && StringUtils.isNotBlank(params.get("status").toString())){
            queryWrapper.eq("status",params.get("status").toString());
        }
        if(params.containsKey("title") && StringUtils.isNotBlank(params.get("title").toString())){
            queryWrapper.like("title",params.get("title").toString());
        }
        queryWrapper.orderByDesc("create_time");
        IPage iPage =page(new Query<SysitemSeckillEntity>().getPage(params), queryWrapper);
        iPage.setRecords(convertList(iPage.getRecords()));
        return new PageUtils(iPage);
    }

    public List convertList(List<SysitemSeckillEntity>  itemList) {
        List<ModelMap> list = new ArrayList<ModelMap>();
        if ( itemList != null &&  itemList.size() > 0) {
            for (SysitemSeckillEntity seckill : itemList) {
                ModelMap map = buildModelList(seckill);
                list.add(map);
            }
        }
        return list;
    }

    public ModelMap buildModelList(SysitemSeckillEntity  seckill) {
        ModelMap model = new ModelMap();
        model.put("id",seckill.getId());
        model.put("itemId",seckill.getItemId());
        model.put("goodsTitle",seckill.getGoodsTitle());
        model.put("goodsSpec",seckill.getGoodsSpec());
        model.put("skuId",seckill.getSkuId());
        model.put("seckillPrice",seckill.getSeckillPrice());
        model.put("seckillStock",seckill.getSeckillStock());
        model.put("goodsPrice",seckill.getGoodsPrice());
        model.put("goodsImg",seckill.getGoodsImg());
        model.put("startTime",seckill.getStartTime());
        model.put("endTime",seckill.getEndTime());
        model.put("createTime",seckill.getCreateTime());
        model.put("status",seckill.getStatus());
//        SysitemSkuEntity skuEntity = sysitemSkuService.getById(seckill.getSkuId());
//        model.put("goodsStock",skuEntity.getStore());
        return model;
    }


}
