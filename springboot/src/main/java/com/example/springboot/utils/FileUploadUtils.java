package com.example.springboot.utils;

import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.UUID;

/**
 * 文件上传工具类
 */
public class FileUploadUtils {

    /**
     * 提取上传文件的文件名
     * 为防止文件名重复，会在原始文件名前添加UUID
     * @param file 上传的文件
     * @return 处理后的文件名
     */
    public static String extractFilename(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            // 如果没有原始文件名，生成一个临时文件名
            return UUID.randomUUID().toString();
        }
        
        // 处理可能的路径分隔符，只保留最后的文件名部分
        int lastUnixPos = originalFilename.lastIndexOf('/');
        int lastWindowsPos = originalFilename.lastIndexOf('\\');
        int lastSeparatorPos = Math.max(lastUnixPos, lastWindowsPos);
        
        String fileName = lastSeparatorPos > 0 ? originalFilename.substring(lastSeparatorPos + 1) : originalFilename;
        
        // 获取文件扩展名
        String extension = "";
        int lastDotPos = fileName.lastIndexOf('.');
        if (lastDotPos > 0) {
            extension = fileName.substring(lastDotPos);
            fileName = fileName.substring(0, lastDotPos);
        }
        
        // 生成UUID前缀，确保文件名唯一
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        
        // 返回UUID+原始文件名+扩展名
        return uuid + "_" + fileName + extension;
    }
} 