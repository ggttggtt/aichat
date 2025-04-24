package com.example.springboot.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

@Service
public class OssService {

    @Resource
    private OSS ossClient;

    @Value("${aliyun.oss.bucket}")
    private String bucketName;

    /**
     * 上传文件到OSS
     * @param file 文件对象
     * @param path 存储路径（如 "images/2023/10/example.jpg"）
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String path) throws IOException {
        try {
            // 上传文件流
            ossClient.putObject(bucketName, path, file.getInputStream());
            // 生成访问URL（如果是私有Bucket，需要生成签名URL）
            return String.format("https://%s.oss-cn-chengdu.aliyuncs.com/%s", bucketName, path);
        } catch (OSSException | ClientException e) {
            throw new RuntimeException("OSS上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     * @param path 文件路径
     */
    public void deleteFile(String path) {
        ossClient.deleteObject(bucketName, path);
    }
}