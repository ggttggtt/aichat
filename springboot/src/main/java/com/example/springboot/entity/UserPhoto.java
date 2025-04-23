package com.example.springboot.entity;

import lombok.Data;

import java.util.Date;

/**
 * 用户照片实体类
 */
@Data
public class UserPhoto {
    
    private Integer id;
    private Integer userId;
    private String photoUrl;
    private Boolean isAvatar;
    private Integer orderNum;
    private Date uploadTime;
} 