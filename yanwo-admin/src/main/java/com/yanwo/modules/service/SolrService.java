package com.yanwo.modules.service;



/**
 *
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2019-08-31 14:14:58
 */
public interface SolrService {
    boolean import2solr(Integer itemId)throws Exception;
    boolean del2solr(Integer itemId)throws Exception;
}
