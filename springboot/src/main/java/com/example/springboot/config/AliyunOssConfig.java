package com.example.springboot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云OSS配置类
 */
@Configuration
public class AliyunOssConfig {
    
    /**
     * 端点（Region）
     */
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    
    /**
     * 访问密钥ID
     */
    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;
    
    /**
     * 访问密钥Secret
     */
    @Value("${aliyun.oss.secret}")
    private String accessKeySecret;
    
    /**
     * 存储桶名称
     */
    @Value("${aliyun.oss.bucket}")
    private String bucketName;
    
    /**
     * 文件存储路径前缀
     */
    @Value("${aliyun.oss.filehost}")
    private String filehost;
    
    /**
     * 访问URL
     */
    @Value("${aliyun.oss.url}")
    private String url;

    public String getEndpoint() {
        return endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getFilehost() {
        return filehost;
    }
    
    public String getUrl() {
        return url;
    }
}
