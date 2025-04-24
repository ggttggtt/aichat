package com.example.springboot.entity;

import lombok.Data;

import java.util.Date;

/**
 * 用户喜欢关系实体类
 */
@Data
public class Like {
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 被喜欢的用户ID
     */
    private Integer likedUserId;
    
    /**
     * 类型：0-不喜欢，1-喜欢
     */
    private Integer type;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
} 