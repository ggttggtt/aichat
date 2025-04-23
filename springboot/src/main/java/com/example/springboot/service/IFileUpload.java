package com.example.springboot.service;

import org.springframework.web.multipart.MultipartFile;

public interface IFileUpload {
    String upload(MultipartFile file);
}
