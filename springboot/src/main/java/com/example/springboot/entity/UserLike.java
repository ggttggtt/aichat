package com.example.springboot.entity;

import lombok.Data;

import java.util.Date;

/**
 * 用户喜欢实体类
 */
@Data
public class UserLike {
    
    private Integer id;
    private Integer userId;
    private Integer likedUserId;
    private Date createTime;
    private Boolean status;  // true表示喜欢，false表示不喜欢
} 