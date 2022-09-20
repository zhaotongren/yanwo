package com.yanwo.utils;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Created by Administrator on 2018/6/12.
 */
public class ImageUtils {

    public static String getRandomImagePath(){
        return "images/"+getRandomString(2)+ "/" +getRandomString(2)+ "/" +getRandomString(2);
    }
    public static String getRandomVideoPath(){
        return "videoes/"+getRandomString(2)+ "/" +getRandomString(2)+ "/" +getRandomString(2);
    }
    public static String getRandomWxcodePath(){
        return "wxcode/"+getRandomString(2)+ "/" +getRandomString(2)+ "/" +getRandomString(2);
    }


    private static String getRandomString(int length){
        String str="1234567890abcdef";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number =random.nextInt(16);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 缩小图片
     *
     * @param srcImageFile 目标图片
     * @param result 目标文件
     * @param scale 缩放比例
     * @param flag 放大(true)or缩小(false)
     */
    public final static void scale(String srcImageFile, String result,
                                   double scale, boolean flag) {
        try {
            BufferedImage src = ImageIO.read(new File(srcImageFile)); // 读入文件
            double width = src.getWidth(); // 得到源图宽
            double height = src.getHeight(); // 得到源图长
            if (flag) {// 放大
                width = width * scale;
                height = height * scale;
            } else {// 缩小
                width = width / scale;
                height = height / scale;
            }
            Image image = src.getScaledInstance((int)width, (int)height,
                    Image.SCALE_DEFAULT);
            BufferedImage tag = new BufferedImage((int)width, (int)height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            ImageIO.write(tag, "JPEG", new File(result));// 输出到文件流
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * 功能 :调整图片大小 开发：wuyechun 2011-7-22
     * @param srcImgPath 原图片路径
     * @param distImgPath  转换大小后图片路径
     * @param width   转换后图片宽度
     * @param height  转换后图片高度
     */
    public static void resizeImage(String srcImgPath, String distImgPath,
                                   int width, int height) throws IOException {

        File srcFile = new File(srcImgPath);
        Image srcImg = ImageIO.read(srcFile);
        BufferedImage buffImg = null;
        buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        buffImg.getGraphics().drawImage(
                srcImg.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0,
                0, null);

        ImageIO.write(buffImg, "JPEG", new File(distImgPath));

    }

}
