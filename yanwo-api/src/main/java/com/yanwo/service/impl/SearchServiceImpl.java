package com.yanwo.service.impl;

import com.aliyun.opensearch.sdk.dependencies.com.google.common.collect.Lists;
import com.aliyun.opensearch.sdk.generated.search.*;
import com.yanwo.Constant.Constants;
import com.yanwo.entity.SysitemItemEntity;
import com.yanwo.service.SearchService;
import com.yanwo.service.SysitemItemService;
import com.yanwo.utils.JsonUtils;
import com.yanwo.utils.OpenSearchUtil;
import com.yanwo.utils.proj.QueryResult;
import com.yanwo.utils.proj.SearchItem;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Administrator on 2018/6/5.
 */
@Service("searchService")
public class SearchServiceImpl implements SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);
    @Autowired
    private SysitemItemService sysitemItemService;


    /**
     * app的搜索
     *
     * @param q        查询关键字
     * @param sort     排序
     * @param page     当前页
     * @param pageSize 页大小
     * @return
     */
    @Override
    public String findSearchResult(String q,
                                   String priceSection,
                                   String catId,
                                   String gradeSort,
                                   String sort,
                                   String itemType,
                                   Integer page,
                                   Integer pageSize
    ) {
        Config config = new Config(Lists.newArrayList(Constants.APPNAME));
        Integer startNum = (page - 1) * pageSize;
        config.setStart(startNum);//第多少条开始
        config.setHits(pageSize);//每页多少条
        //设置返回格式为json,目前只支持返回xml和json格式，暂不支持返回fulljson类型
        config.setSearchFormat(SearchFormat.JSON);
        // 设置搜索结果返回应用中哪些字段
        config.setFetchFields(Lists.newArrayList("item_id", "cat_id", "title",
                "sub_title", "image_default", "max_price", "min_price", "sold_num", "list_time", "grade","item_type","total_sales","max_integral","min_integral","mkt_price"));
        // 创建参数对象
        SearchParams searchParams = new SearchParams(config);

        // 设置查询过滤条件
        Sort sorter = new Sort();
        if (StringUtils.isNotBlank(gradeSort)) {
            if ("gd".equals(gradeSort)) {
                sorter.addToSortFields(new SortField("grade", Order.DECREASE)); //评分降序
            }
            if ("ga".equals(gradeSort)) {
                sorter.addToSortFields(new SortField("grade", Order.INCREASE)); //评分升序
            }
        }

        if (StringUtils.isBlank(sort)) {
            sorter.addToSortFields(new SortField("total_sales", Order.DECREASE)); //销量降序
            sorter.addToSortFields(new SortField("list_time", Order.DECREASE)); //上架时间降序
        } else {
            if ("pd".equals(sort)) {
                sorter.addToSortFields(new SortField("min_price", Order.DECREASE)); //价格从高到低
            } else if ("pa".equals(sort)) {//价格从低到高
                sorter.addToSortFields(new SortField("min_price", Order.INCREASE)); //价格从低到高
            }
            if ("ct".equals(sort)) {//上架时间
                sorter.addToSortFields(new SortField("list_time", Order.DECREASE)); //上架时间降序
            }
            if ("sn".equals(sort)) {
                sorter.addToSortFields(new SortField("total_sales", Order.DECREASE)); //销量降序
            }
        }


        StringBuffer stringBuffer =  new StringBuffer();
        //设置排序规则
//        this.setSort(sorter,sort);
        //添加Sort对象参数
        searchParams.setSort(sorter);
        int a5=0;
        if (StringUtils.isNotBlank(q)) {
            a5=1;
            stringBuffer.append("keyword:\'" + q + "\'");
        }
        if (StringUtils.isNotBlank(catId)) {
            if(a5==1){
                stringBuffer.append(" AND ");
            }
            stringBuffer.append("cat_id:\'" + catId + "\'");
        }
        if (StringUtils.isNotBlank(itemType)) {
            if(a5==1){
                stringBuffer.append(" AND ");
            }
            stringBuffer.append("item_type:\'" + itemType + "\'");
        }

        if (StringUtils.isNotBlank(priceSection)) {
            String lowPrice = priceSection.substring(0, priceSection.indexOf("-"));//最低筛选价
            String tallPrice = priceSection.substring(lowPrice.length() + 1, priceSection.length());//最高筛选价
            searchParams.setFilter("min_price>"+lowPrice + " AND min_price<="+tallPrice);
        }
        if(StringUtils.isNotBlank(stringBuffer.toString())){
            searchParams.setQuery(stringBuffer.toString());
        }
        QueryResult qr = new QueryResult();
        String s2 = OpenSearchUtil.search(searchParams);
        Map map4 = JsonUtils.jsonToMap(s2);
        Object o = map4.get("result");
        List list2 = (List) ((Map) o).get("items");
        List<SearchItem> items = new ArrayList<SearchItem>();
        for (Object o2 : list2) {
            Map map3 = (Map) o2;
            System.out.println(map3);
            String item_id = (String) map3.get("item_id");
            String title = (String) map3.get("title");
            String sub_title = (String) map3.get("sub_title");
            String image_default = (String) map3.get("image_default");
            Float max_price = Float.parseFloat((String) map3.get("max_price"));
            Float min_price = Float.parseFloat((String) map3.get("min_price"));
            Float max_integral = Float.parseFloat((String) map3.get("max_integral"));
            Float min_integral = Float.parseFloat((String) map3.get("min_integral"));
            Float mkt_price = Float.parseFloat((String) map3.get("mkt_price"));
//            Integer sold_num = Integer.valueOf((String) map3.get("sold_num"));
            Integer total_sales = Integer.valueOf((String) map3.get("total_sales"));
            String list_time = (String) map3.get("list_time");
            Long grade = Long.parseLong((String) map3.get("grade"));
            Long cat_id = Long.parseLong((String) map3.get("cat_id"));
            String item_type = (String) map3.get("item_type");
            SearchItem item = new SearchItem();
            item.setItemId(Long.valueOf(item_id));
            item.setTitle(title);
            item.setSubTitle(sub_title);
            item.setImageDefault(image_default);
            item.setMaxPrice(max_price.toString());
            item.setMinPrice(min_price.toString());
            item.setMaxIntegral(max_integral.toString());
            item.setMinIntegral(min_integral.toString());
            item.setMktPrice(mkt_price.toString());
            item.setSoldNum(total_sales);//前台展示销售量 = 实际销售量 + 自定义销售量
            item.setListTime(list_time);
            item.setGrade(grade.intValue());
            item.setItemType(item_type);
            //添加到集合中
            items.add(item);
        }
        qr.setResult(items);
        qr.setTotalNum(((Integer) ((Map) o).get("total")).longValue());//总记录数
        long totalPage = 1;
        if (qr.getTotalNum() % pageSize == 0) {
            totalPage = qr.getTotalNum() / pageSize;
        } else {
            totalPage = qr.getTotalNum() / pageSize + 1;
        }
        qr.setCurrPage(page);
        qr.setTotalPage(totalPage);

        Map result = new HashMap();
        result.put("r", qr);
        return JsonUtils.objectToJson(result);
    }

    private void setSort(Sort sorter, String sort) {
        switch (sort) {
            case "1":
                sorter.addToSortFields(new SortField("sold_quantity", Order.DECREASE)); //降序
                break;
            case "2":
                sorter.addToSortFields(new SortField("sold_quantity", Order.INCREASE)); //升序
                break;
            case "3":
                sorter.addToSortFields(new SortField("view_count", Order.DECREASE)); //降序
                break;
            case "4":
                sorter.addToSortFields(new SortField("view_count", Order.INCREASE)); //升序
                break;
            case "5":
                sorter.addToSortFields(new SortField("item_price", Order.DECREASE)); //降序
                break;
            case "6":
                sorter.addToSortFields(new SortField("item_price", Order.INCREASE)); //升序
                break;
            case "7":
                sorter.addToSortFields(new SortField("list_time", Order.DECREASE)); //降序
                break;
        }
    }

    /**
     * 将当前时间转化为10位的时间戳
     *
     * @param
     * @return
     **/
    public Integer convertTimeStamp() {
        //将13位时间戳转化为字符串类型的10位时间戳
        String timestamp = String.format("%010d", System.currentTimeMillis() / 1000);
        //将字符串类型的10位时间戳转化为int类型的10位时间戳
        Integer stamp = Integer.valueOf(timestamp);
        return stamp;
    }

}
