package com.leyou.upload.service.impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.upload.service.UploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class UploadServiceImpl implements UploadService {

    private static final List<String> picType = Arrays.asList("image/png", "image/jpeg", "image/bmp");

    @Override
    public String upload2Nginx(MultipartFile file) {
        try {
            //1、非空判断
            if (file == null) {
                throw new LyException(ExceptionEnum.CANNOT_UPLOAD_NULL);
            }
            //2、校验文件类型
            if (!picType.contains(file.getContentType())) {
                throw new LyException(ExceptionEnum.TYPE_NOT_ALLOW);
            }
            //3、检验文件内容是否是图片
            BufferedImage read = ImageIO.read(file.getInputStream());
            if (read==null) {
                throw new LyException(ExceptionEnum.NOT_ONE_PICTURE);
            }
            //4、见文件写入nginx html文件夹
            //5、获取文件名
            StringBuilder path = new StringBuilder();
            Random random = new Random();
            path.append("images/");
            for (int i = 0; i < 2; i++) {
                int imgPath = random.nextInt(15);
                path.append(imgPath + "/");
            }
            String imgPath = path.toString();
            File imgfile = new File("D:/nginx-1.13.8/html/" + imgPath);
            if (!imgfile.exists()) {
                imgfile.mkdirs();
            }
            String originalFilename = file.getOriginalFilename();
            file.transferTo(new File(imgfile,originalFilename));
            //6、将文件url路径返回
            return "http://image.leyou.com/"+imgPath+originalFilename;
        } catch (IOException e) {
            throw new LyException(ExceptionEnum.COMMON_FAIL);
        }

    }
}
