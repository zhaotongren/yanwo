package com.yanwo.modules.controller.sys;

import com.yanwo.utils.FtpUtil;
import com.yanwo.utils.ImageUtils;
import com.yanwo.utils.OssUtil;
import com.yanwo.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

/**
 * zlq
 * 文件上传通用接口
 * Created by Administrator on 2018/7/9 0009.
 */
@RestController
@RequestMapping("/upload")
public class UploadController {
    private static final Logger log = LoggerFactory.getLogger(UploadController.class);

    /*host 例如192.168.9.108*/
    @Value("${ftp.host}")
    private  String host;

    /*端口  21*/
    @Value("${ftp.port}")
    private String port;

    @Value("${ftp.user}")
    private String username;

    @Value("${ftp.password}")
    private String password;

    @Value("${ftp.basePath}")
    private String basePath;

    @Value("${sever.image.url}")
    private String severImageUrl;
    @RequestMapping(value="/uploadFtp",method= RequestMethod.POST)
    public R uploadFtp(MultipartFile file) {
        String image_url="";
        try {
            if(file!=null) {
                InputStream inputStream=file.getInputStream();
                String fileName =file.getOriginalFilename();
                String newFileName= UUID.randomUUID().toString().replace("-","")+fileName.substring(fileName.lastIndexOf("."));
                String imagePath = ImageUtils.getRandomImagePath();
                //上传图片到图片服务器
                FtpUtil.uploadFile(host, Integer.valueOf(port), username, password, basePath, imagePath, newFileName, inputStream);
                /*用来访问图片的路径*/
                image_url=severImageUrl.endsWith("/")?severImageUrl+imagePath+"/"+newFileName:severImageUrl+"/"+imagePath+"/"+newFileName;
            }

        }catch (Exception e){

        }
        return R.ok(image_url);
    }

    @RequestMapping(value="/upload",method= RequestMethod.POST)
    public R upload(MultipartFile file) {
        String image_url="";
        try {
            if(file!=null) {
                InputStream inputStream=file.getInputStream();
                String fileName =file.getOriginalFilename();
                String newFileName= UUID.randomUUID().toString().replace("-","")+fileName.substring(fileName.lastIndexOf("."));
                String imagePath = ImageUtils.getRandomImagePath();
                //上传图片到图片服务器
                OssUtil.uploadFile(imagePath,newFileName,inputStream);
                /*用来访问图片的路径*/
                image_url=severImageUrl.endsWith("/")?severImageUrl+imagePath+"/"+newFileName:severImageUrl+"/"+imagePath+"/"+newFileName;
            }

        }catch (Exception e){
            log.info("上传图片异常",e);
            e.printStackTrace();
        }
        return R.ok(image_url);
    }

}
