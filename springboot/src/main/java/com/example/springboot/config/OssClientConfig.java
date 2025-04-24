package com.example.springboot.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OSS客户端配置类
 */
@Configuration
public class OssClientConfig {

    private final AliyunOssConfig aliyunOssConfig;

    public OssClientConfig(AliyunOssConfig aliyunOssConfig) {
        this.aliyunOssConfig = aliyunOssConfig;
    }

    /**
     * 创建OSS客户端Bean
     * @return OSS客户端实例
     */
    @Bean
    public OSS ossClient() {
        return new OSSClientBuilder().build(
                aliyunOssConfig.getEndpoint(),
                aliyunOssConfig.getAccessKeyId(),
                aliyunOssConfig.getAccessKeySecret()
        );
    }
} 