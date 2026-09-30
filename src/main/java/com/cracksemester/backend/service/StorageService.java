package com.cracksemester.backend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    void init();
    String storeFile(MultipartFile file);
    Resource loadFileAsResource(String fileName);
    void deleteFile(String fileName);
}
