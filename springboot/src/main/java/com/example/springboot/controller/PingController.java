package com.example.springboot.controller;

import com.example.springboot.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用于测试后端连接的控制器
 */
@RestController
public class PingController {

    /**
     * 简单的ping测试接口
     * @return 结果
     */
    @GetMapping("/ping")
    public Result ping() {
        return Result.success("pong");
    }
} 