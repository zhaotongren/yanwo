package com.yanwo.service;

/**
 * Created by Administrator on 2018/6/5.
 */
public interface SearchService {

    /**
     *
     * @param q 查询关键字
     * @param sort 排序
     * @param page 当前页
     * @param pageSize 页大小
     * * @return
     */
    public String findSearchResult(String q, String priceSection,String catId,String gradeSort,String sort,String itemType,Integer page, Integer pageSize);

}
