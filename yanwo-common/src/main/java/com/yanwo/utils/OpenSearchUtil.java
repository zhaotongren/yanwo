package com.yanwo.utils;

import com.aliyun.opensearch.DocumentClient;
import com.aliyun.opensearch.OpenSearchClient;
import com.aliyun.opensearch.SearcherClient;
import com.aliyun.opensearch.sdk.dependencies.org.json.JSONArray;
import com.aliyun.opensearch.sdk.dependencies.org.json.JSONObject;
import com.aliyun.opensearch.sdk.generated.OpenSearch;
import com.aliyun.opensearch.sdk.generated.commons.OpenSearchResult;
import com.aliyun.opensearch.sdk.generated.document.Command;
import com.aliyun.opensearch.sdk.generated.document.DocumentConstants;
import com.aliyun.opensearch.sdk.generated.search.SearchParams;
import com.aliyun.opensearch.sdk.generated.search.general.SearchResult;
import com.yanwo.Constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 *
 * 阿里云开放搜索
 *  https://help.aliyun.com/document_detail/52360.html
 */
public class OpenSearchUtil {

    private static Logger log = LoggerFactory.getLogger(OpenSearchUtil.class);

    //替换为opensearch应用名
    private static String appName = Constants.APPNAME;
    //替换opensearch应用表名
    //目前 在opensearch建立一个表  类似solr的配置文件建表
    private static String tableName = Constants.TABLENAME;
    //替换accesskey
    private static String accesskey = "LTAI4FvunR7JFxfpUcRgSusX";
    //替换secret
    private static String secret = "ClPLluEc3CCCBaIcmJtjh7XXWtRRKO";
    //替换应用的API访问地址
    private static String host = Constants.HOST;
    /**
     *
     *
     *
     * 使用commit的方式向opensearch 推送数据
     * 另外一种push适合大量数据
     */
    public static boolean add(Map<String, Object> doc){
        //查看文件和默认编码格式
        //log.debug(String.format("file.encoding: %s", System.getProperty("file.encoding")));
        //log.debug(String.format("defaultCharset: %s", Charset.defaultCharset().name()));
        //创建并构造OpenSearch对象
        OpenSearch openSearch1 = new OpenSearch(accesskey, secret, host);
        //创建OpenSearchClient对象，并以OpenSearch对象作为构造参数
        OpenSearchClient serviceClient1 = new OpenSearchClient(openSearch1);
        //定义DocumentClient对象添加数据并提交
        DocumentClient documentClient1 = new DocumentClient(serviceClient1);
        // 把doc1加入缓存，并设为新增文档
        documentClient1.add(doc);
        log.info("添加到搜索服务器数据详情："+doc.toString());
        try {
            OpenSearchResult osr = documentClient1.commit(appName, tableName);
            if(osr.getResult().equalsIgnoreCase("true")){
                log.info("向opensarch推送数据成功");
                return true;
            }
            else{
                log.info("向opensarch推送数据错误",osr.getTraceInfo());
                return false;
            }
        }
        catch (Exception e){
            log.info("向opensarch推送数据异常",e);
            return false;
        }
    }

    /**
     * 添加多个
     *  使用push的方法
     */
    public static boolean addMultiple(List<Map<String, Object>> docs) {
        JSONArray docsJsonArr = new JSONArray();
        for (Map doc:docs) {
            JSONObject json = new JSONObject();
            json.put(DocumentConstants.DOC_KEY_CMD, Command.ADD.toString());
            json.put(DocumentConstants.DOC_KEY_FIELDS, doc);
            docsJsonArr.put(json);
        }
        String docsJson = docsJsonArr.toString();
        //创建并构造OpenSearch对象
        OpenSearch openSearch = new OpenSearch(accesskey, secret, host);
        //创建OpenSearchClient对象，并以OpenSearch对象作为构造参数
        OpenSearchClient serviceClient = new OpenSearchClient(openSearch);
        //定义DocumentClient对象添加json格式doc数据批量提交
        DocumentClient documentClient = new DocumentClient(serviceClient);
        try {
            //执行推送操作
            OpenSearchResult osr = documentClient.push(docsJson, appName, tableName);
            //判断数据是否推送成功，主要通过判断2处，第一处判断用户方推送是否成功，第二处是应用控制台中有无报错日志
            //用户方推送成功后，也有可能在应用端执行失败，此错误会直接在应用控制台错误日志中生成，比如字段内容转换失败
            if(osr.getResult().equalsIgnoreCase("true")){
                System.out.println(osr.getTraceInfo().getRequestId());
                return true;
            }else{
                System.out.println(osr.getTraceInfo());
                return false;
            }
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
    /**
     * 删除一个
     *
     * @param doc
     * @return
     */
    public static boolean del(Map<String, Object> doc){
        OpenSearch openSearch1 = new OpenSearch(accesskey, secret, host);
        //创建OpenSearchClient对象，并以OpenSearch对象作为构造参数
        OpenSearchClient serviceClient1 = new OpenSearchClient(openSearch1);
        //定义DocumentClient对象添加数据并提交
        DocumentClient documentClient1 = new DocumentClient(serviceClient1);
        documentClient1.remove(doc);
        log.info("opensarch删除数据内容："+doc.toString());
        try {
            OpenSearchResult osr = documentClient1.commit(appName, tableName);
            if(osr.getResult().equalsIgnoreCase("true")){
                log.info("opensarch删除数据成功");
                return true;
            }else{
                log.info("opensarch删除数据失败，原因："+osr.getTraceInfo());
                return false;
            }
        }
        catch (Exception e){
            log.info("向opensarch删除数据异常",e);
            return false;
        }
    }
    public static String search(SearchParams searchParams){
        try {
            //创建并构造OpenSearch对象
            OpenSearch openSearch = new OpenSearch(accesskey, secret, host);
            //创建OpenSearchClient对象，并以OpenSearch对象作为构造参数
            OpenSearchClient serviceClient = new OpenSearchClient(openSearch);
            SearcherClient searcherClient = new SearcherClient(serviceClient);
            SearchResult searchResult=searcherClient.execute(searchParams);
            JSONObject obj = new JSONObject(searchResult.getResult());
            return obj.toString();
        }
        catch (Exception e){
            log.info("向opensarch查询数据异常",e);
            return "";
        }
    }
    public static String analyze(SearchParams searchParams) {
        try {
            //创建并构造OpenSearch对象
            OpenSearch openSearch = new OpenSearch(accesskey, secret, host);
            //创建OpenSearchClient对象，并以OpenSearch对象作为构造参数
            OpenSearchClient serviceClient = new OpenSearchClient(openSearch);
            SearcherClient searcherClient = new SearcherClient(serviceClient);
            SearchResult searchResult=searcherClient.execute(searchParams);
            JSONObject obj = new JSONObject(searchResult.getResult());
            return obj.toString();
        }
        catch (Exception e){
            //log.info("向opensarch查询数据异常",e);
            return "";
        }
    }
}
