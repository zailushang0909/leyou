package com.leyou.upload.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    String upload2Nginx(MultipartFile file);
}
