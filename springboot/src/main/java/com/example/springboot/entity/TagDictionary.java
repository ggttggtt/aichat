package com.example.springboot.entity;

import lombok.Data;

/**
 * 标签字典实体类
 */
@Data
public class TagDictionary {
    
    private Integer id;
    private String tagName;
    private String category;
} 