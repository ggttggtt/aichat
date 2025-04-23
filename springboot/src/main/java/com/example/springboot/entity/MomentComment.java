package com.example.springboot.entity;

import lombok.Data;

import java.util.Date;

/**
 * 动态评论实体类
 */
@Data
public class MomentComment {
    
    private Integer id;
    private Integer momentId;
    private Integer userId;
    private String content;
    private Date createTime;
    
    // 非数据库字段
    private UserProfile user;
    private String formattedTime;
} 