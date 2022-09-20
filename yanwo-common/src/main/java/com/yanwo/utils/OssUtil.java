package com.yanwo.utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.yanwo.Constant.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2018/5/29 0029.
 */
public class OssUtil {

    private static String endpoint = Constants.ENDPOINT;//图片服务器

    private static  String accessKeyId = "LTAI4FvunR7JFxfpUcRgSusX";//OSS服务器的key

    private static String accessKeySecret = "ClPLluEc3CCCBaIcmJtjh7XXWtRRKO";//OSS服务器的秘钥

    private static String bucketName = "yanwo-image";//存储空间


    /**
     * Description: 上传文件至OSS
     * @param filePath 上传内容到指定的路径
     * @param objectName 保存为指定的文件名称
     * @param input 输入流
     * @return 成功返回true，否则返回false
     */
    public static boolean uploadFile(String filePath,String objectName, InputStream input) throws IOException{
        boolean result = false;
        // 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 上传内容到指定的存储空间（bucketName）并保存为指定的文件名称（objectName）
            ossClient.putObject(bucketName, filePath+"/"+objectName, input);
            // 关闭OSSClient。
            ossClient.shutdown();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (null != ossClient) {
                ossClient.shutdown();
            }
        }
        return result;
    }

    /**
     * Description: 从OSS服务器下载文件
     * @param filePath 文件储存路径
     * @param objectName 保存为指定的文件名称
     * @param localPath 下载后保存到本地的路径
     * @return
     */
    public static boolean downloadFile( String filePath,String objectName, String localPath) {
        boolean result = false;
        // 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 调用ossClient.getObject返回一个OSSObject实例，该实例包含文件内容及文件元信息。
            OSSObject ossObject = ossClient.getObject(bucketName, filePath+"/"+objectName);
            // 调用ossObject.getObjectContent获取文件输入流，可读取此输入流获取其内容。
            InputStream content = ossObject.getObjectContent();
            if (content != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                }
                // 数据读取完成后，获取的流必须关闭，否则会造成连接泄漏，导致请求无连接可用，程序无法正常工作。
                content.close();
            }
            // 关闭OSSClient。
            ossClient.shutdown();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != ossClient) {
                ossClient.shutdown();
            }
        }
        return result;
    }

    /**
     * Description: 从OSS服务器删除文件
     * @param filePath 文件路径
     * @param objectName 保存为指定的文件名称
     * @return
     */
    public static boolean deleteFile(String filePath,String objectName) {
        boolean result = false;
        // 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 删除文件。
            ossClient.deleteObject(bucketName, filePath+"/"+objectName);
            // 关闭OSSClient。
            ossClient.shutdown();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != ossClient) {
                ossClient.shutdown();
            }
        }
        return result;
    }

    /**
     * Description: 判断文件是否存在
     * @param filePath 文件路径
     * @return
     */
    public static boolean existFile(String filePath)  {
        boolean result = false;
        // 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            OSSObject ossObject = ossClient.getObject(bucketName, filePath);
            // 调用ossObject.getObjectContent获取文件输入流，可读取此输入流获取其内容。
            InputStream content = ossObject.getObjectContent();
            if (content != null) {
                result = true;
            }
            // 关闭OSSClient。
            ossClient.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (null != ossClient) {
                ossClient.shutdown();
            }
        }
        return result;
    }
}
