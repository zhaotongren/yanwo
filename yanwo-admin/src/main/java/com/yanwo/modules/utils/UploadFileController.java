package com.yanwo.modules.utils;

import com.yanwo.utils.ImageUtils;
import com.yanwo.utils.OssUtil;
import com.yanwo.utils.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by cccc on 2018/5/28.
 */
@RestController
@RequestMapping("file")
public class UploadFileController {

    @Value("${sever.image.url}")
    private String severImageUrl;

    @PostMapping("/upload")
    @ApiOperation("图片上传")
    public R upload(MultipartFile file) {
        String image_url="";
        try {
            System.out.println("1-------------------------");
            if(file!=null) {
                System.out.println("2-------------------------");
                InputStream inputStream=file.getInputStream();
                String fileName =file.getOriginalFilename();
                String newFileName= UUID.randomUUID().toString().replace("-","")+fileName.substring(fileName.lastIndexOf("."));
                String imagePath = ImageUtils.getRandomImagePath();
                //上传图片到图片服务器
                OssUtil.uploadFile(imagePath,newFileName,inputStream);
                /*用来访问图片的路径*/
                image_url=severImageUrl.endsWith("/")?severImageUrl+imagePath+"/"+newFileName:severImageUrl+"/"+imagePath+"/"+newFileName;
                System.out.println("image_url-------------------------:"+image_url);
            }

        }catch (Exception e){

        }
        return R.ok(image_url);
    }

}
