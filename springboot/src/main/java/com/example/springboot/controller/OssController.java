package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.service.impl.OssService;
import com.example.springboot.utils.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云OSS文件操作控制器
 */
@RestController
@RequestMapping("/api/oss")
public class OssController {

    @Autowired
    private OssService ossService;

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
            // 生成唯一文件名
            String fileName = FileUploadUtils.extractFilename(file);
            
            // 构建完整的存储路径
            String path = directory + "/" + fileName;
            
            // 上传到OSS
            String fileUrl = ossService.uploadFile(file, path);
            
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
     * 删除OSS上的文件
     * @param path 文件路径，相对于OSS bucket的路径
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    public Result deleteFile(@RequestParam("path") String path) {
        try {
            ossService.deleteFile(path);
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