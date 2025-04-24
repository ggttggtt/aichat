package com.example.springboot.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.example.springboot.config.AliyunOssConfig;
import org.springframework.web.multipart.MultipartFile;

/**
 * 阿里云OSS文件上传工具类
 */
public class AliyunOssUploadUtils {

    private final AliyunOssConfig aliyunOssConfig;

    /**
     * 构造函数
     * @param aliyunOssConfig 阿里云OSS配置
     */
    public AliyunOssUploadUtils(AliyunOssConfig aliyunOssConfig) {
        this.aliyunOssConfig = aliyunOssConfig;
    }

    /**
     * 上传文件到阿里云OSS
     * @param file 要上传的文件
     * @return 文件访问URL
     * @throws Exception 上传过程中的异常
     */
    public String uploadFile(MultipartFile file) throws Exception {
        // 1. 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(
                aliyunOssConfig.getEndpoint(),
                aliyunOssConfig.getAccessKeyId(),
                aliyunOssConfig.getAccessKeySecret()
        );

        try {
            // 2. 生成文件名（防止重复）
            String fileName = FileUploadUtils.extractFilename(file);
            String filePath = aliyunOssConfig.getFilehost() + "/" + fileName;

            // 3. 上传文件
            ossClient.putObject(
                aliyunOssConfig.getBucketName(),
                filePath,
                file.getInputStream()
            );

            // 4. 返回文件访问URL
            return aliyunOssConfig.getUrl() + "/" + filePath;
        } finally {
            // 关闭OSSClient
            ossClient.shutdown();
        }
    }
}
