package com.yanwo.modules.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanwo.dao.SyscategoryCatDao;
import com.yanwo.dao.SysitemItemDao;
import com.yanwo.dao.SysitemSkuDao;
import com.yanwo.entity.*;
import com.yanwo.modules.service.SolrService;
import com.yanwo.modules.service.SysitemItemService;
import com.yanwo.modules.service.SysitemSkuService;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.PageUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.Query;
import org.springframework.ui.ModelMap;


@Service("sysitemItemService")
public class SysitemItemServiceImpl extends ServiceImpl<SysitemItemDao, SysitemItemEntity> implements SysitemItemService {

    @Autowired
    SysitemItemDao sysitemItemDao;
    @Autowired
    SolrService solrService;
    @Autowired
    SyscategoryCatDao syscategoryCatDao;
    @Autowired
    SysitemSkuService sysitemSkuService;

    @Override
    public PageUtils queryList(Map<String, Object> params,long pageSize, long pageCurrent){

        QueryWrapper<SysitemItemEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("approve_status",0);

        if(params.containsKey("approveStatus") && StringUtils.isNotBlank(params.get("approveStatus").toString())){
            queryWrapper.eq("approve_status",params.get("approveStatus").toString());
        }

        if(params.containsKey("title") && StringUtils.isNotBlank(params.get("title").toString())){
            queryWrapper.like("title",params.get("title").toString());
        }
        if(params.containsKey("itemType") && StringUtils.isNotBlank(params.get("itemType").toString())){
            queryWrapper.eq("item_type",params.get("itemType").toString());
        }
        if(params.containsKey("isRecommend") && StringUtils.isNotBlank(params.get("isRecommend").toString())){
            queryWrapper.eq("is_recommend",params.get("isRecommend").toString());
        }
        queryWrapper.orderByDesc("created_time");
        IPage iPage =page(new Query<SysitemItemEntity>().getPage(params), queryWrapper);
        iPage.setRecords(convertList(iPage.getRecords()));
        return new PageUtils(iPage);
    }

    public List convertList(List<SysitemItemEntity>  itemList) {
        List<ModelMap> list = new ArrayList<ModelMap>();
        if ( itemList != null &&  itemList.size() > 0) {
            for (SysitemItemEntity  item : itemList) {
                ModelMap map = buildModelList(item);
                list.add(map);
            }
        }
        return list;
    }

    public ModelMap buildModelList(SysitemItemEntity  item) {
        SyscategoryCatEntity cat = syscategoryCatDao.selectById(item.getCatId());

        ModelMap model = new ModelMap();
        model.put("itemId",item.getItemId());
        model.put("title",item.getTitle());
        model.put("catName",cat.getCatName());
        SyscategoryCatEntity syscategoryCatEntity = syscategoryCatDao.selectById(cat.getParentId());
        if(null!=syscategoryCatEntity){
            model.put("parentCatName",syscategoryCatEntity.getCatName());
        }else{
            model.put("parentCatName","");
        }
        model.put("imageDefault",item.getImageDefault());
        model.put("maxPrice",item.getMaxPrice());
        model.put("minPrice",item.getMinPrice());
        if(1==item.getItemType()){
            model.put("maxIntegral",item.getMaxIntegral());
            model.put("minIntegral",item.getMinIntegral());
        }
        model.put("maxPrice",item.getMaxPrice());
        model.put("minPrice",item.getMinPrice());
        model.put("approveStatus",item.getApproveStatus());
        model.put("soldNum",item.getSoldNum());
        model.put("createdTime",item.getCreatedTime());
        model.put("modifiedTime",item.getModifiedTime());
        model.put("itemType",item.getItemType());
        model.put("customSales",item.getCustomSales());//自定义销售量
        QueryWrapper<SysitemSkuEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("item_id",item.getItemId());
        List<SysitemSkuEntity> sysitemSkuEntities = sysitemSkuService.list(queryWrapper);
        Integer store=0;
        if(!sysitemSkuEntities.isEmpty()){
            for(SysitemSkuEntity sku:sysitemSkuEntities){
                if(null!=sku.getStore()){
                    store+=sku.getStore();
                }
            }
        }
        model.put("store",store);
        model.put("isRecommend",item.getIsRecommend());
        model.put("itemSort",item.getItemSort());
        model.put("isSeckill",item.getIsSeckill());
        return model;
    }

    @Override
    public void putAway(Integer[] ids, Integer status) {
        for (Integer id : ids) {
            SysitemItemEntity sysitemItemEntity = getById(id);
            sysitemItemEntity.setApproveStatus(status);
            sysitemItemEntity.setModifiedTime(DateUtils.currentUnixTime());
            updateById(sysitemItemEntity);

            try {
                if (status == 2) {//上架
                    solrService.import2solr(id);
                } else {//下架
                    solrService.del2solr(id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
