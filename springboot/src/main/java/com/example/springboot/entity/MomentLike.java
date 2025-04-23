package com.example.springboot.entity;

import lombok.Data;

import java.util.Date;

/**
 * 动态点赞实体类
 */
@Data
public class MomentLike {
    
    private Integer id;
    private Integer momentId;
    private Integer userId;
    private Date createTime;
} 