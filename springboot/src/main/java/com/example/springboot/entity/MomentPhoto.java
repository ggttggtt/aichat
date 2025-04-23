package com.example.springboot.entity;

import lombok.Data;

/**
 * 动态照片实体类
 */
@Data
public class MomentPhoto {
    
    private Integer id;
    private Integer momentId;
    private String photoUrl;
    private Integer orderNum;
} 