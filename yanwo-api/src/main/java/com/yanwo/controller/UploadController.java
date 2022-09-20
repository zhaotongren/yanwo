package com.yanwo.controller;

import com.yanwo.utils.ImageUtils;
import com.yanwo.utils.OssUtil;
import com.yanwo.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
@Api(tags = "图片上传接口")
public class UploadController extends BaseController {

    @Value("${sever.image.url}")
    private String severImageUrl;

    @ResponseBody
    @RequestMapping(value="/imageFile")
    public R upload(@RequestParam(value = "file") MultipartFile file) {
        String image_url=null;
        try {
            if (file != null) {
                if (!isImage(file)) {
                    return R.error("请上传图片文件！");
                }
                InputStream inputStream =file.getInputStream();
                String fileName =file.getOriginalFilename();
                String newFileName = UUID.randomUUID().toString().replace("-", "") + fileName.substring(fileName.lastIndexOf("."));
                String imagePath = ImageUtils.getRandomImagePath();
                //上传图片到图片服务器
                OssUtil.uploadFile(imagePath, newFileName, inputStream);
                /*用来访问图片的路径*/
                image_url = severImageUrl.endsWith("/") ? severImageUrl + imagePath + "/" + newFileName : severImageUrl + "/" + imagePath + "/" + newFileName;
                inputStream.close();
            }
        }
        catch (Exception e){
        }
        return R.okput(image_url);
    }
    public Boolean isImage(MultipartFile file){
        String fileName = file.getOriginalFilename();
        int i = fileName.lastIndexOf('.');
        String fileType = fileName.substring(i+1);
        if(fileType.equals("JPG")||fileType.equals("jpg")||fileType.equals("png")||fileType.equals("gif")||fileType.equals("tif")||fileType.equals("bmp")||fileType.equals("jpeg")) {
            return true;
        }
        return false;
    }
}
