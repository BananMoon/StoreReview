package com.review.storereview.service;

import org.springframework.web.multipart.MultipartFile;

public interface BaseImageProcessService {
    String createFileName(MultipartFile multipartFile);
    String uploadImage(MultipartFile uploadFile, String fileName);
    void deleteFile(String fileName);
}