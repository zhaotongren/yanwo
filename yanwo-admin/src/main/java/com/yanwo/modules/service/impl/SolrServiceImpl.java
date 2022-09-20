package com.yanwo.modules.service.impl;

import com.aliyun.opensearch.sdk.dependencies.com.google.common.collect.Maps;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.Query;
import com.yanwo.dao.SysitemItemDao;
import com.yanwo.entity.SysitemItemEntity;
import com.yanwo.entity.SysitemSkuEntity;
import com.yanwo.modules.service.SolrService;
import com.yanwo.modules.service.SysitemItemService;
import com.yanwo.modules.service.SysitemSkuService;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.OpenSearchUtil;
import com.yanwo.utils.PageUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Service("solrService")
public class SolrServiceImpl implements SolrService {
    private Logger log = LoggerFactory.getLogger(SolrService.class);
    @Autowired
    SysitemItemService sysitemItemService;
    @Autowired
    SysitemSkuService sysitemSkuService;


    @Value("${solr.server.url}")
    private String SOLR_SERVER_URL;

    @Override
    public boolean import2solr(Integer itemId) throws Exception{
        //连接solr服务器
        /**SolrServer solrServer = new HttpSolrServer(SOLR_SERVER_URL);
        //创建一个文档对象
        //主要字段
        SysitemItemEntity item = sysitemItemDao.selectById(itemId);
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", item.getItemId()+"");
        document.addField("item_title", item.getTitle());
        document.addField("item_price", item.getPrice().toString());
        if(item.getImageDefaultId() ==null ){
            return false;//不符合要求的都不发布
        }
        document.addField("item_image", item.getImageDefaultId());

        document.addField("sold_quantity",item.getSoldNum()==null?0:item.getSoldNum());//销量
        //把文档对象写入索引库
        solrServer.add(document);
        //提交
        solrServer.commit();
        solrServer.shutdown();**/

        //阿里开放搜索
        SysitemItemEntity item = sysitemItemService.getById(itemId);
        Map<String, Object> doc = Maps.newLinkedHashMap();
        doc.put("item_id", item.getItemId());
        doc.put("cat_id", item.getCatId());
        doc.put("title", item.getTitle());
        doc.put("sub_title", item.getSubTitle());
        doc.put("image_default", item.getImageDefault());
        doc.put("max_price", item.getMaxPrice());
        doc.put("min_price", item.getMinPrice());

        doc.put("max_integral", "0");
        doc.put("min_integral", "0");

        if(null!=item.getMaxIntegral() && null!=item.getMinIntegral()){
            doc.put("max_integral", item.getMaxIntegral());
            doc.put("min_integral", item.getMinIntegral());
        }

        QueryWrapper<SysitemSkuEntity> wrapper=new QueryWrapper<>();
        wrapper.eq("item_id",item.getItemId());
        List<SysitemSkuEntity> list1 = sysitemSkuService.list(wrapper);
        BigDecimal b = new BigDecimal(0);
        for(SysitemSkuEntity skuEntity:list1){
            if(b.compareTo(new BigDecimal(0))==0){
                b=skuEntity.getMktPrice();
            }
            if(skuEntity.getMktPrice().compareTo(b)==-1){
                b=skuEntity.getMktPrice();
            }
        }
        doc.put("mkt_price", b);
        doc.put("sold_num", item.getSoldNum()==null?0:item.getSoldNum());
        doc.put("list_time", DateUtils.currentUnixTime());
        doc.put("grade", item.getGrade()==null?0:item.getGrade());
        doc.put("item_type", item.getItemType());
        doc.put("total_sales",item.getSoldNum() + item.getCustomSales());

        if(OpenSearchUtil.add(doc)){
            log.info("商品"+item.getItemId()+"成功添加到搜索服务器");
        }
        else{
            log.info("商品"+item.getItemId()+"添加到搜索服务器失败");
        }

       return true;
    }
    @Override
    public boolean del2solr(Integer itemId) throws Exception{
        //删除solr服务器的数据
        /**SolrServer solrServer = new HttpSolrServer(SOLR_SERVER_URL);
        solrServer.deleteById(itemId+"");
        solrServer.shutdown();**/

        //阿里开放搜索
        Map<String, Object> doc1 = Maps.newLinkedHashMap();
        doc1.put("item_id", itemId);
        OpenSearchUtil.del(doc1);
        return true;
    }

}
