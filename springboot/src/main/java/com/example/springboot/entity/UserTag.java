package com.example.springboot.entity;

import lombok.Data;

/**
 * 用户标签实体类
 */
@Data
public class UserTag {
    
    private Integer id;
    private Integer userId;
    private String tagName;
} 