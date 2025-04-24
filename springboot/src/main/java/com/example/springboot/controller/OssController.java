package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.service.impl.OssService;
import com.example.springboot.utils.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 阿里云OSS文件操作控制器
 */
@RestController
@RequestMapping("/api/oss")
public class OssController {

    @Autowired(required = false)
    private OssService ossService;
    
    @Value("${files.upload.path:./uploads/}")
    private String uploadPath;
    
    @Value("${server.domain:http://localhost:8888}")
    private String domain;

    /**
     * 上传文件到OSS
     * @param file 上传的文件
     * @param directory 可选的目录路径，例如"images/avatar"
     * @return 文件URL和其他信息
     */
    @PostMapping("/upload")
    public Result uploadFile(@RequestParam("file") MultipartFile file, 
                             @RequestParam(value = "directory", defaultValue = "default") String directory) {
        try {
            String fileUrl;
            String fileName = UUID.randomUUID().toString() + getFileExtension(file.getOriginalFilename());
            
            // 构建完整的存储路径
            String path = directory + "/" + fileName;
            
            // 尝试使用OSS服务上传
            if (ossService != null) {
                try {
                    fileUrl = ossService.uploadFile(file, path);
                } catch (Exception e) {
                    // OSS上传失败，降级到本地文件存储
                    fileUrl = uploadToLocalStorage(file, directory, fileName);
                }
            } else {
                // OSS服务未配置，使用本地文件存储
                fileUrl = uploadToLocalStorage(file, directory, fileName);
            }
            
            // 构建响应数据
            Map<String, Object> data = new HashMap<>();
            data.put("url", fileUrl);
            data.put("filename", fileName);
            data.put("originalFilename", file.getOriginalFilename());
            data.put("size", file.getSize());
            data.put("contentType", file.getContentType());
            
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("500", "文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传到本地存储
     */
    private String uploadToLocalStorage(MultipartFile file, String directory, String fileName) throws IOException {
        // 确保目录存在
        String fullDirectory = uploadPath + directory;
        File dir = new File(fullDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // 保存文件
        String fullPath = fullDirectory + "/" + fileName;
        File destFile = new File(fullPath);
        file.transferTo(destFile);
        
        // 返回可访问的URL
        String relativePath = directory + "/" + fileName;
        return domain + "/uploads/" + relativePath;
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty() || !filename.contains(".")) {
            return ".png"; // 默认扩展名
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * 删除OSS上的文件
     * @param path 文件路径，相对于OSS bucket的路径
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    public Result deleteFile(@RequestParam("path") String path) {
        try {
            if (ossService != null) {
                ossService.deleteFile(path);
            } else {
                // 本地文件删除
                File file = new File(uploadPath + path);
                if (file.exists()) {
                    file.delete();
                }
            }
            return Result.success("文件删除成功");
        } catch (Exception e) {
            return Result.error("500", "文件删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传图片（特定接口）
     * @param file 图片文件
     * @return 图片URL
     */
    @PostMapping("/upload/image")
    public Result uploadImage(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "aichat");
    }
    
    /**
     * 上传头像（特定接口）
     * @param file 头像图片
     * @return 头像URL
     */
    @PostMapping("/upload/avatar")
    public Result uploadAvatar(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "aichat");
    }
} 